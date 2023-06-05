package com.rootgame.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.paint.Color;

public class LoadedModule {

    private static Map<String, List<String>> factionRaceMap = new HashMap<>();
    private static Map<String, Color> factionColorMap = new HashMap<>();
    private static Map<String, List<List<String>>> raceNameMap = new HashMap<>();
    private static List<String> cityNames = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(LoadedModule.class);

    private LoadedModule (){} // Private constructor to prevent instantiation
    
    public static void loadModule(String module) {
        try (Stream<String> lines = Files.lines(Paths.get("FactionRaces.json"))) {
            String jsonData = lines.collect(Collectors.joining());
            JSONArray objectArray = new JSONArray(jsonData);
    
            JSONArray moduleArray = findModuleArray(module, objectArray);
    
            for (int i = 0; i < moduleArray.length(); i++) {
                JSONObject worldJson = moduleArray.getJSONObject(i);
                if (worldJson.has("worldobjects")) {
                    parseWorldObjects(worldJson.getJSONArray("worldobjects"));
                }
                if (worldJson.has("factions")) {
                    parseFactions(worldJson.getJSONArray("factions"));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading the JSON file: {}", e.getMessage());
        }
    }
    
    private static JSONArray findModuleArray(String module, JSONArray objectArray) {
        for (int i = 0; i < objectArray.length(); i++) {
            JSONObject moduleJson = objectArray.getJSONObject(i);
            if (moduleJson.has(module)) {
                return moduleJson.getJSONArray(module);
            }
        }
        return new JSONArray(); // Return empty array if module not found
    }


    private static void parseWorldObjects(JSONArray worldObjectsArray){
        for (int i = 0; i < worldObjectsArray.length(); i++){
            JSONObject worldObjectJson = worldObjectsArray.getJSONObject(i);
            cityNames = extractStringList(worldObjectJson, "settlementnames");
            System.out.println(cityNames);
        }
    }

    private static void parseFactions(JSONArray factionsArray){
        for (int i = 0; i < factionsArray.length(); i++){
            JSONObject factionsJson = factionsArray.getJSONObject(i);
            String factionName = factionsJson.getString("faction");
            addFactionColor(factionsJson, factionName);
            addFactionRaces(factionsJson, factionName);
        }  
    }

    private static void addFactionColor(JSONObject factionsJson, String factionName){
        Color factionColor = Color.web(factionsJson.getString("color"));
        factionColorMap.put(factionName, factionColor);
    }

    private static void addFactionRaces(JSONObject factionsJson,String factionName){
        if (factionsJson.has("races")){
            List <String> races = new ArrayList<>();
            JSONArray racesJsonArray = factionsJson.getJSONArray("races");
            races = extractRaces(racesJsonArray, races);
            factionRaceMap.put(factionName, races);
        }
    }

    private static List<String> extractRaces(JSONArray racesJsonArray, List<String> races) {
        for (int j = 0; j < racesJsonArray.length(); j++) {
            List<List<String>> forenamesSurenames = new ArrayList<>();

            JSONObject racesJsonObject = racesJsonArray.getJSONObject(j);
            String race = "";

            if (racesJsonObject.has("name")){
                race = racesJsonObject.getString("name");
                races.add(race);
            }
            forenamesSurenames.add(extractStringList(racesJsonObject, "forenames"));
            forenamesSurenames.add(extractStringList(racesJsonObject, "surnames"));

            raceNameMap.putIfAbsent(race, forenamesSurenames);
        }  
        return races;
    }

    private static List<String> extractStringList(JSONObject racesJsonObject, String jsonString) {
        List<String> list = new ArrayList<>();
        if (!racesJsonObject.isNull(jsonString)){
            JSONArray jsonArray = racesJsonObject.getJSONArray(jsonString);
            for (Object item : jsonArray) {
                list.add(item.toString());
            }
        }
        return list;
    }

    public static Color getFactionColor(String faction) {
        return factionColorMap.get(faction);
    }

    public static Map<String, List<String>> getFactionRaceMap() {
        return factionRaceMap;
    }

    public static List<String> getFactionList() {
        Set<String> factionSet = factionRaceMap.keySet();
        return new ArrayList<>(factionSet);
    }

    public static Map<String, List<List<String>>> getRaceNameMap() {
        return raceNameMap;
    }

    public static List<String> getCityNames() {
        return cityNames;
    }

}
