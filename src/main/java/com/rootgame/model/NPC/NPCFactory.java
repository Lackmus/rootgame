package com.rootgame.model.NPC;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rootgame.model.World.WorldObjects.WorldObject;

public class NPCFactory {
    private static final Logger logger = LoggerFactory.getLogger(NPCFactory.class);
    
    private static final String NEUTRAL = "Neutral";
    private static final int MAX_LOYALTY = 100;
    private static final double FACTION_CHANGE_PROBABILITY = 0.5;
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * The function creates an NPC object with a random name, animal, loyalty, and faction based on the
     * NPCType and WorldObject parameters.
     * 
     * @param npcType The type of NPC being created (e.g. civilian, trader, bandit, etc.).
     * @param faction The faction that the NPC belongs to.
     * @param worldObject The world object that the NPC will be associated with or located in.
     * @return The method is returning an instance of the NPC class.
     */
    public static NPC createNPC(NPCType npcType, String faction, WorldObject worldObject, Map<String, List<List<String>>> raceNameMap,Map<String, List<String>> factionRaceMap) {
        
        String name;
        String species;

        int loyalty = random.nextInt(MAX_LOYALTY);
        switch (npcType) {
            case CIVILIAN: case TRADER: case CARAVAN:
                if (!NEUTRAL.equals(faction) && random.nextDouble() > FACTION_CHANGE_PROBABILITY)
                    faction = NEUTRAL;
                break;
            case BANDIT: case MERCENARY:
                faction = NEUTRAL;
                break;
            default:  
                break;
        }
        
        species = getRandomSpecies(factionRaceMap,faction);
        name = getRandomName(raceNameMap, species);
        NPC npc = new NPC(name, species, npcType, faction, worldObject);
        npc.setLoyalty(loyalty);
        
        return npc;
    }

    public static String getRandomName(Map<String, List<List<String>>> raceNameMap, String species) {
        if (raceNameMap == null || species == null) {
            throw new IllegalArgumentException("raceNameMap and species must not be null");
        }

        List<List<String>> raceNames = raceNameMap.get(species);

        if (raceNames == null || raceNames.size() < 2) {
            logError("Invalid race name data for species: " + species);
            throw new IllegalStateException("Invalid race name data for species");
        }

        List<String> forenames = raceNames.get(0);
        List<String> surnames = raceNames.get(1);

        String forename = getRandomElement(forenames);
        String surname = getRandomElement(surnames);

        return forename + " " + surname;
    }

    private static String getRandomElement(List<String> list) {
        if (list == null || list.isEmpty()) {
            logError("Invalid name list data");
            throw new IllegalStateException("Invalid name list data");
        }
        return list.size() == 1 ? list.get(0) : list.get(random.nextInt(list.size()));
    }

    private static void logError(String errorMessage) {
        logger.error("NameGenerator Error: {}", errorMessage);
    }


    private static String getRandomSpecies(Map<String, List<String>> factionRaceMap,String faction){
        int factionCount = factionRaceMap.get(faction).size();
        return factionRaceMap.get(faction).get(random.nextInt(factionCount));
    }
}