package com.rootgame.model.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.rootgame.model.NPC.NPCFactory;
import com.rootgame.model.NPC.NPCType;
import com.rootgame.model.World.WorldObjects.WorldObjectType;
import com.rootgame.model.World.WorldObjects.WorldObject;
import com.rootgame.model.World.WorldObjects.WorldObjectFactory;

public class GenerateWorld {

    /************************
     * World Generation API *
     ************************/
        
    /*******************************
     * fill World with settlements *
     *******************************/

    /**
     * The function fills a world with settlements at random locations while ensuring they are not too
     * close to each other or out of bounds.
     * 
     * @param settlementList A list of WorldObject instances representing settlements in the game
     * world.
     * @param mapWidth The width of the map in units (integer value).
     * @param mapHeight The height of the map, which is the total number of tiles or units vertically
     * in the game world.
     * @param settlementNames A list of strings representing the names of the settlements to be created
     * in the world.
     */
    public static void fillWorld(List<WorldObject> settlementList, int mapWidth, int mapHeight, List<String> settlementNames) {
        validateInput(mapWidth, mapHeight, settlementNames);
        
        Collections.shuffle(settlementNames);
    
        int numSettlements = settlementNames.size();
        int firstHalfX = mapWidth / 2;
        int firstHalfY = mapHeight / 2;
        int quarter = (numSettlements + 3) / 4;
        
        Random random = new Random();
        int minDistance = 100; // Minimum distance between settlements
    
        for (int i = 0; i < numSettlements; i++) {
            int j = i / quarter;
            int x, y;
    
            do {
                x = random.nextInt(firstHalfX) + (j % 2 == 0 ? 10 : firstHalfX - 10);
                y = random.nextInt(firstHalfY) + (j < 2 ? 10 : firstHalfY - 10);
            } while (isOutOfBounds(mapWidth, mapHeight, x, y) || !noObjectInDistance(settlementList, x, y, minDistance));
    
            settlementList.add(WorldObjectFactory.createWorldObject(WorldObjectType.SETTLEMENT, settlementNames.get(i), x, y));
        }
    }

    /**
     * The function validates input parameters for map dimensions and settlement names, throwing an
     * exception if they are invalid.
     * 
     * @param mapWidth The width of the map, which is an integer value.
     * @param mapHeight The height of the map, which is a positive integer value representing the
     * number of rows in the map.
     * @param settlementNames A list of strings representing the names of settlements on a map.
     */
    private static void validateInput(int mapWidth, int mapHeight, List<String> settlementNames) {
        if (mapWidth <= 0 || mapHeight <= 0 || settlementNames.isEmpty()) {
            throw new IllegalArgumentException("Invalid map dimensions or empty settlement names");
        }
    }
  
    /**
     * The function checks if a given point (x,y) is outside the bounds of a map with dimensions
     * (mapX,mapY).
     * 
     * @param mapX The maximum x-coordinate value allowed on the map.
     * @param mapY The maximum value for the Y coordinate on the map.
     * @param x The x-coordinate of a point on a map or grid.
     * @param y The "y" parameter in the method "isOutOfBounds" represents the current y-coordinate of
     * a point on a 2D map. It is used to check if the point is out of bounds of the map, where the map
     * has a width of "mapX" and a height of "map
     * @return The method is returning a boolean value, which is true if the x or y coordinates are
     * less than 0 or greater than the maximum x or y coordinates of the map.
     */
    private static boolean isOutOfBounds(int mapX, int mapY, int x, int y) {
        return (x < 0 || x > mapX || y < 0 || y > mapY); 
    } 

    /**
     * The function checks if there are no WorldObjects within a certain minimum distance from a given
     * point.
     * 
     * @param settlementList A list of WorldObject instances representing settlements.
     * @param x The x-coordinate of the point being checked for distance from other objects.
     * @param y The "y" parameter in the above code represents the y-coordinate of a point in a 2D
     * plane. It is used in the distance calculation to determine the distance between two points.
     * @param minDistance The minimum distance that an object must be from the point (x,y) in order for
     * the method to return true.
     * @return The method is returning a boolean value. It returns true if there are no objects in the
     * given distance from the given coordinates, and false if there is at least one object within the
     * given distance.
     */
    private static boolean noObjectInDistance(List<WorldObject> settlementList ,int x, int y, int minDistance) {
        for (WorldObject settlement : settlementList) {
            if (distance(settlement.getX(),settlement.getY(), x, y) < minDistance) {
                return false;
            }
        }
        return true;
    }

