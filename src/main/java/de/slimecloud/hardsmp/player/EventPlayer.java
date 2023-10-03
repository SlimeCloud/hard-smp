package de.slimecloud.hardsmp.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EventPlayer {

    void addPoints(double points);

    void setPoints(double points);

    void removePoints(double points);

    double getPoints();

    TeamPlayer joinTeam(EventTeam team);

    Player getPlayer();

    OfflinePlayer getOfflinePlayer();

    UUID getUniqueId();

    TeamPlayer createTeam(String name);

    @Nullable EventTeam getTeam();

}
