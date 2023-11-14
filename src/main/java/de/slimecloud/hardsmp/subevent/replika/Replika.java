package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.subevent.SubEvent;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class Replika implements SubEvent {

    private final File directory;
    private final int plotWidth;
    private final int plotLength;
    private final int plotSpacing;
    private final Build plotSchematic;
    private final Set<Build> schematics;

    private final List<Player> players;


    public Replika(Plugin plugin) {
        this.directory = new File(plugin.getDataFolder(), "replika/schematics");
        YamlConfiguration plotConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "plot.yml"));
        this.plotWidth = plotConfig.getInt("plot.width");
        this.plotLength = plotConfig.getInt("plot.length");
        this.plotSpacing = plotConfig.getInt("plot.spacing");
        try {
            this.plotSchematic = Build.load(new File(plugin.getDataFolder(), "replika/" + plotConfig.getString("plot.schematic")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.schematics = new HashSet<>();
        this.players = new ArrayList<>();
        directory.mkdirs();
        if (directory.listFiles()!=null) {
            Arrays.stream(directory.listFiles())
                    .filter(f -> f.getName().endsWith(".build"))
                    .forEach(f -> {
                        try {
                            schematics.add(Build.load(f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void registerSchematic(Build build) {
        schematics.add(build);
    }

    public File getFile(String name) {
        return new File(directory, name + ".build");
    }


    @Override
    public void start(Collection<Player> players) {
        this.players.addAll(players);

    }

    @Override
    public void join(Player player) {
        this.players.add(player);
    }

    @Override
    public void leave(Player player) {
        this.players.remove(player);
    }

    @Override
    public Collection<Player> getPlayers() {
        return new ArrayList<>(players);
    }
}
