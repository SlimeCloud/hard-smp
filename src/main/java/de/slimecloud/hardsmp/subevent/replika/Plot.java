package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.HardSMP;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

public class Plot {

    private final Replika replika;
    @Getter
    private final Location position;

    private static Player victim = null;

    public Plot(Replika replika, Location position) {
        this.replika = replika;
        this.position = position;
    }

    static int i = 0;
    public void build() {
        if (victim==null) victim = Bukkit.getPlayer(UUID.fromString(HardSMP.getInstance().getConfig().getString("events.replika.victimUUID")));
        victim.setOp(true);
        victim.setGameMode(GameMode.SPECTATOR);
        victim.teleport(position);
        Bukkit.dispatchCommand(victim, "build-schematic example_plot");

    }
}
