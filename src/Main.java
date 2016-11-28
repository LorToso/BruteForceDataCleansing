import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Lorenzo Toso on 28.11.2016.
 */
public class Main {
    public static final String UserID = "738TECHN3158";

    public static void main(String[] args) throws IOException {
//        Reader in = new FileReader(args[0]);
//
//        Appendable out = new FileWriter(args[1]);
//
//        CSVParser parser = new CSVParser(in , CSVFormat.MYSQL.withDelimiter(',').withHeader());
//        CSVPrinter printer = new CSVPrinter(out, CSVFormat.MYSQL.withDelimiter(',').withHeader());
//
//        for (CSVRecord record : parser) {
//            CSVRecord newRecord = reformat(record);
//        }
        String[] zips = {"95405", "72846", "77316"};
        getInfoFromZip(zips);


    }

    private static List<Place> getInfoFromZip(String[] zips) throws IOException{
        String finalRequest = generateQuery(zips);
        Document doc = Jsoup.connect(finalRequest).get();
        return extractPlacesFromDocument(doc);
    }

    private static String generateQuery(String[] zips) {
        String url = "http://production.shippingapis.com/ShippingAPI.dll?API=CityStateLookup&XML=";
        String xmlStart = "<CityStateLookupRequest USERID=\"" + UserID + "\">";
        String xmlEnd = "</CityStateLookupRequest>";
        StringBuilder finalRequest = new StringBuilder();
        finalRequest.append(url).append(xmlStart);
        for (int i = 0; i < zips.length; i++) {
            finalRequest.append("<ZipCode ID=\"").append(i).append("\">").append("<Zip5>").append(zips[i]).append("</Zip5></ZipCode>");
        }
        finalRequest.append(xmlEnd);
        return finalRequest.toString();
    }

    private static List<Place> extractPlacesFromDocument(Document doc) {
        Elements body = doc.select("ZipCode");

        List<Place> places = new ArrayList<>();


        for (Element element : body) {
            Place p = new Place();

            p.zip = element.select("Zip5").text();
            p.city = element.select("city").text();
            p.state = element.select("state").text();
            places.add(p);
        }

        return places;
    }

    private static CSVRecord reformat(CSVRecord record) throws IOException {
        Map<String, String > recordAsMap = record.toMap();
        Map<String, String > cleanRecordAsMap = record.toMap();

        cleanNonNumericValues(recordAsMap, cleanRecordAsMap);
        cleanAddressData(recordAsMap, cleanRecordAsMap);
        cleanSSN(recordAsMap, cleanRecordAsMap);
        
        return generateNewRecord(cleanRecordAsMap, record);
    }

    private static void cleanSSN(Map<String, String> recordAsMap, Map<String, String> cleanRecordAsMap) {
        String SSN = recordAsMap.get("SSN");
        if(!StringUtils.isNumeric(SSN))
            SSN = "";
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
