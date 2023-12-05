package de.slimecloud.hardsmp.subevent.replika;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Plot {

    private final Replika replika;
    @Getter
    private final Location position;

    public Plot(Replika replika, Location position) {
        this.replika = replika;
        this.position = position;
    }

    public void build() {
        replika.getPlotSchematic().build(position);
    }
}
