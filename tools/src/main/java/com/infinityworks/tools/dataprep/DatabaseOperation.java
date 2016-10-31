package com.infinityworks.tools.dataprep;

import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;

public interface DatabaseOperation {

    static Operation insertWards(List<Ward> wards) {
        Insert.Builder columns = insertInto("wards")
                .columns("id", "name", "code", "constituency_id");

        wards.stream().forEach(w -> columns.values(w.getId(), w.getWardName(), w.getWardCode(), w.getConstituency()));

        return columns.build();
    }

    static Operation insertConstituencies(List<Constituency> constituency) {
        Insert.Builder columns = insertInto("constituencies")
                .columns("id", "name", "code");

        constituency.stream().forEach(w -> columns.values(w.getId(), w.getName(), w.getCode()));

        return columns.build();
    }
}
