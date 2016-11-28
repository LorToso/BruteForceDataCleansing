import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PlaceChunk {
    private List<Place> places = new ArrayList<>();
    private static final String UserID = "738TECHN3158";

    public void add(Place place)
    {
        places.add(place);
    }


    public void lookupMissingFields()
    {
        // Clean Zip
        // Generate City&State from ZIP
        // Generate ZIP from City&State&Address (also formats address)

    }
    public List<Place> getPlaces()
    {
        return places;
    }


    private static void cleanFromValidZip(Record[] records) throws IOException {

        String[] zipCodes = new String[records.length];
        for (int i = 0; i < records.length; i++) {
            zipCodes[i] = records[i].get("ZIP(String)");
        }
        List<Place> places = getInfoFromZip(zipCodes);
        for (int i = 0; i < records.length; i++) {
            records[i].setPlace(places.get(i));
        }
    }
    private static List<Place> getInfoFromZip(String[] zips) throws IOException{
        String finalRequest = generateZipQuery(zips);
        Document doc = Jsoup.connect(finalRequest).get();
        return extractPlacesFromDocument(doc);
    }

    private static String generateZipQuery(String[] zips) {
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
}
