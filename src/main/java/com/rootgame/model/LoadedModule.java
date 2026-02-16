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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javafx.scene.paint.Color;

public class LoadedModule {

    private static final String FACTION_KEY = "faction";
    private static final String FACTIONS_KEY = "factions";
    private static final String COLOR_KEY = "color";
    private static final String RACES_KEY = "races";
    private static final String NAME_KEY = "name";
    private static final String FORENAMES_KEY = "forenames";
    private static final String SURNAMES_KEY = "surnames";
    private static final String SETTLEMENT_NAMES_KEY = "settlementnames";
    private static final String WORLDOBJECTS_KEY = "worldobjects";

    private static Map<String, List<String>> factionRaceMap = new HashMap<>();
    private static Map<String, Color> factionColorMap = new HashMap<>();
    private static Map<String, List<List<String>>> raceNameMap = new HashMap<>();
    private static List<String> cityNames = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(LoadedModule.class); // Logger object to log messages to the console and a file called "rootgame.log"

    private LoadedModule (){} // Private constructor to prevent instantiation
    
    /**
     * The function `loadModule` reads a JSON file, extracts specific data based on a given module, and
     * parses the extracted data.
     * 
     * @param module The "module" parameter is a string that represents the name or identifier of a
     * module.
     */
    public static void loadModule(String module) {
        ObjectMapper mapper = new ObjectMapper();
        try (Stream<String> lines = Files.lines(Paths.get("FactionRaces.json"))) {
            String jsonData = lines.collect(Collectors.joining());
            ArrayNode objectArray = (ArrayNode) mapper.readTree(jsonData);

            ArrayNode moduleArray = findModuleArray(module, objectArray);

            for (JsonNode worldJson : moduleArray) {
                if (worldJson.has(WORLDOBJECTS_KEY)) {
                    parseWorldObjects((ArrayNode) worldJson.get(WORLDOBJECTS_KEY));
                }
                if (worldJson.has(FACTIONS_KEY)) {
                    parseFactions((ArrayNode) worldJson.get(FACTIONS_KEY));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading the JSON file: {}", e.getMessage());
        }
    }
    
    /**
     * The function finds and returns a JSONArray within a JSONArray based on a specified module name.
     * 
     * @param module A string representing the name of the module to search for in the JSON array.
     * @param objectArray A JSONArray containing a list of JSONObjects. Each JSONObject represents a
     * module and contains an array with the key "module".
     * @return The method `findModuleArray` returns a `JSONArray` object.
     */
    private static ArrayNode findModuleArray(String module, ArrayNode objectArray) {
        for (JsonNode moduleJson : objectArray) {
            if (moduleJson.has(module) && moduleJson.get(module).isArray()) {
                return (ArrayNode) moduleJson.get(module);
            }
        }
        return new ObjectMapper().createArrayNode(); // Return empty array if module not found
    }

    /**
     * The function parses a JSONArray of world objects, extracts the "settlementnames" field from each
     * object, and prints the resulting list of city names.
     * 
     * @param worldObjectsArray A JSONArray containing a list of world objects in JSON format. Each
     * world object is represented by a JSONObject.
     */
    private static void parseWorldObjects(ArrayNode worldObjectsArray){
        for (JsonNode worldObjectJson : worldObjectsArray){
            cityNames = extractStringList(worldObjectJson, SETTLEMENT_NAMES_KEY);
            System.out.println(cityNames);
        }
    }

    /**
     * The function parses a JSON array of factions, extracts the faction name, and calls two helper
     * functions to add the faction color and races.
     * 
     * @param factionsArray The factionsArray parameter is a JSONArray object that contains a list of
     * factions in JSON format.
     */
    private static void parseFactions(ArrayNode factionsArray){
        for (JsonNode factionsJson : factionsArray){
            String factionName = factionsJson.get(FACTION_KEY).asText();
            addFactionColor(factionsJson, factionName);
            addFactionRaces(factionsJson, factionName);
        }
    }

    /**
     * The function adds a faction color to a map using the faction name as the key and the color as
     * the value.
     * 
     * @param factionsJson A JSONObject containing information about factions, including their names
     * and colors.
     * @param factionName The factionName parameter is a String that represents the name of a faction.
     */
    private static void addFactionColor(JsonNode factionsJson, String factionName){
        try {
            Color factionColor = Color.web(factionsJson.get(COLOR_KEY).asText());
            factionColorMap.put(factionName, factionColor);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding faction color for {}: {}", factionName, e.getMessage());
        }
    }

    /**
     * The function adds the races associated with a faction to a map.
     * 
     * @param factionsJson A JSONObject containing information about factions and their races.
     * @param factionName The name of the faction for which we want to add races.
     */
    private static void addFactionRaces(JsonNode factionsJson,String factionName){
        if (factionsJson.has(RACES_KEY) && factionsJson.get(RACES_KEY).isArray()){
            List <String> races = new ArrayList<>();
            ArrayNode racesJsonArray = (ArrayNode) factionsJson.get(RACES_KEY);
            races = extractRaces(racesJsonArray, races);
            factionRaceMap.put(factionName, races);
        }
    }

    /**
     * The function extracts race names, forenames, and surnames from a JSON array and stores them in a
     * map.
     * 
     * @param racesJsonArray A JSONArray containing race information in JSON format.
     * @param races The `races` parameter is a `List<String>` that represents a collection of race
     * names.
     * @return The method is returning a List<String> containing the races extracted from the
     * racesJsonArray.
     */
    private static List<String> extractRaces(ArrayNode racesJsonArray, List<String> races) {
        for (JsonNode racesJsonObject : racesJsonArray) {
            List<List<String>> forenamesSurenames = new ArrayList<>();

            String race = "";

            if (racesJsonObject.has(NAME_KEY)){
                race = racesJsonObject.get(NAME_KEY).asText();
                races.add(race);
            }
            forenamesSurenames.add(extractStringList(racesJsonObject, FORENAMES_KEY));
            forenamesSurenames.add(extractStringList(racesJsonObject, SURNAMES_KEY));

            raceNameMap.putIfAbsent(race, forenamesSurenames);
        }
        return races;
    }

    /**
     * The function extracts a list of strings from a JSON object based on a specified key.
     * 
     * @param racesJsonObject A JSONObject that contains the races data.
     * @param jsonString The `jsonString` parameter is a string that represents the key of a JSON array
     * in the `racesJsonObject`.
     * @return The method is returning a List of Strings.
     */
    private static List<String> extractStringList(JsonNode racesJsonObject, String jsonString) {
        List<String> list = new ArrayList<>();
        if (racesJsonObject.has(jsonString) && racesJsonObject.get(jsonString).isArray()){
            for (JsonNode item : racesJsonObject.withArray(jsonString)) {
                list.add(item.asText());
            }
        }
        return list;
    }

    /**
     * The function returns the color associated with a given faction.
     * 
     * @param faction The faction parameter is a String that represents the name of a faction.
     * @return The method is returning a Color object.
     */
    public static Color getFactionColor(String faction) {
        return factionColorMap.get(faction);
    }

    /**
     * The function returns a map that maps factions to a list of races.
     * 
     * @return The method is returning a Map object with keys of type String and values of type
     * List<String>.
     */
    public static Map<String, List<String>> getFactionRaceMap() {
        return factionRaceMap;
    }

    /**
     * The function returns a list of factions from a map.
     * 
     * @return The method is returning a List of Strings.
     */
    public static List<String> getFactionNames() {
        Set<String> factionSet = factionRaceMap.keySet();
        return new ArrayList<>(factionSet);
    }

    /**
     * The function returns a map that maps race names to a list of lists of strings.
     * 
     * @return The method is returning a map with keys of type String and values of type
     * List<List<String>>.
     */
    public static Map<String, List<List<String>>> getRaceNameMap() {
        return raceNameMap;
    }

    /**
     * The function returns a list of settlement names.
     * 
     * @return A List of Strings containing city names.
     */
    public static List<String> getSettlementNames() {
        return cityNames;
    }

}
