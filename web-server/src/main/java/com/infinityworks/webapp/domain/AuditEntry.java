package com.infinityworks.webapp.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "audit")
public class AuditEntry extends BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date added;

    @Column(nullable = false)
    private String event;

    @Column(nullable = false)
    private String category;

    private String custom1;
    private String custom2;
    private String custom3;

    public static AuditEntry createGotvCard(String wardCode, String username, String numStreets) {
        AuditEntry e = new AuditEntry();
        e.setAdded(new Date());
        e.setCategory("GOTV");
        e.setEvent("Created GOTV canvass card");
        e.setCustom1(wardCode);
        e.setCustom2(username);
        e.setCustom3(numStreets);
        return e;
    }

    public static AuditEntry recordVoted(String ern, String username) {
        AuditEntry e = new AuditEntry();
        e.setAdded(new Date());
        e.setCategory("GOTV");
        e.setEvent("Recorded voted");
        e.setCustom1(ern);
        e.setCustom2(username);
        return e;
    }

    public static AuditEntry createCanvassCard(String wardCode, String username, String numStreets) {
        AuditEntry e = new AuditEntry();
        e.setAdded(new Date());
        e.setCategory("Canvass");
        e.setEvent("Created canvass card");
        e.setCustom1(wardCode);
        e.setCustom2(username);
        e.setCustom3(numStreets);
        return e;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }
}
