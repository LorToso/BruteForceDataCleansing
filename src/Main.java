import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Created by Lorenzo Toso on 28.11.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Reader in = new FileReader(args[0]);

        CSVParser parser = new CSVParser(in , CSVFormat.MYSQL.withDelimiter(',').withHeader());
        for (CSVRecord record : parser) {
            CSVRecord newRecord = reformat(record);
        }

    }

    private static CSVRecord reformat(CSVRecord record) {


        Map<String, String > recordAsMap = record.toMap();
        Map<String, String > cleanRecordAsMap = record.toMap();

        for(Map.Entry<String,String> tuple : recordAsMap.entrySet())
        {
            String value = tuple.getValue();
            String key = tuple.getKey();

            value = capitalizeIfNecessary(key, value);

            cleanRecordAsMap.put(key, value);
        }

        return generateNewRecord(cleanRecordAsMap, record);
    }

    private static String capitalizeIfNecessary(String key, String value) {

        if(key.equals("DOB"))
            return value;
        if(key.equals("PO BOX"))
            return value;
        if(key.equals("POCityStateZi"))
            return value;

        if(!StringUtils.isNumeric(value))
        {
            return StringUtils.capitalize(value);
        }
        return value;

    }

    private static CSVRecord generateNewRecord(Map<String, String> cleanRecordAsMap, CSVRecord record) {
        return null;
    }
}
