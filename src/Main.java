import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by Lorenzo Toso on 28.11.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Reader in = new FileReader(args[0]);

        CSVParser parser = new CSVParser(in , CSVFormat.MYSQL);
        for (CSVRecord record : parser) {
            CSVRecord newRecord = reformat(record);
        }
    }

    private static CSVRecord reformat(CSVRecord record) {
        for(String val : record)
        {
            System.out.println(val);
        }
        return record;
    }
}
