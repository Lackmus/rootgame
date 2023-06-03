package com.rootgame.model.World;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.NPC.NPCType;
import com.rootgame.model.World.WorldObjects.WorldObject;
import com.rootgame.model.World.WorldObjects.WorldObjectFactory;
import com.rootgame.model.World.WorldObjects.WorldObjectType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadWorld {

    /**
     * This function loads data from a JSON file and populates lists of Clearing and Path objects with
     * the parsed data.
     * 
     * @param nameOfJson The name of the JSON file to be loaded.
     * @param clearings A list of Clearing objects that will be populated with data from the JSON file.
     * @param paths A List of Path objects that will be populated with data from the JSON file.
     */
    public static void loadFromJson(String nameOfJson, List<WorldObject> settlements, List<WorldObject> paths) {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(nameOfJson)));
            JSONArray objectArray = new JSONArray(jsonData);

            Map<String, List<String>> neighbourMap = new HashMap<>();
            Map<String, WorldObject> settlementLookupMap = new HashMap<>();
            Map<String, List<String>> destinationPathMap = new HashMap<>();

            for (int i = 0; i < objectArray.length(); i++) {
                JSONObject objectJson = objectArray.getJSONObject(i);

                String name = objectJson.getString("name");
                int x = objectJson.getInt("x");
                int y = objectJson.getInt("y");
                WorldObject.Type type = WorldObject.Type.valueOf(objectJson.getString("type"));

                WorldObject worldObject;
                switch (type) {
                    case SETTLEMENT:
                         worldObject = WorldObjectFactory.createWorldObject(WorldObjectType.SETTLEMENT, name, x, y);
                        settlements.add(worldObject);
                        settlementLookupMap.put(name, worldObject);
                        break;
                    case PATH:
                        worldObject = WorldObjectFactory.createWorldObject(WorldObjectType.PATH, name, x, y);
                        paths.add(worldObject);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid object type: " + type);
                }

                worldObject.setFaction(objectJson.getString("faction"));
                worldObject.setCombatStrength(objectJson.getInt("combatStrength"));
                worldObject.setMarketValue(objectJson.getInt("marketValue"));
                worldObject.setLoyalty(objectJson.getInt("loyalty"));
                worldObject.setBesieged(objectJson.getBoolean("besieged"));
                worldObject.setSiegeTimer(objectJson.getInt("siegeTimer"));
                worldObject.setRuined(objectJson.getBoolean("ruined"));
                worldObject.setRuinTimer(objectJson.getInt("ruinTimer"));
                worldObject.setCapital(objectJson.getBoolean("isCapital"));
                worldObject.setCurrentPopulation(objectJson.getInt("currentPopulation"));
                worldObject.setDescription(objectJson.getString("description"));

                List<NPC> npcs = parseNPCs(objectJson, worldObject, destinationPathMap);
                worldObject.setNPCs(npcs);
      
                List<String> neighbours = parseNeighbours(objectJson);
                neighbourMap.put(name, neighbours);
            }

            addNeighbours(settlements, paths, settlementLookupMap, neighbourMap);
            setNPCPaths(settlements, paths, destinationPathMap, settlementLookupMap);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * The function parses a JSON object to extract a list of neighbours and returns it.
     * 
     * @param objectJson A JSONObject that contains information about a location, including its
     * neighbors.
     * @return The method is returning a List of Strings, which represents the neighbours of a given
     * object in JSON format.
     */
    private static List<String> parseNeighbours (JSONObject objectJson) throws JSONException {
        List<String> neighbours = new ArrayList<>();
        if (objectJson.has("neighbours")) {
            JSONArray neighboursJson = objectJson.getJSONArray("neighbours");
            for (int j = 0; j < neighboursJson.length(); j++) {
                String neighbour = neighboursJson.getString(j);
                neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    /**
     * This function parses a JSON object to create a list of NPC objects.
     * 
     * @param objectJson A JSONObject containing information about NPCs.
     * @return The method is returning a List of NPC objects.
     */
    private static List<NPC> parseNPCs(JSONObject objectJson, WorldObject worldObject, Map<String, List<String>> destinationPathMap) throws JSONException {

        List<NPC> npcs = new ArrayList<>();
        if (objectJson.has("npcs")) {
            JSONArray npcArray = objectJson.getJSONArray("npcs");
            for (int j = 0; j < npcArray.length(); j++) {
                JSONObject npcJson = npcArray.getJSONObject(j);
                int npcCombatStrength = npcJson.getInt("combatStrength");
                int npcMarketValue = npcJson.getInt("marketValue");


                NPC npc = new NPC(npcJson.getString("name"), npcJson.getString("race"), NPCType.valueOf(npcJson.getString("type")),
                                  npcJson.getString("faction"), worldObject);

                npc.setLoyalty(npcJson.getInt("loyalty"));
                npc.setDescription(npcJson.getString("description"));
                npc.setCombatStrength(npcCombatStrength);
                npc.setMarketValue(npcMarketValue);
                //npc.setOrigin(worldObject);
                //npc.setCurrentLocation(worldObject);
                if (npcJson.has("destinationPath")) {
                    JSONArray destinationPathArray = npcJson.getJSONArray("destinationPath");
                    List<String> destinationPath = new ArrayList<>();
                    for (int k = 0; k < destinationPathArray.length(); k++) {
                        String destinationName = destinationPathArray.getString(k);
                        destinationPath.add(destinationName);
                    }
                    destinationPathMap.put(npc.getName(), destinationPath);
                }
                npcs.add(npc);
            }
        }
        return npcs;
    }

    /**
     * The function adds neighbours to clearings and paths based on a lookup map and a neighbour map.
     * 
     * @param clearings A list of Clearing objects representing the different locations on a game
     * board.
     * @param paths A list of Path objects representing the paths between Clearings in a game.
     * @param clearingLookupMap A map that maps the name of a clearing to the corresponding Clearing
     * object.
     * @param neighbourMap A map that contains the names of the clearings and paths as keys, and a list
     * of their neighbouring clearing names as values.
     */
    private static void addNeighbours(List<WorldObject> settlements, List<WorldObject> paths, Map<String, WorldObject> settlementLookupMap, Map<String, List<String>> neighbourMap) {
        for (WorldObject path : paths) {
            List<String> neighbourNames = neighbourMap.get(path.getName());

            for (String neighbourName : neighbourNames) {
                WorldObject neighbour = settlementLookupMap.get(neighbourName);
                if (neighbour != null) {
                    path.addNeighbour(neighbour);
                }
            }
            WorldObject settlement = path.getNeighbours().get(0);
            WorldObject neighbour = path.getNeighbours().get(1);
            settlement.addNeighbour(neighbour);
            //settlement.setPath(neighbour, path);
            //neighbour.setPath(settlement, path);
        }
    }

    private static void setNPCPaths(List<WorldObject> settlements,List<WorldObject> paths, Map<String, List<String>> destinationPathMap, Map<String, WorldObject> settlementLookupMap) {
        List<WorldObject> allObjects = new ArrayList<>();
        allObjects.addAll(settlements);
        allObjects.addAll(paths);
        for (WorldObject worldObject : allObjects) {
            List<NPC> npcs = worldObject.getNPCs();
            for (NPC npc : npcs) {
                List<String> destinationPath = destinationPathMap.get(npc.getName());
                if (destinationPath != null) {
                    for (String destinationName : destinationPath) {
                        WorldObject destination = settlementLookupMap.get(destinationName);
                        npc.addDestination(destination);
                    }
                }
            }
        }
    }
}