package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AppTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    public void testValidArgs() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
        assertTrue(App.getArgsCheck(args));
    }

    private final PrintStream originalErr = System.err;

    @Test
    public void testNoArgs() {
        System.setErr(new PrintStream(output));

        try {
            String[] args = {};
            assertFalse(App.getArgsCheck(args));
            assertEquals("Incorrect number of arguments. Expected 2, got 0", output.toString().trim());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void testNoArgsOLD() {
        String[] args = {};
        assertFalse(App.getArgsCheck(args));
    }

    @Test
    public void testArgsWrongOrder() {
        System.setErr(new PrintStream(output));

        try {
            String[] args = {"https://ilp-rest.azurewebsites.net", "2023-11-15"};
            assertFalse(App.getArgsCheck(args));
            assertEquals("java.time.format.DateTimeParseException: Text 'https://ilp-rest.azurewebsites.net' could not be parsed at index 0\n" + //
                    "Incorrect date format. Expected YYYY-MM-DD, got https://ilp-rest.azurewebsites.net", output.toString().trim());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void testOneArg() {
        // String[] args = {"2023-11-15"};
        // assertFalse(App.getArgsCheck(args));
        System.setErr(new PrintStream(output));

        try {
            String[] args = {"2023-11-15"};
            assertFalse(App.getArgsCheck(args));
            assertEquals("Incorrect number of arguments. Expected 2, got 1", output.toString().trim());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void testTooManyArgs() {
        // String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net", "Extra"};
        // assertFalse(App.getArgsCheck(args));
        System.setErr(new PrintStream(output));

        try {
            String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net", "Extra"};
            assertFalse(App.getArgsCheck(args));
            assertEquals("Incorrect number of arguments. Expected 2, got 3", output.toString().trim());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void testIncorrectDateFormat() {
        // String[] args = {"15-11-2023", "https://ilp-rest.azurewebsites.net"};
        // assertFalse(App.getArgsCheck(args));
        System.setErr(new PrintStream(output));

        try {
            String[] args = {"15-11-2023", "https://ilp-rest.azurewebsites.net"};
            assertFalse(App.getArgsCheck(args));
            assertEquals("java.time.format.DateTimeParseException: Text '15-11-2023' could not be parsed at index 0\n" + //
                    "Incorrect date format. Expected YYYY-MM-DD, got 15-11-2023", output.toString().trim());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void testIncorrectURL() {
        // String[] args = {"2023-11-15", "https://google.com"};
        // assertFalse(App.getArgsCheck(args));
        System.setErr(new PrintStream(output));

        try {
            String[] args = {"2023-11-15", "https://google.com"};
            assertFalse(App.getArgsCheck(args));
            assertEquals("The server is not alive. Expected a valid URL, got https://google.com", output.toString().trim());
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void testRunTime() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
        long startTime = System.currentTimeMillis();

        App.main(args);
        
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        assertTrue(timeElapsed < 60000);
    }

    // @Test
    // public void averageRunTime() {
    //     String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
    //     int totalTime = 0;
    //     boolean bool = true;
    //     for (int i = 0; i < 100; i++) {
    //         long startTime = System.currentTimeMillis();
    //         App.main(args);
    //         long endTime = System.currentTimeMillis();
    //         long timeElapsed = endTime - startTime;
    //         totalTime += timeElapsed;
    //     }
    //     System.out.println(totalTime / 100);
    //     assertTrue(bool);
    // }

    @Test
    public void testCorrectFilesCreated() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
        String[] fileNames = {"deliveries-2023-11-15.json", "drone-2023-11-15.geojson", "flightpath-2023-11-15.json"};
        String directoryPath = "resultfiles/";

        App.main(args);

        for (String fileName : fileNames) {
            Path path = FileSystems.getDefault().getPath(directoryPath + fileName);
            assertTrue(Files.exists(path));
        }
    }

    @Test
    public void testEmptyDeliveriesFile() {
        String[] args = {"2023-01-01", "https://ilp-rest.azurewebsites.net"};
        String fileName = "resultfiles/deliveries-2023-01-01.json";

        App.main(args);
        
        Path path = FileSystems.getDefault().getPath(fileName);
        assertTrue(Files.exists(path));
        
        try {
            String content = Files.readString(path);
            assertEquals("[]", content);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    @Test
    public void testDeliveryFileForContent() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
        String fileName = "resultfiles/deliveries-2023-11-15.json";

        App.main(args);

        Path path = FileSystems.getDefault().getPath(fileName);
        assertTrue(Files.exists(path));

        try {
            String content = Files.readString(path);
            assertNotEquals("[]", content);
            assertTrue(content.contains("orderValidationCode"));
            assertTrue(content.contains("orderNo"));
            assertTrue(content.contains("orderStatus"));
            assertTrue(content.contains("costInPence"));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Test
    public void testDroneFileForContent() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
        String fileName = "resultfiles/drone-2023-11-15.geojson";

        App.main(args);

        Path path = FileSystems.getDefault().getPath(fileName);
        assertTrue(Files.exists(path));

        try {
            String content = Files.readString(path);
            assertTrue(content.contains("coordinates"));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Test
    public void testFLightpathFileForContent() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net"};
        String fileName = "resultfiles/flightpath-2023-11-15.json";

        App.main(args);

        Path path = FileSystems.getDefault().getPath(fileName);
        assertTrue(Files.exists(path));

        try {
            String content = Files.readString(path);
            assertNotEquals("[]", content);
            assertTrue(content.contains("orderNo"));
            assertTrue(content.contains("fromLongitude"));
            assertTrue(content.contains("fromLatitude"));
            assertTrue(content.contains("angle"));
            assertTrue(content.contains("toLongitude"));
            assertTrue(content.contains("toLatitude"));
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
