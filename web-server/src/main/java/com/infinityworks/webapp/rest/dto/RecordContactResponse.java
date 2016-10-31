package com.infinityworks.webapp.rest.dto;

import com.infinityworks.webapp.domain.Ern;

import java.util.UUID;

public class RecordContactResponse {
    private final UUID localId;
    private final Ern ern;
    private final UUID contactId;

    public RecordContactResponse(UUID localId, Ern ern, UUID contactId) {
        this.localId = localId;
        this.ern = ern;
        this.contactId = contactId;
    }

    public UUID getLocalId() {
        return localId;
    }

    public Ern getErn() {
        return ern;
    }

    public UUID getContactId() {
        return contactId;
    }
}
