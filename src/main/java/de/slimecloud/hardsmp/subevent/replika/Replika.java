package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.subevent.SubEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class Replika implements SubEvent {

    private final Plugin plugin;

    private final File directory;
    /**
     * x axis
     */
    private final int plotWidth;
    /**
     * z axis
     */
    private final int plotLength;
    private final int topBorderHeight;
    private final int plotSpacing;
    private final Build plotSchematic;
    private final Set<Build> schematics;

    private final List<Player> players;

    private final Map<UUID, Plot> plots;

    /**
     * true = x; false = z
     */
    private boolean nextDirection = false;

    private int currentPlotX = 0;
    private int currentPlotZ = 0;
    private int maxPlotX = 0;
    private int maxPlotZ = 0;

    private World world = null;

    public Replika(Plugin plugin) {
        this.plugin = plugin;
        this.directory = new File(plugin.getDataFolder(), "replika/schematics");
        plugin.saveResource("replika/plot.yml", false);
        YamlConfiguration plotConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "replika/plot.yml"));
        this.plotWidth = plotConfig.getInt("plot.width");
        this.plotLength = plotConfig.getInt("plot.length");
        this.plotSpacing = plotConfig.getInt("plot.spacing");
        this.topBorderHeight = plotConfig.getInt("plot.top-border-height");
        try {
            this.plotSchematic = Build.load(new File(plugin.getDataFolder(), "replika/" + plotConfig.getString("plot.schematic")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.schematics = new HashSet<>();
        this.players = new ArrayList<>();
        this.plots = new HashMap<>();
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

    private void putPlot(UUID uuid) {
        plots.put(uuid, new Plot(this, new Location(world, currentPlotX, 0, currentPlotZ)));
    }

    private void generatePlot(UUID uuid) {
        if (nextDirection) {
            currentPlotX += plotWidth + plotSpacing;
            putPlot(uuid);
            if (currentPlotX>=maxPlotX) {
                nextDirection = false;
                maxPlotX = currentPlotX;
                currentPlotX = 0;
            }
        } else {
            currentPlotZ += plotLength + plotSpacing;
            putPlot(uuid);
            if (currentPlotZ>=maxPlotZ) {
                nextDirection = true;
                maxPlotZ = currentPlotZ;
                currentPlotZ = 0;
            }
        }
    }

    public Plot getPlot(UUID uuid) {
        Plot plot = plots.get(uuid);
        if (plot==null) {
            generatePlot(uuid);
            plot = plots.get(uuid);
            plot.build();
        }
        return plot;
    }

    private World getWorld() {
        return getWorld(false);
    }

    private World getWorld(boolean regenerate) {
        World world = Bukkit.getWorld("replika");
        if (regenerate && world!=null) world.getWorldFolder().delete();
        if (regenerate || world==null) {
            WorldCreator generator = new WorldCreator("replika");
            generator.generator(new ChunkGenerator() {});
            world = generator.createWorld();
        }
        this.world = world;
        return world;
    }

    @Override
    public void start(Collection<Player> players) {
        getWorld(true);
        this.players.addAll(players);
        this.players.forEach(p -> p.teleport(getPlot(p.getUniqueId()).getPosition().add(plotWidth, 0, plotLength)));
    }

    @Override
    public void join(Player player) {
        this.players.add(player);
        player.teleport(getPlot(player.getUniqueId()).getPosition().add(plotWidth, 0, plotLength));
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
