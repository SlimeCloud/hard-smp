package de.slimecloud.hardsmp.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface EventPlayer {

    void addPoints(double points);

    void setPoints(double points);

    void removePoints(double points);

    double getPoints();

    void setTeam(EventTeam team);

    Player getPlayer();

    OfflinePlayer getOfflinePlayer();

    EventTeam createTeam();

}
