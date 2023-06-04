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
import java.util.stream.Collectors;

import com.rootgame.model.NPC.NPCFactory;
import com.rootgame.model.NPC.NPCType;
import com.rootgame.model.World.WorldObjects.WorldObjectType;
import com.rootgame.model.World.WorldObjects.WorldObject;
import com.rootgame.model.World.WorldObjects.WorldObjectFactory;

public class GenerateWorld {

    private static Random random = new Random();


    /************************
     * World Generation API *
     ************************/
        
    /*****************************
     * fill World with Clearings *
     ****************************/

    
    /**
     * This function fills a world with clearings at random locations, ensuring that they are not too
     * close to each other or out of bounds.
     * 
     * @param clearings A List of Clearing objects that will be filled with new Clearing objects based
     * on the other parameters.
     * @param mapX The width of the map in units (integer value).
     * @param mapY The height of the map, measured in units (not specified what units).
     * @param clearingNames An array of Strings representing the names of the clearings to be created.
     * 
     * @throws IllegalArgumentException if the map dimensions are invalid or if the clearing names
     */
    public static void fillWorld(List<WorldObject> settlements,int mapX, int mapY, List<String> settlementNames) {
        int listSize = settlementNames.size();
        if (mapX <= 0 || mapY <= 0 || listSize == 0) {
            throw new IllegalArgumentException("Invalid map dimensions or clearing names");
        }
        
        Collections.shuffle(settlementNames);

        int firstHalfX = mapX / 2;
        int firstHalfY = mapY / 2;
        int quarter = (listSize + 3) / 4;
        
        for (int i = 0; i < listSize; i++) {
            int j = i / quarter;
            int x, y;

            do {
                x = random.nextInt(firstHalfX ) + (j % 2 == 0 ? 10 : firstHalfX-10);
                y = random.nextInt(firstHalfY ) + (j < 2 ? 10 : firstHalfY-10);
            } while (isOutOfBounds(mapX, mapY, x, y) || !noObjectInDistance(settlements,x, y, 100));

            settlements.add(WorldObjectFactory.createWorldObject(WorldObjectType.SETTLEMENT,settlementNames.get(i), x, y));
        }
    }

    /**
     * The function checks if the given coordinates are out of bounds within a specified size.
     * 
     * @param x The x-coordinate of a point in a two-dimensional grid.
     * @param y The y parameter represents the vertical coordinate of a point in a two-dimensional
     * space. It is used in the isOutOfBounds method to check if the point is outside the boundaries of
     * a square grid with a size of "size".
     * @return The method is returning a boolean value. It returns `false` if the given `x` and `y`
     * coordinates are out of bounds, and `true` otherwise.
     */
    private static boolean isOutOfBounds(int mapX, int mapY, int x, int y) {
        return (x < 0 || x > mapX || y < 0 || y > mapY); 
    } 


    /*******************************
     * Neighbourhandling/add Paths *
     *************************************************************************************************************************************************************/

    /**
     * This function sets the neighbours of a list of WorldObjects and returns a boolean indicating
     * whether the neighbours were successfully set.
     * 
     * @param settlements A list of WorldObject instances representing settlements in a game world.
     * @return The method is returning a boolean value, which indicates whether the neighbours of the
     * given list of WorldObjects have been successfully set or not.
     */
    public static boolean setNeighbours(List<WorldObject> settlements) {
        boolean neighboursSet = false;
        System.out.println("Setting neighbours");
        setInitialNeighbours(settlements);
        connectRemainingSettlements(settlements);
        neighboursSet = checkNeighbours(settlements);
        
        System.out.println(neighboursSet? "Neighbours set" : "Neighbours not set, trying again");            
    
        return neighboursSet;
    }

