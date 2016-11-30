import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Record implements Iterable<String>{
    private static final int zipLength = 5;
    private static final String defaultZIP = "00000";
    private static final String DefaultSSN = "00000000";
    private Map<String, String> values = new HashMap<>();
    private Map<String, Integer> headerMap = null;


    public void setHeaderMap(Map<String, Integer> headerMap) {
        this.headerMap = headerMap;
    }

    public static List<Record> from(List<CSVRecord> csvRecords)
    {
        return csvRecords.stream().map(Record::from).collect(Collectors.toList());
    }
    public static Record from(CSVRecord csvRecord)
    {
        Record r = new Record();
        r.values = csvRecord.toMap();
        return r;
    }
    public String get(String key)
    {
        if(!values.containsKey(key))
            throw new RuntimeException("Key does not exist!");
        return values.get(key);
    }
    public void set(String key, String value)
    {
        values.put(key, value);
    }


    public void cleanNonNumericValues()
    {
        for(Map.Entry<String,String> tuple : values.entrySet())
        {
            String value = tuple.getValue();
            String key = tuple.getKey();

            value = capitalizeIfNecessary(key, value);

            values.put(key, value);
        }
    }

    private static String capitalizeIfNecessary(String key, String value) {

        if(key.equals("DOB(String)"))
            return value;
        if(key.equals("PO BOX(String)"))
            return value;
        if(key.equals("POCityStateZip(String)"))
            return value;

        if(!StringUtils.isNumeric(value))
        {
            return WordUtils.capitalize(value.toLowerCase());
        }
        return value;

    }
    public void cleanSSN() {
        String SSN = values.get("SSN(String)");
        String cleanSSN = makeNumeric(SSN);

        if(!StringUtils.isNumeric(cleanSSN) || SSN.isEmpty())
            cleanSSN = DefaultSSN;
        else if(cleanSSN.length() > 10)
            cleanSSN = cleanSSN.substring(0,10);
        else if(cleanSSN.length() < 8)
            cleanSSN = DefaultSSN;
        values.put("SSN(String)", cleanSSN);
    }

    private static String makeNumeric(String SSN) {
        StringBuilder cleanSSNBuilder = new StringBuilder();
        for (int i = 0; i < SSN.length(); i++) {
            char c = SSN.charAt(i);
            if (StringUtils.isNumeric("" + c))
                cleanSSNBuilder.append(c);
        }
        return cleanSSNBuilder.toString();
    }

    public void cleanState(){
        String state = get("State(String)");
        if(state.length() != 2)
            state = "";
        set("State(String)", state);
    }
    public void cleanZip(boolean strict){
        String zip = get("ZIP(String)");
        if(!StringUtils.isNumeric(zip))
            zip = makeNumeric(zip);
        if(strict)
        {
            if(zip.length() > zipLength)
                zip = zip.substring(0, zipLength);
            if(zip.length() < zipLength)
                zip = defaultZIP;
        }
        set("ZIP(String)", zip);
    }
    public void setPlace(Place place)
    {
        set("ZIP(String)", place.zip);
        set("City(String)", place.city);
        set("State(String)", place.state);
        set("Address(String)", place.address);
    }

    public Place getPlace() {

        Place p = new Place();
        p.state = get("State(String)");
        p.city = get("City(String)");
        p.address = get("Address(String)");
        p.zip = get("ZIP(String)");
        return p;
    }

    @Override
    public Iterator<String> iterator() {
        return new RecordIterator(this, headerMap);
    }
}
