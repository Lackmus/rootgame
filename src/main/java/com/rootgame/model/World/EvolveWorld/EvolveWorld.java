package com.rootgame.model.World.EvolveWorld;

import java.util.List;

import com.rootgame.model.Faction.Faction;
import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.EvolveWorld.NPCEvolver.NPCEvolver;
import com.rootgame.model.World.EvolveWorld.NPCEvolver.NPCEvolverFactory;
import com.rootgame.model.World.WorldObjects.WorldObject;

public class EvolveWorld {
    private EvolveWorld() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void evolveWorld(List<WorldObject> worldObjectList, List<Faction> factionList, List<NPC> npcList) {
        evolveNPCs(npcList,worldObjectList);
        evolveWorldObjects(worldObjectList);
        evolveFactions(factionList);
    }

/***************
 * NPC-Evolver *
 *************************************************************************************************************************************************************/

    /**
     * The function evolves the NPCs in a list of world objects and sets their "moved" status to false.
     * 
     * @param worldObjectList A list of WorldObject objects.
     */
    private static void evolveNPCs(List<NPC> npcList,List<WorldObject> worldObjectList) {
        for (int i = 0; i < npcList.size(); i++){
            evolveNPC(npcList.get(i), worldObjectList);
        }
    }

    /**
     * The function evolves an NPC based on its type and sets its moved status to true.
     * 
     * @param npc The "npc" parameter is an object of type NPC, which represents a non-player character
     * in the game. It contains information about the type of NPC and its current state.
     * @param worldObjectList A list of WorldObject objects.
     */
    private static void evolveNPC(NPC npc, List<WorldObject> worldObjectList) {
        NPCEvolver evolver = NPCEvolverFactory.createEvolver(npc.getType());
        evolver.applyEvolution(npc, worldObjectList);
    }

    private static void evolveWorldObjects(List<WorldObject> worldObjectList) {
        //TODO 
        for (WorldObject worldObject : worldObjectList) {
            evolveWorldObject(worldObject);
        }
    }

    private static void evolveWorldObject(WorldObject worldObject) {
        //TODO
    }

    private static void evolveFactions(List<Faction> factions) {
        for (Faction faction : factions){
            evolveFaction(faction);
        }
    }
   
    private static void evolveFaction(Faction faction) {
        //TODO
    }
    
}
