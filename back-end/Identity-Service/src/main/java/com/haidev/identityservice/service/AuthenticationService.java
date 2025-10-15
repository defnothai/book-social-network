package com.haidev.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haidev.identityservice.dto.request.authentication.AuthenticationRequest;
import com.haidev.identityservice.dto.request.authentication.IntrospectRequest;
import com.haidev.identityservice.dto.request.authentication.LogoutRequest;
import com.haidev.identityservice.dto.request.authentication.RefreshRequest;
import com.haidev.identityservice.dto.response.AuthenticationResponse;
import com.haidev.identityservice.dto.response.IntrospectResponse;
import com.haidev.identityservice.entity.InvalidatedToken;
import com.haidev.identityservice.entity.User;
import com.haidev.identityservice.exception.AppException;
import com.haidev.identityservice.exception.ErrorCode;
import com.haidev.identityservice.repository.InvalidatedRepository;
import com.haidev.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedRepository invalidatedRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    Long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    Long REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private String generateToken(User user) {
        // header chứa thông tin thuật toán dùng để hash
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // payload chứa nội dung thông tin gửi đi (claim set) trong token: subject username, userid, ...
        // có thể gọi là payload thô
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("haidev.com") // định danh ai là người tạo token
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        // từ claim set thành json object và bọc thành object Payload theo định dạng mà JWSObject yêu
        // cầu
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        // Ghép Header + Payload lại thành một object chuẩn để chuẩn bị ký
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            // ký: hash content với key
            // MACSigner là class của Nimbus để tạo chữ ký HMAC:
            //      cần một secret key ở dạng byte để hoạt động
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize(); // chuyển thành string
        } catch (JOSEException e) {
            log.error("Error when generate token", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        if (!user.getRoles().isEmpty()) {
            user.getRoles().forEach(role -> {
                joiner.add("ROLE_" + role.getName());
                if (!role.getPermissions().isEmpty()) {
                    role.getPermissions().forEach(permission -> {
                        joiner.add(permission.getName());
                    });
                }
            });
        }
        return joiner.toString();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        try {
            SignedJWT signedJWT = verifyToken(token, true);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiredAt(expirationTime).build();
            invalidatedRepository.save(invalidatedToken);
        } catch (AppException e) { // token có hết hạn hay không thì vẫn logout được
            log.info("Token expired");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        // MACVerifier là class của Nimbus để verify Token mà ký bằng HMAC
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        // từ token chuyển thành object để dễ xử lý
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        // lấy header + payload trong JWT, chạy qua thuật toán (ví dụ HS256 = HMAC-SHA256) cùng với
        // secret key.
        // Sau đó so sánh kết quả hash đó với signature có sẵn trong token.
        var verified = signedJWT.verify(verifier);

        if (!verified || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getToken(), true);
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiredAt(expirationTime).build();
        invalidatedRepository.save(invalidatedToken);

        String username = signedJWT.getJWTClaimsSet().getSubject();
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();

        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().isValid(isValid).build();
    }
}
