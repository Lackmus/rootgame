package com.rootgame.model.NPC;

import java.util.List;
import java.util.Map;

import com.rootgame.model.LoadedModule;
import com.rootgame.model.World.WorldObjects.WorldObject;

public class NPCFactory {

    private static Map<String, List<List<String>>> raceNameMap = LoadedModule.getRaceNameMap();
    private static Map<String, List<String>> factionRaceMap = LoadedModule.getFactionRaceMap();

    private NPCFactory() {
    }
    
    /**
     * The function creates an NPC object with a random name, animal, loyalty, and faction based on the
     * NPCType and WorldObject parameters.
     * 
     * @param npcType The type of NPC being created (e.g. civilian, trader, bandit, etc.).
     * @param faction The faction that the NPC belongs to.
     * @param worldObject The world object that the NPC will be associated with or located in.
     * @return The method is returning an instance of the NPC class.
     */
    public static NPC createNPC(NPCType npcType, String faction, WorldObject worldObject) {
        String name;
        String animal;
        int loyalty = (int) (Math.random() * 100);
        switch (npcType) {
            case CIVILIAN: case TRADER: case CARAVAN:
                if (faction != "Neutral" && Math.random() > 0.5)
                    faction = "Neutral";
                break;
            case BANDIT: case MERCENARY:
                faction = "Neutral";
                break;
            default:  
                break;
        }
        animal = factionRaceMap.get(faction).get((int) (Math.random() * LoadedModule.getFactionRaceMap().get(faction).size()));
        name = getRandomName(animal);
        NPC npc = new NPC(name, animal, npcType, faction, worldObject);
        npc.setLoyalty(loyalty);
        
        return npc;
    }

    /**
     * This Java function generates a random name for an animal based on its race and returns it as a
     * string.
     * 
     * @param animal The parameter "animal" is a String representing the type of animal for which a
     * random name is being generated.
     * @return The method is returning a randomly generated name for a given animal, using lists of
     * forenames and surnames specific to that animal's race. If there is an error (i.e. if there are
     * not enough race names), the method returns the string "ERROR".
     */
    private static String getRandomName(String animal) {
        List<List<String>> raceNames = raceNameMap.get(animal);
        if (raceNames.size() < 2) {
            return "ERROR";
        }
        List<String> forenames = raceNames.get(0);
        List<String> surenames = raceNames.get(1);
        String forename = forenames.size() == 1? forenames.get(0) : forenames.get((int) (Math.random() * forenames.size()));
        String surename = surenames.size() == 1? surenames.get(0) : surenames.get((int) (Math.random() * surenames.size()));
        return forename + " " + surename;     
    }
}