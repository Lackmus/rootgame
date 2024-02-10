package com.rootgame.model.World.EvolveWorld.NPCEvolver;

import java.util.List;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;

public interface NPCEvolver {
    void applyEvolution(NPC npc, List<WorldObject> worldObjectList);
}
