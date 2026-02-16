package com.rootgame.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(nameOfJson)));
            ArrayNode objectArray = (ArrayNode) mapper.readTree(jsonData);

            Map<String, List<String>> neighbourMap = new HashMap<>();
            Map<String, WorldObject> settlementLookupMap = new HashMap<>();
            Map<String, List<String>> destinationPathMap = new HashMap<>();

            for (JsonNode objectJson : objectArray) {

                String name = objectJson.get("name").asText();
                int x = objectJson.get("x").asInt();
                int y = objectJson.get("y").asInt();
                WorldObject.Type type = WorldObject.Type.valueOf(objectJson.get("type").asText());

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

                worldObject.setFaction(objectJson.get("faction").asText());
                worldObject.setCombatStrength(objectJson.get("combatStrength").asInt());
                worldObject.setMarketValue(objectJson.get("marketValue").asInt());
                worldObject.setLoyalty(objectJson.get("loyalty").asInt());
                worldObject.setBesieged(objectJson.get("besieged").asBoolean());
                worldObject.setSiegeTimer(objectJson.get("siegeTimer").asInt());
                worldObject.setRuined(objectJson.get("ruined").asBoolean());
                worldObject.setRuinTimer(objectJson.get("ruinTimer").asInt());
                worldObject.setCapital(objectJson.get("isCapital").asBoolean());
                worldObject.setCurrentPopulation(objectJson.get("currentPopulation").asInt());
                worldObject.setDescription(objectJson.get("description").asText());

                List<NPC> npcs = parseNPCs(objectJson, worldObject, destinationPathMap);
                worldObject.setNPCs(npcs);
      
                List<String> neighbours = parseNeighbours(objectJson);
                neighbourMap.put(name, neighbours);
            }

            addNeighbours(settlements, paths, settlementLookupMap, neighbourMap);
            setNPCPaths(settlements, paths, destinationPathMap, settlementLookupMap);
        } catch (IOException e) {
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
    private static List<String> parseNeighbours (JsonNode objectJson) {
        List<String> neighbours = new ArrayList<>();
        if (objectJson.has("neighbours") && objectJson.get("neighbours").isArray()) {
            for (JsonNode neighboursJson : objectJson.withArray("neighbours")) {
                neighbours.add(neighboursJson.asText());
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
    private static List<NPC> parseNPCs(JsonNode objectJson, WorldObject worldObject, Map<String, List<String>> destinationPathMap) {

        List<NPC> npcs = new ArrayList<>();
        if (objectJson.has("npcs") && objectJson.get("npcs").isArray()) {
            for (JsonNode npcJson : objectJson.withArray("npcs")) {
                int npcCombatStrength = npcJson.get("combatStrength").asInt();
                int npcMarketValue = npcJson.get("marketValue").asInt();


                NPC npc = new NPC(npcJson.get("name").asText(), npcJson.get("race").asText(), NPCType.valueOf(npcJson.get("type").asText()),
                                  npcJson.get("faction").asText(), worldObject);

                npc.setLoyalty(npcJson.get("loyalty").asInt());
                npc.setDescription(npcJson.get("description").asText());
                npc.setCombatStrength(npcCombatStrength);
                npc.setMarketValue(npcMarketValue);
                if (npcJson.has("destinationPath") && npcJson.get("destinationPath").isArray()) {
                    List<String> destinationPath = new ArrayList<>();
                    for (JsonNode destinationNameNode : npcJson.withArray("destinationPath")) {
                        destinationPath.add(destinationNameNode.asText());
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
            for (NPC npc : worldObject.getNPCs()) {
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