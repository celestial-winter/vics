package com.infinityworks.tools.dataprep;

import com.infinityworks.tools.dataprep.csv.CsvParser;
import com.ninja_squad.dbsetup.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Parses wards and constituencies csv file and inserts records into a table
 */
public class ConstituenciesImporter {
    private static final Logger log = LoggerFactory.getLogger(ConstituenciesImporter.class);
    private static final String CSV_FILE = "const-ward.csv";

    public static void main(String... args) throws IOException {
        DatabaseOperationExecutor setup = new DatabaseOperationExecutor(new DbConfig.Default());

        CsvParser csvParser = new CsvParser(CSV_FILE);

        List<Constituency> constituencies = new ArrayList<>();
        List<WardCsvRecord> wardRecords = csvParser.parseContent(record -> new WardExtractor().apply(record));
        wardRecords.stream()
                .collect(groupingBy(WardCsvRecord::getConstituencyCode,
                         groupingBy(WardCsvRecord::getConstituencyName)))
                .forEach((constCode, constNameKey) -> {
                    Constituency constituency = new Constituency(constCode, constNameKey.keySet().iterator().next());
                    log.info(constituency.getName());
                    constituencies.add(constituency);
                });

        log.info("Generating constituencies...");

        Operation insertConstituencies = sequenceOf(
                deleteAllFrom("wards"),
                deleteAllFrom("constituencies"),
                DatabaseOperation.insertConstituencies(constituencies));
        setup.execute(insertConstituencies);

        List<Ward> wards = wardRecords.stream()
                .map(wardRecord -> {
                    Optional<Constituency> constituencyForWard = constituencies.stream()
                            .filter(constituency -> Objects.equals(constituency.getName(), wardRecord.getConstituencyName())
                                    && Objects.equals(constituency.getCode(), wardRecord.getConstituencyCode()))
                            .findFirst();
                    return new Ward(wardRecord.getWardName(), wardRecord.getWardCode(), constituencyForWard.get().getId());
                })
                .collect(toList());

        log.info("Generating wards...");

        Operation insertWards = sequenceOf(DatabaseOperation.insertWards(wards));
        setup.execute(insertWards);

        log.info("Done.");
    }
}
