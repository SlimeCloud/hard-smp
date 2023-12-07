package de.slimecloud.hardsmp.subevent.replika;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

public class Plot {

    private final Replika replika;
    @Getter
    private final Location position;

    private static Player opfer = null;

    public Plot(Replika replika, Location position) {
        this.replika = replika;
        this.position = position;
    }

    static int i = 0;
    public void build() {
        if (opfer==null) opfer = Bukkit.getPlayer(UUID.fromString("cc4790ce-0c32-474a-b606-3d211402fea9"));
        opfer.setOp(true);
        opfer.setGameMode(GameMode.SPECTATOR);
        opfer.teleport(position);
        Bukkit.dispatchCommand(opfer, "build-schematic example_plot");

    }
}
