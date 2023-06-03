package com.rootgame.model.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.Path;
import com.rootgame.model.World.WorldObjects.Settlement;
import com.rootgame.model.World.WorldObjects.WorldObject;

public class EvolveWorld {
    private EvolveWorld() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void evolveWorld(List<WorldObject> worldObjectList, NPC randomNpc) {
        if (randomNpc.getDestinationPath().isEmpty()){
            WorldObject destination = worldObjectList.get((int) (Math.random() * worldObjectList.size()));
            randomNpc.setDestination(destination);
            System.out.println(randomNpc.getName() + " Origin: " + randomNpc.getCurrentLocation());
            System.out.println(randomNpc.getName() + "\n Destination: " + randomNpc.getDestination());

            randomNpc.setDestinationPath(makeDestinationPath(randomNpc, destination));
           //System.out.println(randomNpc.getName() + " " + randomNpc.getDestinationPath() + " " + destination);
        } else {randomNpc.moveNPC();
        }
        
        System.out.println("Evolved");
    }

    public static Queue<WorldObject> makeDestinationPath(NPC npc, WorldObject destination){
      WorldObject currentLocation = npc.getCurrentLocation();
      Queue<WorldObject> queue = new LinkedList<>();
      Set<WorldObject> visited = new HashSet<>();
      Map<WorldObject, Queue<WorldObject>> paths = new HashMap<>();
  
      queue.add(currentLocation);
      visited.add(currentLocation);
      paths.put(currentLocation, new LinkedList<>());
  
      while (!queue.isEmpty() && !visited.contains(destination)) {
          WorldObject current = queue.poll();
          Queue<WorldObject> path = paths.get(current);
  
          for (WorldObject neighbour : current.getNeighbours()) {
              if (!visited.contains(neighbour)) {
                  Queue<WorldObject> newPath = new LinkedList<>(path);
                  newPath.add(neighbour);
                  paths.put(neighbour, newPath);
                  queue.add(neighbour);
                  visited.add(neighbour);
              }
          }
          paths.remove(current);
      }
      return paths.get(destination);
  }    
    
/*
   public static Queue<WorldObject> makeDestinationPath(NPC npc, WorldObject destination) {
    WorldObject currentLocation = npc.getCurrentLocation();
    Map<WorldObject, Integer> distance = new HashMap<>();
    Map<WorldObject, WorldObject> previous = new HashMap<>();
    PriorityQueue<WorldObject> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get));

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
    // Return the weight/cost of the edge between source and destination
    // Implement your logic here based on your specific requirements
}

private static Queue<WorldObject> buildPath(WorldObject destination, Map<WorldObject, WorldObject> previous) {
    LinkedList<WorldObject> path = new LinkedList<>();
    WorldObject current = destination;

    while (current != null) {
        path.addFirst(current);
        current = previous.get(current);
    }

    return path;
}
      }*/

    private  static void evolveWorldObjects() {}

    private static void evolveNPCs() {}

    private static void evolveFactions() {}

    private static void evolveClearings() {}

    private static void evolvePaths() {}

    private static void evolveWorldObject(WorldObject worldObject) {}

    private static void evolveNPC(NPC npc) {}

    private static void makeDestinationPath(WorldObject worldObject) {}

    private static void evolveFaction(String faction) {}

    private static void evolveClearing(Settlement clearing) {}

    private static void evolvePath(Path path) {}

    
}