    /**
     * The function checks if a list of WorldObjects has at least 6 objects with 3 neighbors and 1
     * object with 4 neighbors.
     * 
     * @param settlements A list of WorldObject instances representing settlements.
     * @return The method is returning a boolean value. It returns true if there are at least 6
     * settlements with 3 neighbours and at least 1 settlement with 4 neighbours in the given list of
     * settlements, and false otherwise.
     */
    private static boolean checkNeighbours(List<WorldObject> settlements) {
        int count3 = 0;
        int count4 = 0;
        for (WorldObject settlement : settlements) {
            if (settlement.getNeighbours().size() == 3) {
                count3++;
            } else if (settlement.getNeighbours().size() == 4) {
                count4++;
            }
        }
        return count3 >= 6 && count4 >= 1;
    }
    
    /**
     * This function sets initial neighbours for a list of world objects by iterating through each
     * object and adding neighbours until each object has at least two neighbours within a certain
     * search range.
     * 
     * @param settlements A list of WorldObject instances representing settlements in a world.
     */
    private static void setInitialNeighbours(List<WorldObject> settlements) {
        for (WorldObject settlement : settlements) {
            int searchRange = 100;
            while (settlement.getNeighbours().size() < 2) {
                for (WorldObject neighbour : settlements) {
                    if (shouldAddNeighbour(settlement, neighbour, searchRange, settlements)) {
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
     * @param settlements A list of WorldObject representing all the settlements in the world.
     * @return A boolean value indicating whether a neighbor should be added to a list of neighboring
     * settlements based on its distance from a given settlement and a search range.
     */
    private static boolean shouldAddNeighbour(WorldObject settlement, WorldObject neighbor, int searchRange, List<WorldObject> settlements) {
        int distance = settlement.getDistance(neighbor);
        return  !settlement.equals(neighbor) 
                && distance <= searchRange;         
    }
    
    /**
     * This function connects all the settlements in a list by finding the minimum clearing distance
     * between them.
     * 
     * @param settlements A list of WorldObject representing settlements that need to be connected.
     */
    private static void connectRemainingSettlements(List<WorldObject> settlements) {
        List<WorldObject> notConnectedList;
        while (!(notConnectedList = getNotConnectedList(settlements)).isEmpty()) {
            connectMinClearingDistance(settlements, notConnectedList);
        }
    }
    
    /**
     * The function connects two WorldObject settlements with the shortest distance between them.
     * 
     * @param settlements A list of WorldObject instances representing settlements that are already
     * connected to each other.
     * @param notConnectedList A list of WorldObjects that are not yet connected to any other
     * WorldObject.
     */
    private static void connectMinClearingDistance(List<WorldObject> settlements, List<WorldObject> notConnectedList) {
        List<WorldObject> connectedList = getConnectedList(settlements, notConnectedList);
    
        WorldObject[] shortestPath = findShortestDistance(notConnectedList, connectedList);
    
        WorldObject settlementA = shortestPath[0];
        WorldObject settlementB = shortestPath[1];
    
        settlementA.addNeighbour(settlementB);  
        settlementB.addNeighbour(settlementA);   
    }
    
    /**
     * The function returns a list of clearings that are connected and have less than four neighbors,
     * excluding those in a given not connected list.
     * 
     * @param clearings A list of Clearing objects representing all the clearings in a game board.
     * @param notConnectedList A list of Clearing objects that are not yet connected to the rest of the
     * network.
     * @return The method is returning a list of Clearing objects that are connected to other Clearing
     * objects and have less than 4 neighbors, based on the input parameters of a list of all Clearing
     * objects and a list of Clearing objects that are not connected to any other Clearing objects.
     */
    private static List<WorldObject> getConnectedList(List<WorldObject> settlements, List<WorldObject> notConnectedList) {
        return settlements.stream()
                .filter(s -> !notConnectedList.contains(s) && s.getNeighbours().size() < 4)
                .collect(Collectors.toList());
    }

    /**
     * The function returns a list of Clearing objects that are not connected to the main group of
     * Clearings and have less than 4 neighbors.
     * 
     * @param clearings A list of Clearing objects representing the clearings on a game board.
     * @return The method is returning a list of Clearing objects that are not connected to the main
     * group of Clearing objects and have less than 4 neighbors.
     */
    private static List<WorldObject> getNotConnectedList(List<WorldObject> settlements) {
        WorldObject settlement = settlements.get(0);
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
    
        return settlements.stream()
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

     /************
     * add Paths *
     *************************************************************************************************************************************************************/
    
     /**
      * The function adds paths between settlements and their neighbours to a list of paths.
      * 
      * @param settlements A list of WorldObject instances representing settlements.
      * @param paths A list of WorldObject representing the paths between settlements.
      */
     public static void setPaths(List<WorldObject> settlements, List<WorldObject> paths) {
        for (WorldObject settlement : settlements) {
            for (WorldObject neighbour : settlement.getNeighbours()) {
                if (!paths.contains(neighbour)) {
                    WorldObject path = makePath(settlement, neighbour);

                    settlement.removeNeighbour(neighbour);
                    settlement.addNeighbour(path);

                    neighbour.removeNeighbour(settlement);
                    neighbour.addNeighbour(path);

                    paths.add(path);
                }
            }
        }
        setPathFactions(paths);
    }

    /**
     * The function creates a path object between two settlements by calculating the midpoint
     * coordinates and using a WorldObjectFactory.
     * 
     * @param settlementA A WorldObject representing one of the settlements connected by the path.
     * @param settlementB The second settlement object that the path is being created between.
     * @return The method is returning a WorldObject, which is a path object created using the
     * coordinates of two settlements and a name that indicates the connection between them.
     */
    private static WorldObject makePath(WorldObject settlementA, WorldObject settlementB) {
        int pathX = (settlementA.getX() + settlementB.getX()) / 2;
        int pathY = (settlementA.getY() + settlementB.getY()) / 2;

        WorldObject path = WorldObjectFactory.createWorldObject(WorldObjectType.PATH, "Path: " + settlementA.getName() + " - " + settlementB.getName(), pathX, pathY);
        path.addNeighbour(settlementA);
        path.addNeighbour(settlementB);
        return path;
    }
    
    /**************************
     * fill Objects with NPCs *
     *************************************************************************************************************************************************************/
    
     /**
     * The function populates each clearing in a world with a faction and its inhabitants.
     */
    public static void populateWorld(List<WorldObject> settlements, List<WorldObject> paths) {
        for (WorldObject settlement : settlements) {
            populateSettlement(settlement);
        }
        for (WorldObject path : paths) {
            populatePathWithBandit(path);
        }
    }
    
    /**
     * The function populates a clearing with a random number of NPCs of different types and adds a
     * leader NPC to the clearing.
     * 
     * @param clearing an object of type Clearing, which represents a location in a game world where
     * non-player characters (NPCs) can be placed.
     */
    private static void populateSettlement(WorldObject settlement) {
       
        NPCType type = NPCType.LEADER;
        settlement.addNPC(NPCFactory.createNPC(type, settlement.getFaction(), settlement));            

        for (int i = 0; i < 10; i++) {
            switch(i){
                case 0: case 1: case 2: case 3:
                    type = NPCType.SOLDIER;
                    break;
                case 4: case 5: case 6: case 7:
                    type = NPCType.CIVILIAN;
                    break;
                case 8: case 9:
                    type = NPCType.TRADER;
                    break;
                default:
                    break;
            }
            settlement.addNPC(NPCFactory.createNPC(type, settlement.getFaction(), settlement));            

        }
        if (Math.random() < 0.5) {
            type = Math.random() < 0.5? NPCType.CARAVAN : NPCType.MERCENARY;
        }
        settlement.addNPC(NPCFactory.createNPC(type, settlement.getFaction(), settlement));
    }

    private static void populatePathWithBandit(WorldObject path) {
        if (path.getFaction() == "Neutral" && Math.random() < 0.5) {
            path.addNPC(NPCFactory.createNPC(NPCType.BANDIT, path.getFaction(), path));
        }      
    }

    /****************
     * set Factions *
     *************************************************************************************************************************************************************/
    
    /**
     * This function sets factions to a list of clearings and paths, and retries up to 10 times if it
     * fails.
     * 
     * @param clearings A list of Clearing objects representing the different locations on a game board
     * where players can move and take actions.
     * @param paths A list of Path objects, which represent the connections between Clearing objects in
     * a game.
     * @return The method is returning a boolean value.
     */
    public static boolean setFactionsToWorld(List<WorldObject> settlements, List<WorldObject> paths, List<String> factions) { 
        System.out.println("Setting factions...");
        int count = 0;
        while (!setFactionToClearingList(settlements, factions) && count < 10) {
            System.out.println("Retrying to set factions...");
            settlements.forEach(c -> c.setFaction(null));
            count++;
        }

        if (count >= 10) {
            System.out.println("Factions could not be set.");
            return false;
        }
        return checkFactions(settlements);
    }

    /**
     * The function checks if the factions in a list of clearings have borders with each other.
     * 
     * @param clearings A list of Clearing objects that represent different areas on a game board.
     * @return A boolean value is being returned.
     */
    public static boolean checkFactions(List<WorldObject> settlements) {
        List<String> factions = new ArrayList<>();
        for (WorldObject settlement : settlements) {
            if (!factions.contains(settlement.getFaction())) {
                factions.add(settlement.getFaction());
            }
        }
         
        for (String faction : factions) {
            if (!checkFactionBorders(factions, faction, settlements)) {
                return false;
            }
        }
        
        return true;
    } 

    /**
     * The function checks if a given faction borders all other factions in a list of factions in a
     * list of clearings.
     * 
     * @param factions A list of all the factions in the game.
     * @param faction The faction for which we want to check if its borders are clear of other
     * factions.
     * @param clearings A list of Clearing objects representing the different clearings on a game
     * board.
     * @return The method is returning a boolean value indicating whether all the factions in the given
     * list of factions are bordering the clearings controlled by the given faction.
     */
    public static boolean checkFactionBorders(List<String> factions, String faction, List<WorldObject> settlements) {
        List<String> remainingFactions = new ArrayList<>(factions);
        remainingFactions.remove(faction);
        int count = 0;
        int factionCount = 0;
    
        List<WorldObject> factionClearings = settlements.stream()
                .filter(c -> c.getFaction() == faction)
                .collect(Collectors.toList());
    
        for (WorldObject settlement : factionClearings) {
            for (WorldObject neighbor : settlement.getNeighbours()) {
                if (remainingFactions.contains(neighbor.getFaction())) {
                    remainingFactions.remove(neighbor.getFaction());
                    count = 1;
                }

            }
            factionCount += count;
            count = 0;
        }
    
        return (remainingFactions.size() == 0 && factionCount >= 2);
    }

    /**
     * The function sets a random number of factions to a list of clearings, ensuring that each faction
     * has the appropriate number of clearings.
     * 
     * @param clearings A list of Clearing objects representing the different clearings on the game
     * board.
     * @return A boolean value is being returned.
     */
    private static boolean setFactionToClearingList(List<WorldObject> settlements, List<String> factions) {
        factions.remove("Neutral");
        int factionRatio = settlements.size() / factions.size(); 
        Map<String, Integer> factionMap = new HashMap<>(); 
        for (int i = 0 ; i < factions.size() - 1 ; i++) {
            factionMap.put(factions.get(i), random.nextInt(2) + factionRatio);
        }

        // The code is adding a key-value pair to a map called `factionMap`. The key is the last element of a
        // list called `factions`, and the value is the difference between the size of a list called
        // `clearings` and the sum of all the values in `factionMap`. The `reduce` method is used to calculate
        // the sum of all the values in `factionMap`.
        factionMap.put(factions.get(factions.size() - 1), 
        settlements.size() - factionMap.values().stream().reduce(0, Integer::sum)
        ); 

        for (Map.Entry<String, Integer> entry : factionMap.entrySet()) {
            List<WorldObject> clearingsWithNoFaction = getWayWithNoFaction(settlements, entry.getValue());
            if (clearingsWithNoFaction == null) {
                return false;
            }
            setFactionToList(entry.getKey(), clearingsWithNoFaction , entry.getValue());
        }
        return true;
    } 

    /**
     * This function sets a specified faction to a certain number of clearings in a list that currently
     * have no faction.
     * 
     * @param faction The faction that we want to set to the clearings in the list.
     * @param clearingsWithNoFaction A list of Clearing objects that currently have no faction assigned
     * to them.
     * @param count The number of clearings to set the faction for.
     */
    private static void setFactionToList(String faction, List<WorldObject> clearingsWithNoFaction , int count){
        for (int i = 0; i < count; i++) {
            clearingsWithNoFaction.get(i).setFaction(faction);
        }
    }

    /**
     * This function returns a list of clearings with no faction that are connected to each other and
     * have a specified count.
     * 
     * @param clearings A list of Clearing objects representing the game board clearings.
     * @param count The number of Clearings that should be returned in the List.
     * @return The method returns a List of Clearing objects that have no faction and are connected to
     * each other through their neighbors. The size of the list is determined by the "count" parameter
     * passed to the method. If no such list can be found, the method returns null.
     */
    private static List<WorldObject> getWayWithNoFaction( List <WorldObject> settlements, int count) {
        List <WorldObject> listWithNoFaction = settlements.stream()
                .filter(c -> c.getFaction() == null)
                .collect(Collectors.toList());

        Collections.shuffle(listWithNoFaction);

        for (WorldObject settlementWithNoFaction : listWithNoFaction) { 

            Queue<WorldObject> queue = new LinkedList<>();
            Set<WorldObject> visited = new HashSet<>();      
            queue.add(settlementWithNoFaction);
            visited.add(settlementWithNoFaction);

            while (!queue.isEmpty()) {

                WorldObject currentClearing = queue.poll();

                for (WorldObject neighbor : currentClearing.getNeighbours()) {

                    if (!visited.contains(neighbor) && neighbor.getFaction() == null) {

                        queue.add(neighbor);
                        visited.add(neighbor);

                        if (visited.size() == count) {

                            return new ArrayList<>(visited);
                        }
                    }
                }
            }       
        }
        return null;
    }

    /**
     * The function sets the faction of each path in a list based on the factions of its neighboring
     * paths.
     * 
     * @param paths A list of Path objects.
     */
    private static void setPathFactions(List<WorldObject> paths){
        for (WorldObject path : paths) {
            if (path.getNeighbours().get(0).getFaction() == path.getNeighbours().get(1).getFaction()) {
                path.setFaction(path.getNeighbours().get(0).getFaction());
            }
            else {
                path.setFaction("Neutral");
            }
        }
    }

    
        

    /********************
     * helper Functions *
     *************************************************************************************************************************************************************/
    
    /**
     * The function checks if there are any clearings within a certain search range of a given point.
     * 
     * @param x The x-coordinate of the location being checked for clearing availability.
     * @param y The parameter "y" represents the y-coordinate of the location being checked for
     * clearing availability.
     * @param searchRange The maximum distance (in number of tiles) from the given (x,y) coordinates
     * within which the method will search for any existing clearings. If there is any clearing within
     * this range, the method will return false, indicating that the clearing is not free. Otherwise,
     * it will return true, indicating
     * @return The method is returning a boolean value, either true or false.
     */
    private static boolean noObjectInDistance(List<WorldObject> clearings ,int x, int y, int searchRange) {
        for (WorldObject clearing : clearings) {
            if (!checkObjectInRange(clearing, x, y, searchRange)) {
                return false;
            }
        }
        return true;
    }

    /**
     * The function checks if there are no objects within a certain distance from a given point.
     * 
     * @param worldobject This is an object of the class WorldObject, which contains information about
     * the position and other properties of an object in a virtual world.
     * @param x The x-coordinate of the point from which the distance to the WorldObject is being
     * measured.
     * @param y The y-coordinate of the point being checked for distance from the WorldObject.
     * @param searchRange The maximum distance from the point (x,y) within which the function will
     * return false if there is a WorldObject present.
     * @return The method returns a boolean value, either true or false.
     */
    private static boolean checkObjectInRange (WorldObject worldobject, int x, int y, int searchRange) {
        int dX = worldobject.getX();
        int dY = worldobject.getY();
        int distance = (int) Math.sqrt(Math.pow(Math.abs(x - dX), 2) + Math.pow(Math.abs(y - dY), 2));
        if (distance <= searchRange) {
            return false;
        }
        return true;
    }
}
