package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URI;
import java.util.*;

public class App 
{

    /**
     * Runs PizzaDronz for the given date and server URL
     * Checks validity of orders obtained from the URL
     * Creates flightpaths for those that satisfy OrderValidator
     * Creates json and geojson files for the relevant information
     * @param args the date [0] and server URL [1]
     */
    public static void main(String[] args) {

        // checks to ensure the program runs correctly with expected input arguments
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments. Expected 2, got " + args.length);
            System.exit(1);
        }

        if (!validDate(args[0])) {
            System.err.println("Incorrect date format. Expected YYYY-MM-DD, got " + args[0]);
            System.exit(1);
        }

        if (!validURL(args[1])) {
            System.err.println("Incorrect URL format. Expected a valid URL, got " + args[1]);
            System.exit(1);
        }

        if (!RestReader.getIsAlive(args[1])) {
            System.err.println("The server is not alive. Expected a valid URL, got " + args[1]);
            System.exit(1);
        }

        System.out.println("Processing requested orders for " + args[0]);

        OrderValidator orderValidator = new OrderValidator();
        Order[] orders = RestReader.getRequestedOrders(args[0], args[1]);
        List<Order> validOrders = new ArrayList<>();
        List<Order> allOrders = new ArrayList<>();
        Restaurant[] restaurants = RestReader.getRequestedRestaurants(args[1]);
        NamedRegion[] noFLyZones = RestReader.getRequestedNoFlyZones(args[1]);
        NamedRegion centralArea = RestReader.getRequestedCentralArea(args[1]);

        // checks to make sure valid data can be used in the program
        if (restaurants.length == 0) {
            System.err.println("No restaurant data. Expected at least one restaurant, got " + restaurants.length);
            System.exit(1);
        }

        if (noFLyZones.length == 0) {
            System.err.println("No no fly zone data. Expected at least one no fly zone, got " + noFLyZones.length);
            System.exit(1);
        }

        if (centralArea == null) {
            System.err.println("No central area data. Expected one central area, got null");
            System.exit(1);
        }

        System.out.println("Validating Orders");

        // checks all orders for validity, and adds them to the relevant lists
        for (Order order : orders) {
            allOrders.add(order);
            orderValidator.validateOrder(order, restaurants);
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                validOrders.add(order);
            }
        }

        // storage for orders
        List<List<LngLat>> pathList = new ArrayList<>();
        HashMap<String, List<LngLat>> restaurantPaths = new HashMap<>();
        HashMap<Order, List<LngLat>> orderPaths = new HashMap<>();

        System.out.println("Mapping Flightpaths");

        // creates flightpaths for valid orders
        for (Order order : validOrders) {
            LngLat startPosition = getStartPosition();
            LngLat endPosition = orderValidator.oneRestaurant(order.getPizzasInOrder(), restaurants).location();
            String restaurantName = orderValidator.oneRestaurant(order.getPizzasInOrder(), restaurants).name();
            if (!restaurantPaths.containsKey(restaurantName)) {
                List<LngLat> pathTo = PathFinding.findPath(startPosition, endPosition, noFLyZones);
                List<LngLat> fullPath = PathCombiner.fullPath(pathTo);
                restaurantPaths.put(restaurantName, fullPath);
            }
            List<LngLat> path = restaurantPaths.get(restaurantName);

            // set the order status to delivered if a path is found
            // orderPaths map used to ensure correct path given to correct order
            if (path != null) {
                order.setOrderStatus(OrderStatus.DELIVERED);
                pathList.add(path);
                orderPaths.put(order, path);
            }
        }

        System.out.println("Creating files for " + args[0]);

        // creates json and geojson files for the relevant information
        GeoJsonConverter.generateFlightPathGeoJson(pathList, args[0]);
        JsonConverter.writeDeliveriesToFile(allOrders, args[0]);
        JsonConverter.writeFlightPathToFile(pathList, validOrders, args[0]);
        //JsonConverter.writeFlightPathToFile(orderPaths, args[0]);

        System.out.println("PizzaDronz is ready for delivery!");
    }

    /**
     * Locates the starting position for the drone's deliveries
     * @return the start position of the drone
     */
    private static LngLat getStartPosition() {
        return new LngLat(-3.186874, 55.944494);
    }

    /**
     * Checks if the date is in the correct format, ISO 8601
     * @param date the date argument to be checked
     * @return true if the date is in the correct format, false otherwise
     */
    private static boolean validDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        try {
            LocalDate.parse(date, formatter);
            return true;
        }
        catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    /**
     * Checks if the URL is a valid URL
     * @param url the URL argument to be checked
     * @return true if the URL is in the correct format, false otherwise
     */
    private static boolean validURL(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                URI uri = new URI(url);
                return true;
            }
            catch (Exception e) {
                System.err.println("Invalid URL: \n" + e);
                return false;
            }
        }
        return false;
    }
}
