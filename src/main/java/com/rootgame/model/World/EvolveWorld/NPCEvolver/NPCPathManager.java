package com.rootgame.model.World.EvolveWorld.NPCEvolver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;
import com.rootgame.model.World.WorldObjects.WorldObjectType;

public class NPCPathManager  {

    private NPCPathManager() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * The function getRandomDestination returns a random WorldObject of type SETTLEMENT from a given
     * list of WorldObjects.
     * 
     * @param worldObjectList A list of WorldObject objects.
     * @return The method is returning a random WorldObject of type SETTLEMENT from the filtered list.
     */
    public static WorldObject getRandomDestination(List<WorldObject> worldObjectList, WorldObjectType type) {
        List<WorldObject> filterdList = filterdList(worldObjectList, WorldObjectType.SETTLEMENT);
        return filterdList.get((int) (Math.random() * filterdList.size()));
    }

    /**
     * The function `makeDestinationPath` uses Dijkstra's algorithm to find the shortest path from an
     * NPC's current location to a given destination in a graph.
     * 
     * @param npc An NPC object representing a non-player character in the game.
     * @param destination The destination parameter is the WorldObject that the NPC wants to reach. It
     * represents the goal node in the graph.
     * @return The method is returning a Queue of WorldObjects, which represents the optimal path from
     * the current location of the NPC to the destination WorldObject.
     */
    public static Queue<WorldObject> makeDestinationPath(NPC npc, WorldObject destination) {
        WorldObject currentLocation = npc.getCurrentLocation(); // Start node
        Map<WorldObject, Integer> distance = new HashMap<>(); // Distance from start node to given node
        Map<WorldObject, WorldObject> previous = new HashMap<>(); // Previous node in optimal path from source
        PriorityQueue<WorldObject> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get)); // Priority queue of all nodes in Graph comparing them by their distance from start node (lowest distance first)

        distance.put(currentLocation, 0);
        queue.add(currentLocation);

        while (!queue.isEmpty()) {
            WorldObject current = queue.poll();

            if (current == destination) {
                break;
            }

            int currentDistance = distance.get(current);

            for (WorldObject neighbour : current.getNeighbours()) {
                int newDistance = currentDistance + getEdgeWeight(current, neighbour);
                // If new path is shorter than previous shortest path to neighbour then update distance and previous node in optimal path to neighbour 
                if (!distance.containsKey(neighbour) || newDistance < distance.get(neighbour)) { 
                    distance.put(neighbour, newDistance);
                    previous.put(neighbour, current);
                    queue.remove(neighbour); // Remove and re-add to priority queue to update position of neighbour in queue (if it is already in the queue)
                    queue.add(neighbour);
                }
            }
        }
        return buildPath(destination, previous);
    }

    /**
     * The function calculates the weight/cost of an edge between two WorldObjects based on their
     * distance.
     * 
     * @param source The source parameter is a WorldObject representing the starting point of the edge.
     * @param destination The destination parameter is a WorldObject representing the object that we
     * want to calculate the edge weight to.
     * @return The distance between the source and destination objects.
     */
    private static int getEdgeWeight(WorldObject source, WorldObject destination) {
        int distance = source.getDistance(destination);
        // Return the weight/cost of the edge between source and destination
        // Implement your logic here based on your specific requirements
        return distance;
    }

    /**
     * The function builds a path from a destination object to a starting object using a map of
     * previous objects.
     * 
     * @param destination The destination parameter is the WorldObject that we want to build a path to.
     * @param previous The "previous" parameter is a Map that maps each WorldObject to its previous
     * WorldObject in the path. It is used to reconstruct the path from the destination to the starting
     * point.
     * @return The method is returning a Queue of WorldObjects, which represents the path from the
     * destination to the starting point.
     */
    private static Queue<WorldObject> buildPath(WorldObject destination, Map<WorldObject, WorldObject> previous) {
        LinkedList<WorldObject> path = new LinkedList<>();
        WorldObject current = destination;

        while (current != null) { // Traverse through map until we reach starting point (which has null as previous)
            path.addFirst(current);
            current = previous.get(current);
        }
        path.removeFirst(); // Remove starting point from path since we don't need it (we already know it)
        return path;
    }

    private static List<WorldObject> filterdList(List<WorldObject> worldObjectList, WorldObjectType type) {
        return worldObjectList.stream()
            .filter(s -> s.getType() == type)
            .collect(Collectors.toList());
    }
}
