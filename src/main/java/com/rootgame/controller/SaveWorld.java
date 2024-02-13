package com.rootgame.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;

public class SaveWorld {

    /**
     * This function saves a list of WorldObjects to a JSON file.
     *
     * @param nameOfJSON       A String representing the name of the JSON file to be saved.
     * @param worldObjectList  A List of WorldObject objects that will be saved to the JSON file.
     */
    public static void saveToJSON(String nameOfJSON, List<WorldObject> worldObjectList) {
        
        JSONArray worldArray = new JSONArray();

        // Iterate over the worldObjectList and create JSON objects for each WorldObject
        for (WorldObject worldObject : worldObjectList) {
            JSONObject worldObjectObj = new JSONObject();
            worldObjectObj.put("type", worldObject.getType().toString());
            worldObjectObj.put("name", worldObject.getName());
            worldObjectObj.put("x", worldObject.getX());
            worldObjectObj.put("y", worldObject.getY());
            worldObjectObj.put("faction", worldObject.getFaction().toString());
            worldObjectObj.put("distance", worldObject.getDistance());
            worldObjectObj.put("combatStrength", worldObject.getCombatStrength());
            worldObjectObj.put("marketValue", worldObject.getMarketValue());
            worldObjectObj.put("loyalty", worldObject.getLoyalty());
            worldObjectObj.put("besieged", worldObject.isBesieged());
            worldObjectObj.put("siegeTimer", worldObject.getSiegeTimer());
            worldObjectObj.put("ruined", worldObject.isRuined());
            worldObjectObj.put("ruinTimer", worldObject.getRuinTimer());
            worldObjectObj.put("isCapital", worldObject.isCapital());
            worldObjectObj.put("currentPopulation", worldObject.getCurrentPopulation());
            worldObjectObj.put("description", worldObject.getDescription());
            

            // Create and append neighbours array
            JSONArray neighboursArray = new JSONArray();
            for (WorldObject neighbour : worldObject.getNeighbours()) {
                neighboursArray.put(neighbour.getName());
            }
            worldObjectObj.put("neighbours", neighboursArray);

            // Create and append npcs array
            JSONArray npcsArray = new JSONArray();
            for (NPC npc : worldObject.getNPCs()) {
                JSONObject npcObj = new JSONObject();
                
                npcObj.put("name", npc.getName());
                npcObj.put("faction", npc.getFaction().toString());
                npcObj.put("type", npc.getType().toString());
                npcObj.put("race", npc.getSpecies().toString());
                npcObj.put("loyalty", npc.getLoyalty());
                npcObj.put("description", npc.getDescription());
                npcObj.put("combatStrength", npc.getCombatStrength());
                npcObj.put("marketValue", npc.getMarketValue());
                npcObj.put("currentLocation", npc.getCurrentLocation().getName());
                npcObj.put("origin", npc.getOrigin().getName());

                JSONArray destinationPathArray = new JSONArray();
                for (WorldObject destination : npc.getDestinationPath()) {
                    destinationPathArray.put(destination.getName());
                }
                npcObj.put("destinationPath", destinationPathArray);

                npcsArray.put(npcObj);
            }
            worldObjectObj.put("npcs", npcsArray);

            worldArray.put(worldObjectObj);
        }

        // Write the JSON data to a file
        try (FileWriter fileWriter = new FileWriter(nameOfJSON)) {
            fileWriter.write(worldArray.toString(4)); // Use 4 for indentation level
            fileWriter.flush();
            System.out.println("JSON file saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}