package com.rootgame.model.World.EvolveWorld.NPCEvolver;

import java.util.List;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;
import com.rootgame.model.World.WorldObjects.WorldObjectType;

public class CaravanEvolver implements NPCEvolver {
    
    @Override
    public void applyEvolution(NPC npc, List<WorldObject> worldObjectList) {
        if (npc.getDestinationPath().isEmpty()) {

            npc.setDestinationPath(NPCPathManager.makeDestinationPath(npc,NPCPathManager.getRandomDestination(worldObjectList,WorldObjectType.SETTLEMENT)));
            System.out.println(npc.getName() + " Current: " + npc.getCurrentLocation() + " Destination Path: " + npc.getDestinationPath());
        } else {
            npc.moveNPC();
        }
    }
}
