package com.infinityworks.canvass.pafstub;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * App for running a stub PAF api (for demo purposes only)
 */
public class PafServerRunner {
    private static Runnable pafServer = () -> {
        PafServerStub pafApiStub = new PafServerStub(9002);
        pafApiStub.start();
        try {
            pafApiStub.willReturnStreetsByWard("E05001221");
            pafApiStub.willRecordVoterVoted();
            pafApiStub.willReturnVotersByWardByTownAndByStreet("E05000403", "Kingston");
            pafApiStub.willSearchVoters("kt25bu", "fletcher", "E05001221");
            pafApiStub.willReturnWardStats("E05000403");
            pafApiStub.willReturnTheConstituenciesStats();
            pafApiStub.willReturnThePostcodeMetaData("KT2 5BU");
        } catch (IOException e) {
            throw new IllegalStateException("Could not start PAF mock server");
        }
    };

    public static void main(String... args) {
        Executors.newSingleThreadExecutor().execute(pafServer);
    }
}
