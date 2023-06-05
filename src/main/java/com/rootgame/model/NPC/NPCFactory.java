package com.rootgame.model.NPC;

import java.util.List;
import java.util.Map;

import com.rootgame.model.World.WorldObjects.WorldObject;

public class NPCFactory {

    private static Map<String, List<List<String>>> raceNameMap = FactionRaces.getRaceNameMap();
    private static Map<String, List<String>> factionRaceMap = FactionRaces.getFactionRaceMap();

    private NPCFactory() {
    }
    
    /**
     * This Java function returns a random animal object from a list of animals associated with a given
     * faction.
     * 
     * @param faction The "faction" parameter is an input of type "FactionEnum", which is an
     * enumeration that represents a group or faction of animals. It is used to select a random animal
     * from a list of animals that belong to the specified faction.
     * @return The method `makeAnimal` is returning an object of type `Animal`. The specific `Animal`
     * object being returned is randomly selected from an array of `Animal` objects that corresponds to
     * the given `FactionEnum` faction.
     */

    /**
     * The function creates an NPC object with a randomly generated name, animal, and attributes based
     * on the given NPC type and faction.
     * 
     * @param npcType An enum representing the type of NPC being created (SOLDIER, CIVILIAN, TRADER, or
     * LEADER).
     * @param faction The faction that the NPC belongs to. It is of type FactionEnum.
     * @return The method is returning an NPC object.
     */
    public static NPC createNPC(NPCType npcType, String faction, WorldObject worldObject) {
        String name;
        String animal;
        int loyalty = (int) (Math.random() * 100);
        switch (npcType) {
            case CIVILIAN: case TRADER:
                if (faction != "Neutral" && Math.random() > 0.5)
                    faction = "Neutral";
                break;
            case BANDIT: case CARAVAN: case MERCENARY:
                faction = "Neutral";
                break;
            default:  
                break;
        }
        animal = factionRaceMap.get(faction).get((int) (Math.random() * FactionRaces.getFactionRaceMap().get(faction).size()));
        name = getRandomName(animal);
        NPC npc = new NPC(name, animal, npcType, faction, worldObject);
        npc.setLoyalty(loyalty);
        
        return npc;
    }

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