package com.infinityworks.webapp.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "record_contact_log")
public class RecordContactLog {
    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private User user;

    @JoinColumn(name = "wards_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Ward ward;

    private String ern;

    @Temporal(TemporalType.TIMESTAMP)
    private Date added;

    @Enumerated(EnumType.STRING)
    private RecordContactOperationType operation;

    public RecordContactLog() {
        // required by hibernate
    }

    public RecordContactLog(User user, Ward ward, String ern) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.ward = ward;
        this.ern = ern;
        this.added = new Date();
        this.setOperation(RecordContactOperationType.CREATE);
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getErn() {
        return ern;
    }

    public void setErn(String ern) {
        this.ern = ern;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date date) {
        this.added = date;
    }

    public RecordContactOperationType getOperation() {
        return operation;
    }

    public void setOperation(RecordContactOperationType operation) {
        this.operation = operation;
    }

    public UUID getId() {
        return id;
    }
}
