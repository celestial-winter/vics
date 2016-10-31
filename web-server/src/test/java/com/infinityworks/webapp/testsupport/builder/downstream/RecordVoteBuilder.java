package com.infinityworks.webapp.testsupport.builder.downstream;

import com.infinityworks.webapp.rest.dto.RecordVoteRequest;

public class RecordVoteBuilder {
    private String wardCode;
    private String ern;

    public static RecordVoteBuilder recordVote() {
        return new RecordVoteBuilder().withDefaults();
    }

    public RecordVoteBuilder withDefaults() {
        withWardCode("E05001221").withErn("E05001221-PD-123-4");
        return this;
    }

    public RecordVoteBuilder withWardCode(String wardCode) {
        this.wardCode = wardCode;
        return this;
    }

    public RecordVoteBuilder withErn(String ern) {
        this.ern = ern;
        return this;
    }

    public RecordVoteRequest build() {
        return new RecordVoteRequest(wardCode, ern);
    }
}