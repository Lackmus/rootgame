package com.rootgame.model.World;

import java.util.Collections;
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

public class EvolveWorld {
    private EvolveWorld() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void evolveWorld(List<WorldObject> worldObjectList) {
        evolveNPCs(worldObjectList);
        System.out.println("Evolved");
    }

/***************
 * NPC-Evolver *
 *************************************************************************************************************************************************************/

    private static void evolveNPCs(List<WorldObject> worldObjectList) {
        List<WorldObject> synchronizedList = Collections.synchronizedList(worldObjectList);

        synchronized (synchronizedList) {
            for (WorldObject worldObject : synchronizedList) {
                synchronized (worldObject) {
                    for (NPC npc : worldObject.getNPCs()) {
                        evolveNPC(npc, synchronizedList);
                    }
                }
            }            
            for (WorldObject worldObject : synchronizedList) {
                synchronized (worldObject) {
                    for (NPC npc : worldObject.getNPCs()) {
                        npc.setMoved(false);
                    }
                }
            }
        }
    }

    private static void evolveNPC(NPC npc, List<WorldObject> worldObjectList) {
        switch (npc.getType()) {
            case CARAVAN:                
                evolveCaravan(npc,worldObjectList);
                break;
            case LEADER:
                evolveLeader(npc);
                break;
            case SOLDIER:
                evolveSoldier(npc);
                break;
            case TRADER:
                evolveTrader(npc);
                break;
            case BANDIT:
                evolveBandit(npc);
                break;
            case MERCENARY:
                evolveMercenary(npc);
                break;
            default:
                break;
        }
        npc.setMoved(true);
    }

    
    private static void evolveBandit(NPC npc) {
        // TODO Auto-generated method stub
    }

    private static void evolveTrader(NPC npc) {
        // TODO Auto-generated method stub
    }

    private static void evolveSoldier(NPC npc) {
        // TODO Auto-generated method stub
    }

    private static void evolveLeader(NPC npc) {
        // TODO Auto-generated method stub
    }

    private static void evolveMercenary(NPC npc) {
        // TODO Auto-generated method stub
    }

    private static void evolveCaravan(NPC npc, List<WorldObject> worldObjectList) {
        if (npc.getDestinationPath().isEmpty()) {

            npc.setDestinationPath(makeDestinationPath(npc, getRandomDestination(worldObjectList)));
            System.out.println(npc.getName() + " Current: " + npc.getCurrentLocation() + " DestinationPath: " + npc.getDestinationPath());
        } else {
            npc.moveNPC();
        }
    }

    private static WorldObject getRandomDestination(List<WorldObject> worldObjectList) {
        List<WorldObject> filterdList = filterdList(worldObjectList, WorldObjectType.SETTLEMENT);
        return filterdList.get((int) (Math.random() * filterdList.size()));
    }


    public static Queue<WorldObject> makeDestinationPath(NPC npc, WorldObject destination) {
        WorldObject currentLocation = npc.getCurrentLocation(); // Start node
        Map<WorldObject, Integer> distance = new HashMap<>(); // Distance from start node to given node
        Map<WorldObject, WorldObject> previous = new HashMap<>(); // Previous node in optimal path from source
        PriorityQueue<WorldObject> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get)); // Priority queue of all nodes in Graph

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

                if (!distance.containsKey(neighbour) || newDistance < distance.get(neighbour)) {
                    distance.put(neighbour, newDistance);
                    previous.put(neighbour, current);
                    queue.remove(neighbour);
                    queue.add(neighbour);
                }
            }
        }
        return buildPath(destination, previous);
    }

    private static int getEdgeWeight(WorldObject source, WorldObject destination) {
        int distance = source.getDistance(destination);
        // Return the weight/cost of the edge between source and destination
        // Implement your logic here based on your specific requirements
        return distance;
    }

    private static Queue<WorldObject> buildPath(WorldObject destination, Map<WorldObject, WorldObject> previous) {
        LinkedList<WorldObject> path = new LinkedList<>();
        WorldObject current = destination;

        while (current != null) {
            path.addFirst(current);
            current = previous.get(current);
        }
        path.removeFirst();
        return path;
    }
      

//    private  static void evolveWorldObjects() {}

//    

//    private static void evolveFactions() {}

//    private static void evolveClearings() {}

//    private static void evolvePaths() {}

//    private static void evolveWorldObject(WorldObject worldObject) {}

//    

//    private static void makeDestinationPath(WorldObject worldObject) {}

//    private static void evolveFaction(String faction) {}

//    private static void evolveClearing(Settlement clearing) {}

//    private static void evolvePath(Path path) {}

    private static List<WorldObject> filterdList(List<WorldObject> worldObjectList, WorldObjectType type) {
        return worldObjectList.stream()
            .filter(s -> s.getType() == type)
            .collect(Collectors.toList());
    }
    
}
