import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Main {
    private static final int chunkSize = 5;


    public static void main(String[] args) throws IOException {

        Reader in = new FileReader(args[0]);
        Appendable out = new FileWriter(args[1]);

        CSVParser parser = new CSVParser(in , CSVFormat.RFC4180.withHeader());
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180.withHeader());

        List<CSVRecord> csvRecords = parser.getRecords();
        List<Record> records = Record.from(csvRecords);

        List<Record[]> recordChunks = splitIntoChunks(records, chunkSize);

        int i = 0;
        List<Record> cleanRecords = new ArrayList<>();
        for (Record[] recordChunk : recordChunks) {
            clean(recordChunk);
            Collections.addAll(cleanRecords, recordChunk);
            System.out.println(i+=5);
        }

        cleanRecords.forEach((r) -> r.setHeaderMap(parser.getHeaderMap()));
        printer.printRecord(parser.getHeaderMap().keySet());
        printer.printRecords(cleanRecords);
        printer.close();

    }

    private static List<Record[]> splitIntoChunks(List<Record> records, int chunkSize) {
        List<Record[]> splitList = new ArrayList<>();

        for(int i = 0; i < records.size(); i+=chunkSize)
        {
            Record[] chunk = new Record[Math.min(chunkSize, records.size() - i)];
            for(int j = 0; j < chunkSize && i + j < records.size(); j++)
            {
                chunk[j] = records.get(i+j);
            }
            splitList.add(chunk);
        }

        return splitList;
    }



    private static Record[] clean(Record[] recordChunk) throws IOException {


        PlaceChunk places = new PlaceChunk();

        for (Record record : recordChunk) {
            places.add(record.getPlace());
        }
        places.lookupMissingFields();
        for (int i = 0; i < places.getPlaces().size(); i++) {
            recordChunk[i].setPlace(places.getPlaces().get(i));
        }
        for (Record record : recordChunk) {
            record.cleanState();
            record.cleanZip();
            record.cleanSSN();
            record.cleanNonNumericValues();
        }
        
        return recordChunk;
    }


}
