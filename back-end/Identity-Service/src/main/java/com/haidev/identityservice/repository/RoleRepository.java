package com.haidev.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haidev.identityservice.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
