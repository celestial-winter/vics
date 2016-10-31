package com.infinityworks.tools

import com.google.common.collect.ArrayListMultimap
import com.infinityworks.tools.dataprep.csv.CsvParser
import org.apache.commons.csv.CSVRecord
import java.util.function.Function

data class Row(val constituencyCode: String, val regionName: String, val regionCode: String)

fun main(args: Array<String>) {

    val rowMapper = Function<CSVRecord, Row> {
        row -> Row(constituencyCode = row[1], regionName = row[6], regionCode = row[5])
    }

    val parser = CsvParser("wards_const_regions.csv")
    val content = parser.parseContent(rowMapper)

    val constituencyRegionMapping = ArrayListMultimap.create<String, Row>()
    content.forEachIndexed { i, row ->
        if (i != 0) {
            constituencyRegionMapping.put(row.constituencyCode, row)
        }
    }

//    val parser = CsvParser("wards_const_regions.csv")

    println(content[2])
}
