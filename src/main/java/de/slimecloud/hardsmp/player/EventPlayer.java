package de.slimecloud.hardsmp.player;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EventPlayer {

    /**
     * adds points to this player
     *
     * @param points the amount of points to be added
     */
    void addPoints(double points);

    /**
     * set the number of points of the player
     * <p>
     * It is recommended to use only addPoints and removePoints
     *
     * @param points the new number of points of the player
     */
    void setPoints(double points);

    /**
     * remove a certain number of points from the player
     *
     * @param points the amount of points to be removed
     */
    void removePoints(double points);

    /**
     * @return the number of points of the player
     */
    double getPoints();

    /**
     * @return the actual number of points with all stats (play time, walked...)
     */
    double getActualPoints();

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
     * @return the Unique Id of the entity
     * @see UUID
     */
    UUID getUniqueId();

    /**
     * @return the Discord User of the Player or null if player is not verified
     * @see User
     */
    @Nullable User getDiscord();

}
