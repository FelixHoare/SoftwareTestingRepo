package uk.ac.ed.inf;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.Assert.*;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.Random;  

//LngLat(-3.186874, 55.944494); appleton tower

public class LngLatHandlerTest {

    @Test
    public void testDistanceTo() {
        LngLatHandler handler = new LngLatHandler();
        LngLat startPosition = new LngLat(0.0, 0.0);
        LngLat endPosition = new LngLat(3.0, 4.0);
        double expectedDistance = 5.0;
        double actualDistance = handler.distanceTo(startPosition, endPosition);
        assertEquals(expectedDistance, actualDistance, 0.001);
    }

    @RepeatedTest(100)
    public void testCloseTo() {
        LngLatHandler handler = new LngLatHandler();
        double x1 = -3.186874;
        double y1 = 55.944494;
        LngLat point = new LngLat(x1, y1);

        for (int i = 0; i < 360; i += 22.5) {
            double rand = new Random().nextDouble() * 0.00014;
            LngLat testPoint = new LngLat(x1 + rand * Math.toRadians(Math.cos(i)), y1 + rand * Math.toRadians(Math.sin(i)));
            assertTrue(handler.isCloseTo(point, testPoint));
        }
    }

    @Test
    public void testNotCloseTo() {
        LngLatHandler handler = new LngLatHandler();
        double x1 = -3.186874;
        double y1 = 55.944494;
        LngLat point = new LngLat(x1, y1);

        double x2 = 1;
        double y2 = 1;
        LngLat testPoint = new LngLat(x2, y2);

        assertFalse(handler.isCloseTo(point, testPoint));
    }

    @RepeatedTest(100)
    public void testInCentralArea() {
        LngLatHandler handler = new LngLatHandler();

        double x1 = -3.192473;
        double x2 = -3.184319;
        double y1 = 55.946233;
        double y2 = 55.942617;

        LngLat[] central = {
            new LngLat(x1, y1),
            new LngLat(x1, y2),
            new LngLat(x2, y2),
            new LngLat(x2, y1)
        };
        
        NamedRegion centralArea = new NamedRegion("Central Area", central);

        double X = x2 + new Random().nextDouble() * (x1 - x2);
        double Y = y2 + new Random().nextDouble() * (y1 - y2);
        LngLat point = new LngLat(X, Y);

        assertTrue(handler.isInRegion(point, centralArea));
    }

    @RepeatedTest(100)
    public void testOutOfCentralArea() {
        LngLatHandler handler = new LngLatHandler();

        double x1 = -3.192473;
        double x2 = -3.184319;
        double y1 = 55.946233;
        double y2 = 55.942617;

        LngLat[] central = {
            new LngLat(x1, y1),
            new LngLat(x1, y2),
            new LngLat(x2, y2),
            new LngLat(x2, y1)
        };
        
        NamedRegion centralArea = new NamedRegion("Central Area", central);

        double X = x2 - 1 + new Random().nextDouble() * (x1 - x2 + 2);
        double Y = y2 - 1 + new Random().nextDouble() * (y1 - y2 + 2);

        LngLat pointA = new LngLat(X + 2 * Math.abs(x1 - x2), Y + 2 * Math.abs(y1 - y2));
        assertFalse(handler.isInRegion(pointA, centralArea));
        LngLat pointB = new LngLat(X - 2 * Math.abs(x1 - x2), Y - 2 * Math.abs(y1 - y2));
        assertFalse(handler.isInRegion(pointB, centralArea));
        LngLat pointC = new LngLat(X + 2 * Math.abs(x1 - x2), Y - 2 * Math.abs(y1 - y2));
        assertFalse(handler.isInRegion(pointC, centralArea));
        LngLat pointD = new LngLat(X - 2 * Math.abs(x1 - x2), Y + 2 * Math.abs(y1 - y2));
        assertFalse(handler.isInRegion(pointD, centralArea));
    }

    @RepeatedTest(100)
    public void testFlight() {
        LngLatHandler handler = new LngLatHandler();

        double lng = new Random().nextDouble() * 360 - 180;
        double lat = new Random().nextDouble() * 180 - 90;
        double angle = Math.toRadians(new Random().nextInt(16) * 22.5);

        LngLat startPosition = new LngLat(lng, lat);
        LngLat endPosition = handler.nextPosition(startPosition, angle);

        double angleRadians = Math.toRadians(angle);
        double x = startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleRadians));
        double y = startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleRadians));
        LngLat expectedPosition = new LngLat(x,y);

        assertTrue(expectedPosition.equals(endPosition));
    }

    @Test
    public void testHovering() {
        LngLatHandler handler = new LngLatHandler();

        double lng = new Random().nextDouble() * 360 - 180;
        double lat = new Random().nextDouble() * 180 - 90;

        LngLat startPosition = new LngLat(lng, lat);
        LngLat endPosition = handler.nextPosition(startPosition, 999);

        assertTrue(startPosition.equals(endPosition));
    }

}

