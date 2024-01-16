package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class RestReader {

    /**
     * read the order data from the date and url passed in the command line
     * takes the json and converts to array of Order objects
     * @param date the date to get the orders from
     * @param url the url to get the orders from
     * @return an array of orders
     */
    public static Order[] getRequestedOrders(String date, String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/orders/"+date))
                .build();
        HttpResponse<String> response = null;

        // HttpRequest is used to read from the REST API
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }

        // the response code is checked to see if the request was successful
        // it is then converted to an array of Order objects
        // ObjectMapper is used to convert the json to an array of Order objects
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            try {
                return objectMapper.readValue(response.body(), Order[].class);
            }
            catch (Exception e) {
                System.err.println("Unable to read ObjectMapper value for Orders. \nError code: " + e);
                System.exit(1);
            }
        }

        // if the request was unsuccessful, an error message is printed and the program exits gracefully
        else {
            System.err.println("Unsuccessful API request for Orders. \nError code: " + response.statusCode());
            System.exit(1);
        }
        return new Order[0];
    }

    /**
     * read the restaurant data from the url passed in the command line
     * takes the json and converts to array of Restaurant objects
     * @param url the url to get the restaurants from
     * @return an array of restaurants
     */
    public static Restaurant[] getRequestedRestaurants(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/restaurants"))
                .build();
        HttpResponse<String> response = null;

        // HttpRequest is used to read from the REST API
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Unable to obtain response from REST API for Restaurants. \nError code: " + e);
            System.exit(1);
        }

        // the response code is checked to see if the request was successful
        // it is then converted to an array of Restaurant objects
        // ObjectMapper is used to convert the json to an array of Restaurant objects
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.body(), Restaurant[].class);
            }
            catch (Exception e) {
                System.err.println("Unable to read ObjectMapper value for Restaurants. \nError code: " + e);
                System.exit(1);
            }
        }

        // if the request was unsuccessful, an error message is printed and the program exits gracefully
        else {
            System.err.println("Unsuccessful API request for Restaurants. \nError code: " + response.statusCode());
            System.exit(1);
        }
        return new Restaurant[0];
    }

    /**
     * read the central area coordinates from the url passed in the command line
     * takes the json and converts to array of LngLat objects
     * @param url the url to get the central area coordinates from
     * @return an array of lnglat
     */
    public static NamedRegion getRequestedCentralArea(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/centralArea"))
                .build();
        HttpResponse<String> response = null;

        // HttpRequest is used to read from the REST API
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Unable to obtain response from REST API for CentralArea. \nError code: " + e);
            System.exit(1);
        }

        // the response code is checked to see if the request was successful
        // it is then converted to an array of LngLat objects
        // ObjectMapper is used to convert the json to a NamedRegion object
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.body(), NamedRegion.class);
            }
            catch (Exception e) {
                System.err.println("Unable to read ObjectMapper value for CentralArea. \nError code: " + e);
                System.exit(1);
            }
        }

        // if the request was unsuccessful, an error message is printed and the program exits gracefully
        else {
            System.err.println("Unsuccessful API request for CentralArea. \nError code: " + response.statusCode());
            System.exit(1);
        }
        return null;
    }

    /**
     * read the no-fly zones from the url passed in the command line
     * takes the json and converts to array of LngLat objects
     * @param url the url to get the no-fly zones from
     * @return an array of no-fly zones
     */
    public static NamedRegion[] getRequestedNoFlyZones(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/noFlyZones"))
                .build();
        HttpResponse<String> response = null;

        // HttpRequest is used to read from the REST API
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Unable to obtain response from REST API for NoFlyZones. \nError code: " + e);
            System.exit(1);
        }

        // the response code is checked to see if the request was successful
        // it is then converted to an array of LngLat objects
        // ObjectMapper is used to convert the json to an array of NamedRegion objects
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.body(), NamedRegion[].class);
            }
            catch (Exception e) {
                System.err.println("Unable to read ObjectMapper value for NoFlyZones. \nError code: " + e);
                System.exit(1);
            }
        }

        // if the request was unsuccessful, an error message is printed and the program exits gracefully
        else {
            System.err.println("Unsuccessful API request for NoFlyZones. \nError code: " + response.statusCode());
            System.exit(1);
        }
        return null;
    }

    /**
     * read the REST API to see if the page is alive
     * true if it is, false if it isn't
     * @param url the url to get the boolean from
     * @return a boolean
     */
    public static Boolean getIsAlive(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/isAlive"))
                .build();
        HttpResponse<String> response = null;

        // HttpRequest is used to read from the REST API
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Unable to obtain request from REST API. Your URL may not be correct, please try again\n" +
                    "Error code: " + e);
            System.exit(1);
        }

        // the response code is checked to see if the request was successful
        String aliveResponse = response.body();

        return aliveResponse.equals("true");
    }
}