    /**
     * The function calculates the distance between two points in a 2D plane using their coordinates.
     * 
     * @param x1 The x-coordinate of the first point.
     * @param y1 The parameter y1 represents the y-coordinate of the first point in a two-dimensional
     * coordinate system.
     * @param x2 The parameter x2 represents the x-coordinate of the second point in the Cartesian
     * plane.
     * @param y2 The parameter y2 represents the y-coordinate of the second point in the Cartesian
     * plane.
     * @return The method is returning the distance between two points in a 2D plane, given their
     * coordinates (x1, y1) and (x2, y2).
     */
    private static double distance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }


    /*********************
     * Neighbourhandling *
     *************************************************************************************************************************************************************/

    /**
     * This function sets the neighbours of a list of WorldObjects and returns a boolean indicating
     * whether the neighbours were successfully set.
     * 
     * @param settlementList A list of WorldObject instances representing settlements in a game world.
     * @return The method is returning a boolean value, which indicates whether the neighbours of the
     * given list of WorldObjects have been successfully set or not.
     */
    public static boolean setNeighbours(List<WorldObject> settlementList) {
        for (int i = 0; i < settlementList.size(); i++){ 
            System.out.println("Attempt " + i); 
            clearNeighbours(settlementList);
            Collections.shuffle(settlementList);
            setInitialNeighbours(settlementList);
            connectRemainingsettlementList(settlementList);
            if (checkNeighbours(settlementList)) {
                return true;
            }
        }
        return false;
    }

    // clear neighbours
    /**
     * The function clears the neighbours of a list of WorldObjects.
     * 
     * @param settlementList A list of WorldObject instances representing settlements in a game world.
     */
    private static void clearNeighbours(List<WorldObject> settlementList) {
        for (WorldObject settlement : settlementList) {
            settlement.clearNeighbours();
        }
    }

    /**
     * The function checks if a list of WorldObjects has at least 6 objects with 3 neighbors and 1
     * object with 4 neighbors.
     * 
     * @param settlementList A list of WorldObject instances representing settlements.
     * @return The method is returning a boolean value. It returns true if there are at least 6
     * settlements with 3 neighbours and at least 1 settlement with 4 neighbours in the given list of
     * settlements, and false otherwise.
     */
    private static boolean checkNeighbours(List<WorldObject> settlementList) {
        int count3 = 0;
        int count4 = 0;
        for (WorldObject settlement : settlementList) {
            if (settlement.getNeighbours().size() == 3) {
                count3++;
            } else if (settlement.getNeighbours().size() == 4) {
                count4++;
            }
        }
        if (count3 >= 6 && count4 >= 1) {
            return true;
        }
        return false;
    }
    
    /**
     * This function sets initial neighbours for a list of world objects by iterating through each
     * object and adding neighbours until each object has at least two neighbours within a certain
     * search range.
     * 
     * @param settlementList A list of WorldObject instances representing settlements in a world.
     */
    private static void setInitialNeighbours(List<WorldObject> settlementList) {
        for (WorldObject settlement : settlementList) {
            int searchRange = 100;
            while (settlement.getNeighbours().size() < 2) {
                for (WorldObject neighbour : settlementList) {
                    if (shouldAddNeighbour(settlement, neighbour, searchRange, settlementList)) {
                        settlement.addNeighbour(neighbour); 
                        neighbour.addNeighbour(settlement);
                    }
                }
                searchRange += 10;
            }
        }
    }
    
    /**
     * This function determines whether a neighboring world object should be added to a list of
     * settlements based on its distance and search range.
     * 
     * @param settlement A WorldObject representing a settlement.
     * @param neighbor The neighboring WorldObject being checked for potential addition to a list of
     * settlements.
     * @param searchRange The maximum distance between the settlement and its neighbor for the neighbor
     * to be considered as a valid candidate for adding as a neighbor to the settlement.
     * @param settlementList A list of WorldObject representing all the settlements in the world.
     * @return A boolean value indicating whether a neighbor should be added to a list of neighboring
     * settlements based on its distance from a given settlement and a search range.
     */
    private static boolean shouldAddNeighbour(WorldObject settlement, WorldObject neighbor, int searchRange, List<WorldObject> settlementList) {
        int distance = settlement.getDistance(neighbor);
        return  !settlement.equals(neighbor) 
                && distance <= searchRange;         
    }
    
    /**
     * This function connects all the settlements in a list by finding the minimum settlement distance
     * between them.
     * 
     * @param settlementList A list of WorldObject representing settlements that need to be connected.
     */
    private static void connectRemainingsettlementList(List<WorldObject> settlementList) {
        List<WorldObject> notConnectedList;
        while (!(notConnectedList = getNotConnectedList(settlementList)).isEmpty()) {
            connectMinSettlementDistance(settlementList, notConnectedList);
        }
    }
    
    /**
     * The function connects two WorldObject settlements with the shortest distance between them.
     * 
     * @param settlementList A list of WorldObject instances representing settlements that are already
     * connected to each other.
     * @param notConnectedList A list of WorldObjects that are not yet connected to any other
     * WorldObject.
     */
    private static void connectMinSettlementDistance(List<WorldObject> settlementList, List<WorldObject> notConnectedList) {
        List<WorldObject> connectedList = getConnectedList(settlementList, notConnectedList);
    
        WorldObject[] shortestPath = findShortestDistance(notConnectedList, connectedList);
    
        WorldObject settlementA = shortestPath[0];
        WorldObject settlementB = shortestPath[1];
    
        settlementA.addNeighbour(settlementB);  
        settlementB.addNeighbour(settlementA);   
    }
    
    /**
     * The function returns a list of WorldObjects that are connected and have less than 4 neighbors,
     * given a list of settlements and a list of not connected WorldObjects.
     * 
     * @param settlementList A list of WorldObject instances representing settlements that are already
     * connected to each other.
     * @param notConnectedList A list of WorldObjects that are not yet connected to any other
     * WorldObject.
     * @return The method is returning a list of WorldObjects that are connected to each other and have
     * less than 4 neighbours, based on the input parameters of a list of settlements and a list of not
     * connected settlements.
     */
    private static List<WorldObject> getConnectedList(List<WorldObject> settlementList, List<WorldObject> notConnectedList) {
        return settlementList.stream()
                .filter(s -> !notConnectedList.contains(s) && s.getNeighbours().size() < 4)
                .collect(Collectors.toList());
    }

    /**
     * The function returns a list of WorldObjects that are not connected to the main group and have
     * less than 4 neighbors.
     * 
     * @param settlementList A list of WorldObject instances representing settlements.
     * @return The method is returning a list of WorldObjects that are not connected to the main group
     * of settlements and have less than 4 neighbors.
     */
    private static List<WorldObject> getNotConnectedList(List<WorldObject> settlementList) {
        WorldObject settlement = settlementList.get(0);
        Queue<WorldObject> queue = new LinkedList<>();
        Set<WorldObject> visited = new HashSet<>();
    
        queue.add(settlement);
        visited.add(settlement);
    
        while (!queue.isEmpty()) {
            WorldObject currentSettlement = queue.poll();
            for (WorldObject neighbor : currentSettlement.getNeighbours()) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
    
        return settlementList.stream()
                .filter(c -> !visited.contains(c) && c.getNeighbours().size() < 4)
                .collect(Collectors.toList());
    }

    /**
     * This function finds the shortest distance between two lists of WorldObjects.
     * 
     * @param notConnectedList A list of WorldObjects that are not yet connected to any other
     * WorldObject.
     * @param connectedList The "connectedList" parameter is a list of WorldObjects that are already
     * connected to each other. The method is trying to find the shortest distance between these
     * connected objects and the objects in the "notConnectedList" parameter.
     * @return The method is returning an array of two WorldObject instances that have the shortest
     * distance between them. The first element of the array is a WorldObject from the notConnectedList
     * and the second element is a WorldObject from the connectedList.
     */
    private static WorldObject[] findShortestDistance(List<WorldObject> notConnectedList, List<WorldObject> connectedList) {
        return notConnectedList.stream()
                .flatMap(c -> connectedList.stream().map(d -> new WorldObject[]{c, d}))
                .min(Comparator.comparingInt(a -> a[0].getDistance(a[1])))
                .orElseThrow();
    }

    /****************
     * set Factions *
     *************************************************************************************************************************************************************/
    
    /**
     * The function attempts to set factions to a list of settlement objects and returns true if
     * successful, otherwise false.
     * 
     * @param settlementList A list of WorldObject representing the settlements in the world.
     * @param pathList The `pathList` parameter is a list of `WorldObject` objects.
     * @param factionList The factionList parameter is a List of Strings that contains the names of the
     * factions.
     * @return The method is returning a boolean value.
     */
    public static boolean generateFactions(List<WorldObject> settlementList, List<WorldObject> pathList, List<String> factionList) { 
        int maxAttempts = 10;
        
        for (int i = 0; i < maxAttempts; i++) {
            clearFactions(settlementList);
            if (setFactionToSettlementList(settlementList, factionList)) {
                setCapital(settlementList,factionList);
                return true;
            }
        }

        return false;
    }

    /**
     * The function sets the capital of each faction in a list of settlements based on the number of
     * neighboring settlements belonging to the same faction.
     * 
     * @param settlementList A list of WorldObject objects representing different settlements in a
     * world.
     * @param factionList A list of factions represented as strings.
     */
    private static void setCapital(List<WorldObject> settlementList, List<String> factionList) {
        // set capital for each faction based on the number of neighboring settlements belonging to the same faction and only that faction and if possible no other faction as neighbor
        // ignore neutral faction
        for (String faction : factionList) {
            if (faction.equals("Neutral")) {
                continue;
            }
            List<WorldObject> factionSettlements = settlementList.stream()
                    .filter(settlement -> settlement.getFaction().equals(faction))
                    .collect(Collectors.toList());
            
            WorldObject capital = factionSettlements.stream()
                    .max(Comparator.comparingInt(settlement -> settlement.getNeighbours().stream()
                            .filter(neighbor -> neighbor.getFaction().equals(faction))
                            .collect(Collectors.toList()).size()))
                    .orElseThrow( 
                            () -> new IllegalStateException("No settlement found for faction " + faction)
                     );
           
            capital.setCapital(true);
        }
        
    }

    /**
     * The function clears the faction of all WorldObjects in a given list.
     * 
     * @param settlementList A List of WorldObject instances representing settlements.
     */
    private static void clearFactions(List<WorldObject> settlementList) {
        settlementList.forEach(settlement -> settlement.setFaction(null));
    }

    /**
     * This function sets factions to settlement lists based on the number of settlementList and factions
     * provided.
     * 
     * @param settlementList A list of WorldObject representing settlementList in a game.
     * @param factionList A list of strings representing the different factions in the game, including
     * "Neutral".
     * @return The method is returning a boolean value. It returns true if all the factions have been
     * successfully assigned to the settlements in the list of settlements, and false if there are not
     * enough settlements with no faction to assign to a faction.
     */
    private static boolean setFactionToSettlementList(List<WorldObject> settlementList, List<String> factionList) {
        List<String> availableFactions = factionList.stream()
            .filter(faction -> !faction.equals("Neutral"))
            .collect(Collectors.toList());

        int settlementsPerFaction = settlementList.size() / availableFactions.size(); 
        Map<String, Integer> factionMap = new HashMap<>(); 
        for (int i = 0 ; i < availableFactions.size() - 1 ; i++) {
            factionMap.put(availableFactions.get(i), ThreadLocalRandom.current().nextInt(2) + settlementsPerFaction);
        }

        int remainingSettlements = settlementList.size() - factionMap.values().stream().reduce(0, Integer::sum);
        factionMap.put(availableFactions.get(availableFactions.size() - 1), remainingSettlements); 

        for (Map.Entry<String, Integer> entry : factionMap.entrySet()) {
            List<WorldObject> settlementsWithNoFaction = getWayWithNoFaction(settlementList, entry.getValue()); // 
            if (settlementsWithNoFaction == null) {
                return false;
            } else {
                setFactionToList(entry.getKey(), settlementsWithNoFaction , entry.getValue());
            }
        }
        return checkFactions(settlementList);
    } 

    /**
     * This function sets a given faction to a specified number of WorldObjects in a List that have no
     * faction.
     * 
     * @param faction A String representing the faction that will be set to the WorldObjects in the
     * list.
     * @param settlementsWithNoFaction A List of WorldObject instances that have no faction assigned to
     * them.
     * @param count The number of settlements in the list that need to have their faction set to the
     * specified faction.
     */
    private static void setFactionToList(String faction, List<WorldObject> settlementsWithNoFaction , int count){
        for (int i = 0; i < count; i++) {
            settlementsWithNoFaction.get(i).setFaction(faction);
        }
    }

    /**
     * This function returns a list of WorldObjects with no faction, connected to each other through
     * their neighbors, with a specified count.
     * 
     * @param settlementList A list of WorldObject instances representing settlements.
     * @param count The number of WorldObjects to be returned in the resulting list.
     * @return The method returns a List of WorldObjects that have no faction and are connected to each
     * other through a chain of neighboring WorldObjects. The size of the list is determined by the
     * "count" parameter passed to the method. If no such list can be found, the method returns null.
     */
    private static List<WorldObject> getWayWithNoFaction( List <WorldObject> settlementList, int count) {
        List<WorldObject> listWithNoFaction = settlementList.stream()
                .filter(c -> c.getFaction() == null)
                .collect(Collectors.toList());

        Collections.shuffle(listWithNoFaction);

        for (WorldObject settlementWithNoFaction : listWithNoFaction) { 
            Queue<WorldObject> queue = new LinkedList<>();
            Set<WorldObject> visited = new HashSet<>();  

            queue.add(settlementWithNoFaction);
            visited.add(settlementWithNoFaction);

            int currentCount  = 1;

            while (!queue.isEmpty()) {
                WorldObject currentSettlement = queue.poll();

                for (WorldObject neighbor : currentSettlement.getNeighbours()) {

                    if (neighbor.getFaction() == null && visited.add(neighbor)) {
                        queue.add(neighbor);
                        currentCount ++;

                        if (currentCount  == count) {
                            return new ArrayList<>(visited);
                        }
                    }
                }
            }       
        }
        return null;
    }

    /**
     * The function checks if all factions in a list of settlementList have contiguous borders.
     * 
     * @param settlementList a list of WorldObject instances representing settlementList in a game world.
     * @return A boolean value is being returned.
     */
    public static boolean checkFactions(List<WorldObject> settlementList) {
        Set<String> factionSet = new HashSet<>();
        for (WorldObject settlement : settlementList) {
            factionSet.add(settlement.getFaction());
        }
        List<String> factionList = new ArrayList<>(factionSet);
         
        for (String faction : factionList) {
            if (!checkFactionBorders(factionList, faction, settlementList)) {
                return false;
            }
        }
        return true;
    } 

    /**
     * This function checks if a given faction has at least two settlementList bordering with different
     * factions.
     * 
     * @param factionList A list of all the factions in the game.
     * @param faction The faction for which we want to check the borders.
     * @param settlementList a list of WorldObject instances representing settlementList in a game world
     * @return The method is returning a boolean value.
     */
    public static boolean checkFactionBorders(List<String> factionList, String faction, List<WorldObject> settlementList) {
        Set<String> remainingFactions = factionList.stream().filter(f -> !f.equals(faction)).collect(Collectors.toSet()) ;
        int borderSettlementCount = 0;
        int matchingNeighborCount = 0;

        List<WorldObject> factionSettlements = settlementList.stream()
                .filter(settlement -> settlement.getFaction().equals(faction))
                .collect(Collectors.toList());
    
        for (WorldObject settlement : factionSettlements) {
            for (WorldObject neighbor : settlement.getNeighbours()) {
                if (remainingFactions.remove(neighbor.getFaction())) {         
                    matchingNeighborCount = 1;
                }
            }
            borderSettlementCount += matchingNeighborCount;
            matchingNeighborCount = 0;
        }

        return borderSettlementCount > 1;
    }

     /************
     * add Paths *
     *************************************************************************************************************************************************************/
    
     /**
      * The function adds paths between settlements and their neighbours to a list of paths.
      * 
      * @param settlementList A list of WorldObject instances representing settlements.
      * @param pathList A list of WorldObject representing the paths between settlements.
      */
      public static void setPaths(List<WorldObject> settlementList, List<WorldObject> pathList) {
        for (WorldObject settlement : settlementList) {
            for (WorldObject neighbour : settlement.getNeighbours()) {
                if (!pathList.contains(neighbour)) {
                    WorldObject path = makePath(settlement, neighbour);

                    settlement.removeNeighbour(neighbour);
                    settlement.addNeighbour(path);

                    neighbour.removeNeighbour(settlement);
                    neighbour.addNeighbour(path);

                    pathList.add(path);
                }
            }
        }
        setPathFactions(pathList);
    }

    /**
     * The function creates a path object between two settlementList by calculating the midpoint
     * coordinates and using a WorldObjectFactory.
     * 
     * @param settlementA A WorldObject representing one of the settlementList connected by the path.
     * @param settlementB The second settlement object that the path is being created between.
     * @return The method is returning a WorldObject, which is a path object created using the
     * coordinates of two settlementList and a name that indicates the connection between them.
     */
    private static WorldObject makePath(WorldObject settlementA, WorldObject settlementB) {
        int pathX = (settlementA.getX() + settlementB.getX()) / 2;
        int pathY = (settlementA.getY() + settlementB.getY()) / 2;

        WorldObject path = WorldObjectFactory.createWorldObject(WorldObjectType.PATH, "Path: " + settlementA.getName() + " - " + settlementB.getName(), pathX, pathY);
        path.addNeighbour(settlementA);
        path.addNeighbour(settlementB);
        return path;
    }

     /**
     * The function sets the faction of a list of WorldObject paths based on the factions of their
     * neighboring WorldObjects.
     * 
     * @param pathList A list of WorldObject instances representing paths in a game world.
     */
    private static void setPathFactions(List<WorldObject> pathList){
        for (WorldObject path : pathList) {
            if (path.getNeighbours().get(0).getFaction() == path.getNeighbours().get(1).getFaction()) {
                path.setFaction(path.getNeighbours().get(0).getFaction());
            }
            else {
                path.setFaction("Neutral");
            }
        }
    }

    /**************************
     * fill Objects with NPCs *
     *************************************************************************************************************************************************************/
    
    /**
     * The function populates a list of settlementList and a list of paths with different types of
     * objects.
     * 
     * @param settlementList A list of WorldObject instances representing settlementList in a game world.
     * @param pathList A list of WorldObjects representing paths in a game world.
     */
    public static void populateWorld(List<WorldObject> settlementList, List<WorldObject> pathList) {
        for (WorldObject settlement : settlementList) {
            populateSettlement(settlement);
        }

        Collections.shuffle(settlementList);
        int caravanCount = 3;
        for (WorldObject settlement : settlementList) {
            if (caravanCount > 0) {
                createAndAddNPC(NPCType.CARAVAN, settlement.getFaction(), settlement);
                caravanCount--;
            }
        }

        for (WorldObject path : pathList) {
            populatePathWithBandit(path);
        }
    }
    
    /**
     * The function populates a settlement with NPCs of different types, including a leader, soldiers,
     * civilians, traders, and either a caravan or mercenary.
     * 
     * @param settlement A WorldObject representing a settlement in the game.
     */
    private static void populateSettlement(WorldObject settlement) {
        final int NUM_NPC = 10;
        final int NUM_SOLDIERS = 3;
        final int NUM_CIVILIANS = 3;
        final int NUM_TRADESMEN = 2;
    
        createAndAddNPC(NPCType.LEADER, settlement.getFaction(), settlement);
    
        for (int i = 0; i < NUM_NPC; i++) {
            NPCType type;
    
            if (i <= NUM_SOLDIERS) {
                type = NPCType.SOLDIER;
            } else if (i <= NUM_SOLDIERS + NUM_CIVILIANS) {
                type = NPCType.CIVILIAN;
            } else if (i <= NUM_NPC - NUM_TRADESMEN) {
                type = NPCType.TRADER;
            } else {
                double randomValue = Math.random();
                type = randomValue < 0.8 ? NPCType.MERCENARY : NPCType.BANDIT;
            }
    
            createAndAddNPC(type, settlement.getFaction(), settlement);
        }
    }

    private static void createAndAddNPC(NPCType type, String faction, WorldObject settlement) {
        settlement.addNPC(NPCFactory.createNPC(type, faction, settlement));
    }

    /**
     * This function populates a neutral path with a bandit NPC with a 50% chance.
     * 
     * @param path The parameter "path" is a WorldObject, which is an object representing a location or
     * area in the game world. The method "populatePathWithBandit" adds a Bandit NPC to the path if the
     * path's faction is "Neutral" and a random number is less than 0.
     */
    private static void populatePathWithBandit(WorldObject path) {
        if (path.getFaction() == "Neutral" && Math.random() < 0.5) {
            createAndAddNPC(NPCType.BANDIT, path.getFaction(), path);
        }      
    }
}    