package de.slimecloud.hardsmp.player;

public interface TeamPlayer extends EventPlayer {

    /**
     * The points are added equally distributed to all team members
     * @param points points to add
     */
    void addMultipliedPoints(double points);

    /**
     * Returns the team of the player
     * @return the team
     * @see EventTeam
     */
    EventTeam getTeam();

    /**
     * Herewith the player leaves the team and the EventPlayer is returned
     * @return the EventPlayer implementation of the player
     * @see EventPlayer
     */
    EventPlayer leaveTeam();

}
