
public class Place {
    private static final String defaultZip = "00000";
    String zip;
    String city;
    String state;
    String address;

    public boolean hasValidZip() {
        return zip != null && zip.length() == 5 && !zip.equals(defaultZip);
    }
}
