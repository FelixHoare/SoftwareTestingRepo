package uk.ac.ed.inf;

import java.util.*;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class PathFinding {

    /**
     * A* search algorithm for flightpath finding
     * @param start the LngLat starting position of the drone for the path
     * @param goal the LngLat end position of the drone for the path - drone need only be close to this
     * @param noFlyZones the NamedRegion areas the done cannot enter
     * @return a List<LngLat> of coordinates for the drone to follow to make up a valid path
     */
    public static List<LngLat> findPath(LngLat start, LngLat goal, NamedRegion[] noFlyZones) {
        LngLatHandler lngLatHandler = new LngLatHandler();
        Set<LngLat> closedSet = new HashSet<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getTotalCost));
        Map<LngLat, LngLat> cameFrom = new HashMap<>();
        Map<LngLat, Double> gScore = new HashMap<>();

        if (isInNoFlyZone(start, noFlyZones) || isInNoFlyZone(goal, noFlyZones)) {
            return null;
        }

        openSet.add(new Node(start, 0, lngLatHandler.distanceTo(start, goal)));

        gScore.put(start, 0.0);

        // search for path in every node from the open set
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // if the current position is close to the goal position, the path is found
            if (lngLatHandler.isCloseTo(current.getLocation(), goal)) {
                return reconstructPath(cameFrom, current.getLocation());
            }

            closedSet.add(current.getLocation());

            // for each neighbour of the current node, check if it is unsuitable to be in the path
            // (already visited or is in a noFlyZone)
            for (LngLat neighbor : getNeighbours(current.getLocation())) {
                if (closedSet.contains(neighbor) || isInNoFlyZone(neighbor, noFlyZones)) {
                    continue;
                }

                // search heuristic
                Double currentGScore = gScore.get(current.getLocation());
                double tentativeGScore = (currentGScore != null ? currentGScore : 0.0) + lngLatHandler.distanceTo(current.getLocation(), neighbor);

                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    // records where the node came from and the cost to get there
                    cameFrom.put(neighbor, current.getLocation());
                    gScore.put(neighbor, tentativeGScore);
                    double heuristic = lngLatHandler.distanceTo(neighbor, goal);
                    openSet.add(new Node(neighbor, tentativeGScore, heuristic));
                }
            }
        }

        System.out.println("No path found");
        return null;
    }

    /**
     * A node in the A* search algorithm
     * Composed of a LngLat location, a gScore and an hScore (heuristic)
     */
    private static class Node {
        private final LngLat location;
        private final double gScore;
        private final double hScore;

        public Node(LngLat location, double gScore, double hScore) {
            this.location = location;
            this.gScore = gScore;
            this.hScore = hScore;
        }

        public LngLat getLocation() {
            return location;
        }

        public double getTotalCost() {
            return gScore + hScore;
        }
    }

    private static boolean isInNoFlyZone(LngLat position, NamedRegion[] regions) {
        LngLatHandler lngLatHandler = new LngLatHandler();
        for (NamedRegion region : regions) {
            if (lngLatHandler.isInRegion(position, region)) {
                return true;
            }
        }
        return false;
    }

    // loops through the 16 valid compass directions and returns a LngLat[] of all neighbours
    private static LngLat[] getNeighbours(LngLat current) {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat[] neighbours = new LngLat[16];
        for (int i = 0; i < 16; i++) {
            neighbours[i] = lngLatHandler.nextPosition(current, i * 22.5);
        }
        return neighbours;
    }

    // reconstructs the path from the cameFrom map
    private static List<LngLat> reconstructPath(Map<LngLat, LngLat> cameFrom, LngLat current) {
        List<LngLat> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

}
