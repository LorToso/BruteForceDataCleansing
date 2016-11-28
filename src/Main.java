import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Lorenzo Toso on 28.11.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Reader in = new FileReader(args[0]);

        Appendable out = new FileWriter(args[1]);

        CSVParser parser = new CSVParser(in , CSVFormat.MYSQL.withDelimiter(',').withHeader());
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.MYSQL.withDelimiter(',').withHeader());

        for (CSVRecord record : parser) {
            CSVRecord newRecord = reformat(record);
        }

    }

    private static CSVRecord reformat(CSVRecord record) throws IOException {
        Map<String, String > recordAsMap = record.toMap();
        Map<String, String > cleanRecordAsMap = record.toMap();

        cleanNonNumericValues(recordAsMap, cleanRecordAsMap);
        cleanAddressData(recordAsMap, cleanRecordAsMap);

        return generateNewRecord(cleanRecordAsMap, record);
    }

    private static void cleanAddressData(Map<String, String> recordAsMap, Map<String, String> cleanRecordAsMap) throws IOException {

        String city = recordAsMap.get("City");
        String state = recordAsMap.get("State");


        String URI = "https://tools.usps.com/go/ZipLookupResultsAction!input.action?resultMode=1&companyName=&address1=&address2=&city=" + city + "&state=" + state + "&urbanCode=&postalCode=&zip=";

        Document doc = Jsoup.connect(URI).get();

        Elements allZips = doc.select(".zip");
        List<String> zips = allZips.stream().map(Element::data).collect(Collectors.toList());



    }

    private static void cleanNonNumericValues(Map<String, String> recordAsMap, Map<String, String> cleanRecordAsMap) {
        for(Map.Entry<String,String> tuple : recordAsMap.entrySet())
        {
            String value = tuple.getValue();
            String key = tuple.getKey();

            value = capitalizeIfNecessary(key, value);

            cleanRecordAsMap.put(key, value);
        }
    }

    private static String capitalizeIfNecessary(String key, String value) {

        if(key.equals("DOB"))
            return value;
        if(key.equals("PO BOX"))
            return value;
        if(key.equals("POCityStateZip"))
            return value;

        if(!StringUtils.isNumeric(value))
        {
            return StringUtils.capitalize(value);
        }
        return value;

    }

    private static CSVRecord generateNewRecord(Map<String, String> cleanRecordAsMap, CSVRecord record) {
        throw new NotImplementedException("Hier war ich noch nicht!");
    }
}
