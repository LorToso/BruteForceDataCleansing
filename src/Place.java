
public class Place {
    private static final String defaultZip = "00000";
    String zip;
    String city;
    String state;
    String address;

    public void cleanZipCode()
    {
        if(zip == null)
            zip = defaultZip;
        zip = getLongestNumberSequence(zip);
        if(zip.length() != 5)
            zip = defaultZip;
    }
    private static String getLongestNumberSequence(String str)
    {
        String longest = "";
        int i = 0;
        while (i < str.length()) {
            while (i < str.length() && !Character.isDigit(str.charAt(i))) {
                ++i;
            }
            int start = i;
            while (i < str.length() && Character.isDigit(str.charAt(i))) {
                ++i;
            }
            if (i - start > longest.length()) {
                longest = str.substring(start, i);
            }
        }
        return longest;
    }

    public boolean hasValidZip() {
        return zip != null && zip.length() == 5 && !zip.equals(defaultZip);
    }
}
