package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

/**
 * implement the needed computations for a LngLat
 */
public class LngLatHandler implements LngLatHandling {

    /**
     * get the distance between two positions
     * @param startPosition is where the start is
     * @param endPosition is where the end is
     * @return the euclidean distance between the positions
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double xSq = Math.pow((startPosition.lng() - endPosition.lng()), 2);
        double ySq = Math.pow((startPosition.lat() - endPosition.lat()), 2);
        return Math.sqrt(xSq + ySq);
    }

    /**
     * check if two positions are close (<= than SystemConstants.DRONE_IS_CLOSE_DISTANCE)
     * @param startPosition is the starting position
     * @param otherPosition is the position to check
     * @return if the positions are close
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * special handling shortcut for the central area. Here an implementation might add special improved processing as the central region is always rectangular
     * @param point to be checked
     * @param centralArea the central area
     * @return if the point is in the central area
     */
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        if (centralArea == null){
            throw new IllegalArgumentException("the named region is null");
        }
        if (centralArea.name().equals(SystemConstants.CENTRAL_REGION_NAME) == false) {
            throw new IllegalArgumentException("the named region: " + centralArea.name() + " is not valid - must be: " + SystemConstants.CENTRAL_REGION_NAME);
        }

        return isInRegion(point, centralArea);
    }

    /**
     * check if the @position is in the @region (includes the border)
     * @param position to check
     * @param region as a closed polygon
     * @return if the position is inside the region (including the border)
     */

    public boolean isInRegion(LngLat position, NamedRegion region) {
        int count = 0;
        LngLat[] coords = region.vertices();
        int n = coords.length;

        // ray casting algorithm

        for (int i = 0; i < n; i++) {
            LngLat p1 = coords[i];
            LngLat p2 = coords[(i + 1) % n];

            if (position.lat() == p1.lat() && position.lng() == p1.lng() || position.lat() == p2.lat() && position.lng() == p2.lng()) {
                return true;
            }

            if (position.lat() > Math.min(p1.lat(), p2.lat()) && position.lat() <= Math.max(p1.lat(), p2.lat()) &&
                    position.lng() <= Math.max(p1.lng(), p2.lng()) && p1.lat() != p2.lat()) {
                double xinters = (position.lat() - p1.lat()) * (p2.lng() - p1.lng()) / (p2.lat() - p1.lat()) + p1.lng();
                if (p1.lng() == p2.lng() || position.lng() <= xinters) {
                    count++;
                }
            }
        }
        // if count is odd, the point is within the region
        return count % 2 == 1;

    }

    /**
     * find the next position if an @angle is applied to a @startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * @return the new position after the angle is used
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        // hovering
        if (angle == 999) {
            return startPosition;
        }
        // trigonometry must use radians
        double angleRadians = Math.toRadians(angle);
        double x = startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleRadians));
        double y = startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleRadians));
        return new LngLat(x,y);
    }
}
