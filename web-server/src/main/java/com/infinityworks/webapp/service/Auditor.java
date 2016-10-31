package com.infinityworks.webapp.service;

import com.infinityworks.webapp.domain.AuditEntry;
import com.infinityworks.webapp.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class Auditor {

    private final AuditRepository auditRepository;

    @Autowired
    public Auditor(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Async
    public void audit(AuditEntry auditEntry) {
        auditRepository.save(auditEntry);
    }
}
