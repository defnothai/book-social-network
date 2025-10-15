package com.haidev.identityservice.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvalidatedToken {
    @Id
    String id;

    Date expiredAt;
}
