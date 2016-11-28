import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Record {
    private static final String DefaultSSN = "00000000";
    private Map<String, String> values = new HashMap<>();



    public static List<CSVRecord> toCSV(List<Record> records, CSVParser parser)
    {
        throw new NotImplementedException("Noch nicht fertig");
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
    public void cleanSSN() {
        String SSN = values.get("SSN");
        if(!StringUtils.isNumeric(SSN) || SSN.isEmpty())
            SSN = DefaultSSN;
        else if(SSN.length() > 10)
            SSN = SSN.substring(0,9);
        else if(SSN.length() < 8)
            SSN = DefaultSSN;
        values.put("SSN", SSN);
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
}
