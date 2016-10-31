package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, UUID> {}
