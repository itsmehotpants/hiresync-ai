package com.hiresync.core.repository;

import com.hiresync.core.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    List<Contact> findByUserIdOrderByNameAsc(String userId);
    Optional<Contact> findByIdAndUserId(String id, String userId);
}
