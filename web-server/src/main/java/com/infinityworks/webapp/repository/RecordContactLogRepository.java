package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.RecordContactLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecordContactLogRepository extends JpaRepository<RecordContactLog, UUID> {}
