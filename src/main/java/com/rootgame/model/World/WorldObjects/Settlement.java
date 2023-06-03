package com.rootgame.model.World.WorldObjects;

public class Settlement extends WorldObject {

    private final int MAX_POPULATION = (int) (Math.random() * 10 + 20);
    private final int MAX_MARKEVALUE = MAX_POPULATION - 10;
    private final int MAX_COMBATSTRENGTH = MAX_POPULATION - 5;
    private final int MAX_LOYALTY = 100;

    public Settlement(WorldObjectType type, String name, int x, int y) {
        super(type, name, x, y);
        combatStrength = MAX_COMBATSTRENGTH;
        marketValue = MAX_MARKEVALUE;
        loyalty = (int) (Math.random() * MAX_LOYALTY);
        currentPopulation = MAX_POPULATION;
        siegeTimer = (int)combatStrength/5;
    }

    @Override
    public void setLoyalty(int loyalty) {
        if (loyalty > MAX_LOYALTY) {
            throw new IllegalArgumentException("Loyalty is too high");
        }
        this.loyalty = loyalty;
    }

    @Override
    public void updateBesieged() {
        if (isBesieged() && siegeTimer < 3) {
            siegeTimer++;
            currentPopulation--;
            updateCombatStrength();
            updateMarketValue();
        } else {
            siegeTimer = 0;
            this.besieged = false;
        }
    }

    public void updateSiegeTimer() {
        this.siegeTimer = (int)getCombatStrength()/5;
    }

    public void setSiegeTimer(int siegeTimer) {
        this.siegeTimer = siegeTimer;
    }

    @Override
    public void setRuined(boolean ruined) {
        this.ruined = ruined;
        currentPopulation -= currentPopulation / 2;
        ruinTimer = 3;
    }

    // set ruintimer
    

    @Override
    public void updateRuined() {
        if (isRuined() && ruinTimer > 0) {
            ruinTimer--;
            if (ruinTimer == 0) {
                setRuined(false);
            }
        }
    }

    @Override
    public void growPopulation() {
        if (isBesieged() || isRuined()) {
            return;
        }
        if (currentPopulation < MAX_POPULATION) {
            currentPopulation++;
            updateMarketValue();
            updateCombatStrength();
        }
    }
    
}
