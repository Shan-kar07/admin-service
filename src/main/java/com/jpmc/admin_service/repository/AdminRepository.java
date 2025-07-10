package com.jpmc.admin_service.repository;

import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.enums.Status; // Import the Status enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Best practice to add @Repository

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    List<Admin> findByStatus(Status status);
    Optional<Admin> findByEmail(String email);

}