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


    public void lookupMissingFields() throws IOException {
        cleanZipCodes();
        generateCityAndStateFromZip();
        generateZipFromCityStateAndAddress();
    }

    private void generateZipFromCityStateAndAddress() throws IOException {
        places = generateZipFromCityStateAndAddress(places);
    }

    private void generateCityAndStateFromZip() throws IOException {
        places = generateCityAndStateFromZip(places);
    }

    private void cleanZipCodes() {
        places.forEach(Place::cleanZipCode);
    }

    public List<Place> getPlaces()
    {
        return places;
    }

    private static List<Place> generateCityAndStateFromZip(List<Place> places) throws IOException{


        String[] zips = new String[places.size()];
        for (int i = 0; i < places.size(); i++) {
            zips[i] = places.get(i).zip;
        }

        String finalRequest = generateZipQuery(zips);
        Document doc = Jsoup.connect(finalRequest).get();
        List<Place> generatedPlaces = extractPlacesFromDocument(doc);


        List<Place> correctPlaces = new ArrayList<>();
        for (int i = 0; i < places.size(); i++) {
            if(generatedPlaces.get(i).zip.equals(""))
                correctPlaces.add(places.get(i));
            else
            {
                Place p = places.get(i);
                p.state = generatedPlaces.get(i).state;
                p.city = generatedPlaces.get(i).city;
                correctPlaces.add(p);
            }
        }

        return correctPlaces;
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


    private static List<Place> generateZipFromCityStateAndAddress(List<Place> places) throws IOException {
        Document doc = Jsoup.connect(generateZipQuery(places)).get();
        Elements body = doc.select("Address");

        List<Place> places2 = new ArrayList<>();
        for (int i = 0; i < places.size(); i++) {
            Element element = body.get(i);

            if(isInvalidRecord(element))
            {
                places2.add(places.get(i));
                continue;
            }

            Place p = new Place();
            p.zip = element.select("Zip5").text();
            p.city = element.select("city").text();
            p.state = element.select("state").text();
            p.address = element.select("Address2").text();
            places2.add(p);
        }
        return places2;
    }

    private static boolean isInvalidRecord(Element element) {
        return element.select("Zip5").text().equals("");
    }

    private static String generateZipQuery(List<Place> places) {
        String url = "http://production.shippingapis.com/ShippingAPI.dll?API=ZipCodeLookup&XML=";
        StringBuilder finalRequest = new StringBuilder();
        finalRequest.append(url);
        finalRequest.append("<ZipCodeLookupRequest USERID=\"" + UserID + "\">");
        int i = 0;
        for (Place place : places) {
            finalRequest.append("<Address ID=\'").append(i++).append("\'>")
                    .append("<FirmName></FirmName>")
                    .append("<Address1></Address1>")
                    .append("<Address2>").append(place.address).append("</Address2>")
                    .append("<City>").append(place.city).append("</City>")
                    .append("<State>").append(place.state).append("</State>")
                    .append("</Address>");
        }
        finalRequest.append("</ZipCodeLookupRequest>");
        return finalRequest.toString();
    }
}
