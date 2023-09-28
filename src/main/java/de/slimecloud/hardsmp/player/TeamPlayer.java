package de.slimecloud.hardsmp.player;

public interface TeamPlayer extends EventPlayer {

    /**
     * equals to
     * <p>
     * addPoints(getTeam().getMultiplier()*points);
     */
    void addMultipliedPoints(double points);

    EventTeam getTeam();

    void leaveTeam();

}
