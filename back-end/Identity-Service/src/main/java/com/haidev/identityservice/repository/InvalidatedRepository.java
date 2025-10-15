package com.haidev.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haidev.identityservice.entity.InvalidatedToken;

@Repository
public interface InvalidatedRepository extends JpaRepository<InvalidatedToken, String> {}
