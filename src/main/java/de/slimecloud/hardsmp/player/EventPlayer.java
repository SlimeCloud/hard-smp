package de.slimecloud.hardsmp.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EventPlayer {

    /**
     * adds points to this player
     * @param points the amount of points to be added
     */
    void addPoints(double points);

    /**
     * set the number of points of the player
     * <p>
     * It is recommended to use only addPoints and removePoints
     * @param points the new number of points of the player
     */
    void setPoints(double points);

    /**
     * remove a certain number of points from the player
     * @param points the amount of points to be removed
     */
    void removePoints(double points);

    /**
     * @return the number of points of the player
     */
    double getPoints();

    /**
     * adds the player to a team. If the player is already in a team, he will automatically leave it.
     * @param team the team to which it should be added
     * @return the TeamPlayer object of the player
     * @see TeamPlayer
     */
    TeamPlayer joinTeam(EventTeam team);

    /**
     * @return the spigot player object of the EventPlayer or null if the player is offline
     * @see OfflinePlayer
     * @see Player
     */
    @Nullable Player getPlayer();

    /**
     * @return the Spigot OfflinePlayer Object of the EventPlayer
     * @see OfflinePlayer
     */
    OfflinePlayer getOfflinePlayer();

    /**
     * @return the Unique Id of the player
     * @see UUID
     */
    UUID getUniqueId();

    /**
     * Creates a team with the specified name and this player as the leader
     * @param name the name of the new team
     * @return the TeamPlayer object of the player
     * @see TeamPlayer
     */
    TeamPlayer createTeam(String name);

    /**
     * @return The player's team if he is in one, otherwise null
     * @see EventTeam
     */
    @Nullable EventTeam getTeam();

}
