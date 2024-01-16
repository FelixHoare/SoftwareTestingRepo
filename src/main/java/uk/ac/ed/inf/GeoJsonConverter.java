package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import uk.ac.ed.inf.ilp.data.LngLat;

public class GeoJsonConverter {

    /**
     * Converts all data to a JsonObject to be written to geojson file
     * file contains all LngLat positions of the drone that day
     * @param flightPaths the list of flightpaths to be written to geojson
     * @param date the date corresponding to the flightpaths
     */
    public static void generateFlightPathGeoJson(List<List<LngLat>> flightPaths, String date) {

        // writes file for corresponding date to the resultfiles directory
        String fileName = "resultfiles/drone-" + date + ".geojson";

        JsonObject featureCollection = new JsonObject();

        // empty geojson file if no flightpaths
        // exactly how geojson.io is seen when opened with no data
        if (flightPaths.size() == 0) {
            String emptyGeoJson = "{\n" +
                    "  \"type\": \"FeatureCollection\",\n" +
                    "  \"features\": []\n" +
                    "}";
            writeGeoJson(fileName, emptyGeoJson);
            return;
        }

        featureCollection.addProperty("type", "FeatureCollection");

        // JsonArray and JsonObject used to store the flightpaths directly from Java objects
        JsonArray features = new JsonArray();
        JsonObject feature = new JsonObject();
        JsonObject properties = new JsonObject();

        feature.addProperty("type", "Feature");
        feature.add("properties", properties);

        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");
        JsonArray coordinates = new JsonArray();

        // adds all coordinates to the JsonArray
        for (List<LngLat> flightPath : flightPaths) {
            for (LngLat lngLat : flightPath) {
                JsonArray coord = new JsonArray();
                coord.add(lngLat.lng());
                coord.add(lngLat.lat());
                coordinates.add(coord);
            }
        }
        geometry.add("coordinates", coordinates);
        feature.add("geometry", geometry);

        features.add(feature);

        //features.add(feature);

        featureCollection.add("features", features);

        // JsonObject converted to String and written to file
        writeGeoJson(fileName, featureCollection.toString());
    }

    /**
     * Writes a GeoJson file of the drone's flightpaths to the resultfiles directory
     * @param fileName the name of the file to be written
     * @param geoJson the String of all the data to be written
     */
    private static void writeGeoJson(String fileName, String geoJson) {
        new File("resultfiles").mkdirs();
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(geoJson);
        } catch (Exception e) {
            System.err.println("Failed to write to file: " + fileName);
        }
    }

}
