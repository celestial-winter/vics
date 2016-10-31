package com.infinityworks.tools.dataprep;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

class DatabaseOperationExecutor implements DatabaseOperation {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public DatabaseOperationExecutor(DbConfig dbConfig) {
        this.dbUrl = dbConfig.getDbUrl();
        this.dbUser = dbConfig.getDbUser();
        this.dbPassword = dbConfig.getDbPassword();
    }

    public void execute(Operation... operations) {
        DriverManagerDestination destination =
                new DriverManagerDestination(dbUrl, dbUser, dbPassword);
        DbSetup dbSetup = new DbSetup(destination, Operations.sequenceOf(operations));
        dbSetup.launch();
    }
}
