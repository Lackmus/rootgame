package com.rootgame.model.World.EvolveWorld.NPCEvolver;

import com.rootgame.model.NPC.NPCType;

public class NPCEvolverFactory {
    public static NPCEvolver createEvolver(NPCType npcType) {
        switch (npcType) {
            case CARAVAN:
                return new CaravanEvolver();
            case LEADER:
                return new LeaderEvolver();
            case CIVILIAN:
                return new CivilianEvolver();
            case TRADER:
                return new TraderEvolver();
            case BANDIT:
                return new BanditEvolver();
            case MERCENARY:
                return new MercenaryEvolver();
            case SOLDIER:
                return new SoldierEvolver();
            default:
                throw new IllegalArgumentException("Invalid NPC type: " + npcType);
        }
    }
}
