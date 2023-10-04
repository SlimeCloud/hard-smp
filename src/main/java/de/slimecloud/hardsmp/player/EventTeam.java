package de.slimecloud.hardsmp.player;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface EventTeam extends EventEntity {

    /**
     * returns a collection from TeamPlayer containing all team members
     * @return the players collection
     * @see Collection
     * @see TeamPlayer
     */
    Collection<TeamPlayer> getPlayers();

    /**
     * Returns a TeamPlayer object through an EventPlayer object
     * @param player the EventPlayer object of the player
     * @return the TeamPlayer object or null if the EventPlayer is not in this team
     * @see TeamPlayer
     */
    @Nullable TeamPlayer getPlayer(EventPlayer player);

    /**
     * Returns a TeamPlayer object through an OfflinePlayer object
     * @param player the OfflinePlayer object of the player
     * @return the TeamPlayer object or null if the OfflinePlayer is not in this team
     * @see TeamPlayer
     */
    @Nullable TeamPlayer getPlayer(OfflinePlayer player);

    /**
     * Returns the Team Leader as TeamPlayer
     * @return the Team Leader
     * @see TeamPlayer
     */
    TeamPlayer getTeamLeader();

    /**
     * adds a player to the team. If the player is already in a team, he will automatically leave it.
     * @param player the player to join the team
     */
    void add(EventPlayer player);

    /**
     * Removes a player from the team. If the player is not in the team, nothing happens.
     * @param player The player to leave the team
     */
    void remove(TeamPlayer player);

    /**
     * @return the name of the team
     */
    String getName();

    /**
     * Returns the team multiplier. the multiplier is a number between 1 and 0. this number depends on the team size. It is calculated as follows: 1 / team size
     * @return the Multiplier
     */
    double getMultiplier();

}
