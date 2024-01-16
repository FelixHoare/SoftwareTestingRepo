package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.ArrayList;
import java.util.List;

public class PathCombiner {

    /**
     * Reverses a provided flightpath
     * @param path the path to be reversed
     * @return List<LngLat> the reversed path
     */
    private static List<LngLat> reversePath(List<LngLat> path) {
        List<LngLat> pathCopy = new ArrayList<>(path);
        for (int i = 0, j = pathCopy.size() - 1; i < j; i++) {
            pathCopy.add(i, pathCopy.remove(j));
        }
        return pathCopy;
    }

    /**
     * Combines two paths into one
     * Utilises reversePath to reverse the path back to the starting position
     * Adds the starting position to the end of the path so the drone can hover whilst pizza is delivered
     * @param pathTo the path to the destination
     * @return List<LngLat> the combined path
     */
    public static List<LngLat> fullPath(List<LngLat> pathTo) {
        List<LngLat> fullPath = new ArrayList<>();
        List<LngLat> pathFrom = reversePath(pathTo);
        fullPath.addAll(pathTo);
        fullPath.addAll(pathFrom);
        fullPath.add(new LngLat(-3.186874, 55.944494));
        return fullPath;
    }
}
