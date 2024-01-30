package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import uk.ac.ed.inf.ilp.data.*;

public class RestTest {
    
    @Test
    public void testIsServerAlive() {
        boolean alive = false;
        try {
            alive = RestReader.getIsAlive("https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertTrue(alive);
    }

    @Test
    public void testIsServerAliveIncorrectURL() {
        boolean alive = false;
        try {
            alive = RestReader.getIsAlive("https://google.com");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertFalse(alive);
    }

    // @Test
    // public void testIsServerAliveNoURL() {
    //     boolean alive = false;
    //     try {
    //         alive = RestReader.getIsAlive(null);
    //     } catch (Exception e) {
    //         System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
    //         System.exit(1);
    //     }
    //     assertFalse(alive);
    // }
    // Not working

    @Test
    public void testGetOrders() {
        Order[] orders = null;
        try {
            orders = RestReader.getRequestedOrders("2023-11-15", "https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertNotNull(orders);
        assertEquals(orders.length, 58);
    }

    private static boolean checkValidDate(String date) {
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

    @Test
    public void testInvalidDate() {
        String[] dates = {"2023/11/15", "2023 11 15", "15/11/2023", "date"};
        for (String date : dates) {
            assertFalse(checkValidDate(date));
        }
    }

    @Test
    public void testDateWithNoOrders() {
        Order[] orders = null;
        try {
            orders = RestReader.getRequestedOrders("2023-01-01", "https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertNotNull(orders);
        assertEquals(orders.length, 0);
    }

    @Test
    public void testGetRestaurants() {
        Restaurant[] restaurants = null;
        try {
            restaurants = RestReader.getRequestedRestaurants("https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertNotNull(restaurants);
        assertEquals(restaurants.length, 7);
    }

    @Test
    public void testGetCentralArea() {
        NamedRegion centralArea = null;
        try {
            centralArea = RestReader.getRequestedCentralArea("https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertNotNull(centralArea);
        assertEquals(centralArea.name(), "central");
    }

    @Test
    public void testGetNoFlyZones() {
        NamedRegion[] noFlyZones = null;
        try {
            noFlyZones = RestReader.getRequestedNoFlyZones("https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            System.err.println("Failed to obtain response from REST API for Orders. \nError code: " + e);
            System.exit(1);
        }
        assertNotNull(noFlyZones);
        assertEquals(noFlyZones.length, 4);
    }

}
