package com.rootgame.model.World.WorldObjects;

public class Settlement extends WorldObject {

    private final int MAX_POPULATION = (int) (Math.random() * 10 + 20);
    private final int MAX_MARKEVALUE = MAX_POPULATION - 10;
    private final int MAX_COMBATSTRENGTH = MAX_POPULATION - 5;
    private final int MAX_LOYALTY = 100;
    private boolean isCapital;


    public Settlement(WorldObjectType type, String name, int x, int y) {
        super(type, name, x, y);
        combatStrength = MAX_COMBATSTRENGTH;
        marketValue = MAX_MARKEVALUE;
        loyalty = (int) (Math.random() * MAX_LOYALTY);
        currentPopulation = MAX_POPULATION;
        siegeTimer = (int)combatStrength/5;
        isCapital = false;
    }

    public boolean isCapital() {
        return isCapital;
    }

    public void setCapital(boolean isCapital) {
        this.isCapital = isCapital;
    }

    @Override
    public void setLoyalty(int loyalty) {
        if (loyalty > MAX_LOYALTY) {
            throw new IllegalArgumentException("Loyalty is too high");
        }
        this.loyalty = loyalty;
    }

    /**
     * This function updates the state of a besieged object by decreasing its population, combat
     * strength, and market value if it is currently under siege.
     */
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

    /**
     * This function sets a boolean value for a property, reduces the current population by half, and
     * sets a timer to 3.
     * 
     * @param ruined a boolean value indicating whether the object is ruined or not.
     */
    @Override
    public void setRuined(boolean ruined) {
        this.ruined = ruined;
        currentPopulation -= currentPopulation / 2;
        ruinTimer = 3;
    }    

    /**
     * This function updates the state of an object that has been ruined by decrementing a timer until
     * it reaches zero and then setting the ruined state to false.
     */
    @Override
    public void updateRuined() {
        if (isRuined() && ruinTimer > 0) {
            ruinTimer--;
            if (ruinTimer == 0) {
                setRuined(false);
            }
        }
    }

    /**
     * This function increases the population of a city if it is not besieged or ruined, and updates
     * the market value and combat strength accordingly.
     */
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
