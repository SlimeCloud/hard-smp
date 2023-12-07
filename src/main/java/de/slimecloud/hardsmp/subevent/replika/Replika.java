package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.subevent.SubEvent;
import lombok.Getter;
import org.bukkit.*;
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
    private final Map<String, Build> schematics;

    private final List<Player> players;

    private final Map<UUID, Plot> plots;

    /**
     * true = x; false = z
     */
    private boolean nextDirection = false;

    private int currentPlotPosX = 0;
    private int currentPlotPosZ = 0;
    private int currentRow = 1;
    private int plotsInRow = 0;

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
        this.schematics = new HashMap<>();
        this.players = new ArrayList<>();
        this.plots = new HashMap<>();
        directory.mkdirs();
        if (directory.listFiles() != null) {
            Arrays.stream(directory.listFiles())
                    .filter(f -> f.getName().endsWith(".build"))
                    .forEach(f -> {
                        try {
                            String name = f.getName();
                            schematics.put(name.substring(0, name.length() - ".build".length()), Build.load(f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void registerSchematic(String name, Build build) {
        schematics.put(name, build);
    }

    public File getFile(String name) {
        return new File(directory, name + ".build");
    }

    private void putPlot(UUID uuid) {
        Location loc = new Location(world, currentPlotPosX, 0, currentPlotPosZ);
        plots.put(uuid, new Plot(this, loc));
    }


    private void generatePlot(UUID uuid) {
        putPlot(uuid);
        plotsInRow += 1;
        if (plotsInRow >= 5) {
            plotsInRow = 0;
            currentRow += 1;
            currentPlotPosZ = (currentRow - 1) * (plotLength +plotSpacing);
            currentPlotPosX = 0;
            return;
        }
        currentPlotPosX += plotWidth+plotSpacing;

    }

    public Plot getPlot(UUID uuid) {
        Plot plot = plots.get(uuid);
        if (plot == null) {
            generatePlot(uuid);
            plot = plots.get(uuid);
            plot.build();
        }
        return plot;
    }

    private World getWorld() {
        return getWorld(false);
    }


    //todo: reset old world - manuell?
    private World getWorld(boolean regenerate) {
        World world = Bukkit.getWorld("replika");
        if (regenerate && world != null) {
            if (world.getWorldFolder().delete()) plugin.getLogger().info("world was del");
            else plugin.getLogger().info("world was NOT del");
        }
        if (regenerate || world == null) {
            WorldCreator generator = new WorldCreator("replika");
            generator.generator(new ChunkGenerator() {
            });
            world = generator.createWorld();
        }
        this.world = world;
        return world;
    }

    @Override
    public void setup(Collection<Player> players) {
        getWorld(true);
        this.players.addAll(players);

        //this.players.forEach(player ->
        for (int i = 0; i < 12; i++)
        {
            UUID uui = UUID.randomUUID();
            Plot plot = getPlot(uui);
            Location plotLoc = plot.getPosition();
            Location teleportLoc = plotLoc.add((plotSpacing + (double) plotWidth /2), 1, (double) (plotLength / 2) /2);
            //player.teleport(teleportLoc);
            Bukkit.getWorld("replika").setBlockData(teleportLoc, Material.RED_WOOL.createBlockData());
        }
    }

    @Override
    public void join(Player player) {
        this.players.add(player);
        Plot plot = getPlot(player.getUniqueId());
        player.teleport(plot.getPosition().add(plotWidth, 0, plotLength));
    }

    @Override
    public void leave(Player player) {
        this.players.remove(player);
    }

    @Override
    public Collection<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public void stop() {

    }
}
