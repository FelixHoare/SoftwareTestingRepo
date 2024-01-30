package uk.ac.ed.inf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.text.DecimalFormat;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

public class JsonConverter {

    /**
     * Writes a Json file of the day's deliveries
     * Includes order number, order status, order validation code and cost in pence
     * @param orders the relevant orders for the day
     * @param date the date corresponding to the orders
     */
    public static void writeDeliveriesToFile(List<Order> orders, String date) {
        JSONArray deliveryArray = new JSONArray();

        for (Order order : orders) {
            JSONObject delivery = new JSONObject();
            delivery.put("orderNo", order.getOrderNo());
            delivery.put("orderStatus", order.getOrderStatus());
            delivery.put("orderValidationCode", order.getOrderValidationCode());
            delivery.put("costInPence", order.getPriceTotalInPence());
            deliveryArray.put(delivery);
        }

        String fileName = "resultfiles/deliveries-" + date + ".json";
        writeJson(fileName, deliveryArray);
    }

//    /**
//     * Writes a Json file of the day's flightpaths, separated by order number
//     * Includes order number, to and from coordinates, and angle between coordinates
//     * @param path the relevant flightpaths for the day
//     * @param order the relevant orders for the day
//     * @param date the date corresponding to the flightpaths
//     */
//    public static void writeFlightPathToFile(HashMap<Order, List<LngLat>> orderPath, String date) {
//        JSONArray flightPathArray = new JSONArray();
//
//        for (Order key : orderPath.keySet()) {
//            List<LngLat> path = orderPath.get(key);
//            for (int i = 0; i < path.size() - 1; i++) {
//                JSONObject flightPath = new JSONObject();
//                flightPath.put("orderNo", key.getOrderNo());
//                flightPath.put("fromLongitude", path.get(i).lng());
//                flightPath.put("fromLatitude", path.get(i).lat());
//                flightPath.put("angle", angleCalculator(path.get(i), path.get(i+1)));
//                flightPath.put("toLongitude", path.get(i+1).lng());
//                flightPath.put("toLatitude", path.get(i+1).lat());
//                flightPathArray.put(flightPath);
//            }
//        }
//
//        String fileName = "resultfiles/flightpath-" + date + ".json";
//        writeJson(fileName, flightPathArray);
//    }
    public static void writeFlightPathToFile(List<List<LngLat>> path, List<Order> order, String date) {
        JSONArray flightPathArray = new JSONArray();

        for (int i = 0; i < path.size(); i++) {
            for (int j = 0; j < path.get(i).size() - 1; j++) {
                JSONObject flightPath = new JSONObject();
                flightPath.put("orderNo", order.get(i).getOrderNo());
                flightPath.put("fromLongitude", path.get(i).get(j).lng());
                flightPath.put("fromLatitude", path.get(i).get(j).lat());
                flightPath.put("angle", angleCalculator(path.get(i).get(j), path.get(i).get(j+1)));
                flightPath.put("toLongitude", path.get(i).get(j+1).lng());
                flightPath.put("toLatitude", path.get(i).get(j+1).lat());
                flightPathArray.put(flightPath);
            }
        }

        String fileName = "resultfiles/flightpath-" + date + ".json";
        writeJson(fileName, flightPathArray);
    }

    // used to round angles to correct decimal places, removing unnecessary precision
    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Writes a Json file of the relevant JsonArray to the resultfiles directory
     * @param fileName the name of the file to be written
     * @param jsonArray the String of all the data to be written
     */
    private static void writeJson(String fileName, JSONArray jsonArray) {
        new File("resultfiles").mkdirs();
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonArray.toString());
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + fileName);
        }
    }

    private static double angleCalculator(LngLat from, LngLat to) {
        if (from == to || from.equals(to)) {
            return 999.0;
        }
        double angle = Math.toDegrees(Math.atan2(to.lat() - from.lat(), to.lng() - from.lng()));
        if (angle < 0) {
            angle += 360;
        }
        // remove unnecessary precision
        angle = Double.parseDouble(df.format(angle));
        return angle;
    }
}
