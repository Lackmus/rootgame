package com.rootgame.model.World.EvolveWorld;

import java.util.List;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.EvolveWorld.NPCEvolver.NPCEvolver;
import com.rootgame.model.World.EvolveWorld.NPCEvolver.NPCEvolverFactory;
import com.rootgame.model.World.WorldObjects.WorldObject;

public class EvolveWorld {
    private EvolveWorld() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void evolveWorld(List<WorldObject> worldObjectList) {
        evolveNPCs(worldObjectList);
    }

/***************
 * NPC-Evolver *
 *************************************************************************************************************************************************************/

    /**
     * The function evolves the NPCs in a list of world objects and sets their "moved" status to false.
     * 
     * @param worldObjectList A list of WorldObject objects.
     */
    private static void evolveNPCs(List<WorldObject> worldObjectList) {

        for (WorldObject worldObject : worldObjectList) {
            List<NPC> npcList = worldObject.getNPCs();
            for (int i = 0; i < npcList.size(); i++) {
                evolveNPC(npcList.get(i), worldObjectList);
            }
        }   

        worldObjectList.stream()
            .flatMap(worldObject -> worldObject.getNPCs().stream())
            .forEach(npc -> npc.setMoved(false));
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
        npc.setMoved(true);
    }

          

//    private static void evolveWorldObjects() {}

//    

//    private static void evolveFactions() {}

//    private static void evolveClearings() {}

//    private static void evolvePaths() {}

//    private static void evolveWorldObject(WorldObject worldObject) {}

//    

//    private static void makeDestinationPath(WorldObject worldObject) {}

//    private static void evolveFaction(String faction) {}

//    private static void evolveClearing(Settlement clearing) {}

//    private static void evolvePath(Path path) {}

    
}
