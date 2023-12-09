package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.build.BuildFormat;
import de.slimecloud.hardsmp.subevent.SubEvent;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
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
    @Getter
    private final int plotWidth;

    /**
     * z axis
     */
    @Getter
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
    private ArrayList<Build> levels;
    private Map<UUID, Integer> playerLevels;
    private Boolean isStarted = false;
    private World world = null;

    private Player victim = null;

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
        this.playerLevels = new HashMap<>();
        this.plots = new HashMap<>();
        this.victim = Bukkit.getPlayer(UUID.fromString(plugin.getConfig().getString("events.replika.victimUUID")));
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

    Set<UUID> uuids = new HashSet<>();
    int i = 0;

    private void generatePlot(UUID uuid) {
        uuids.add(uuid);
        i++;
        putPlot(uuid);
        plotsInRow += 1;
        if (plotsInRow >= 5) {
            plotsInRow = 0;
            currentRow += 1;
            currentPlotPosZ = (currentRow - 1) * (plotLength + plotSpacing);
            currentPlotPosX = 0;
            return;
        }
        currentPlotPosX += plotWidth + plotSpacing;
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

    World getWorld() {
        return getWorld(false);
    }


    //todo: reset old world - manuell?
    World getWorld(boolean regenerate) {
        if (!regenerate && this.world!=null) return this.world;
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

    private void placeLevel(int level, UUID uuid) {
        Plot playerPlot = plots.get(uuid);
        playerPlot.build();
        wierdBuild(playerPlot.getPosition().toLocation(getWorld()).add(plotSpacing + 1, 0, (double) plotLength / 2 + 1), String.valueOf(level));
    }

    public Boolean checkLevel(Player player) {
        int level = playerLevels.get(player.getUniqueId());

        Build template;
        try {
            template = Build.load(getFile(String.valueOf(level)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Build currentBuild = getCurrentBuild(player);

        if (currentBuild.equals(template)) {
            playerLevels.put(player.getUniqueId(), ++level);
            placeLevel(level, player.getUniqueId());
            return true;
        }
        return false;
    }

    private Build getCurrentBuild(Player player) {
        Plot playerPlot = plots.get(player.getUniqueId());
        Location loc1 = playerPlot.getPosition().toLocation(getWorld()).add(plotSpacing + 1, 0, 1);
        Location loc2 = loc1.toLocation(getWorld()).add(plotWidth - 3, topBorderHeight, plotWidth - 3); //we can use as z the same as in x because our build space is currently always a square
        File file = getFile("temp_"+player.getUniqueId());
        try {
            return Build.scan(file, loc1, loc2, false, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Build> registerLevel() {
        ArrayList<Build> newLevel = new ArrayList<>();
        int maxLevel = HardSMP.getInstance().getConfig().getInt("events.replika.max-levels");

        for (int level = 1; level < maxLevel; level++) {
            try {
                newLevel.add(Build.load(getFile(String.valueOf(level))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.levels = newLevel;
    }

    //just don't ask, it works....
    //(say thanks to spigot, that we have do this)
    public void wierdBuild(Location location, String schematicName) {
        if (victim == null) victim = Bukkit.getPlayer(UUID.fromString(plugin.getConfig().getString("events.replika.victimUUID")));
        victim.setOp(true);
        victim.setGameMode(GameMode.SPECTATOR);
        victim.teleport(location);
        Bukkit.dispatchCommand(victim, "build-schematic " + schematicName.replace(".build", ""));
    }

    @Override
    public void setup(Collection<Player> players) {
        if (isStarted) return;
        getWorld(true);
        registerLevel();
        Bukkit.getScheduler().runTask(HardSMP.getInstance(), scheduledTask -> {
            this.players.addAll(players);
            this.players.remove(victim);

            //todo change back to tp instead of looping and placing blocks
            //this.players.forEach(player ->
            for (int i = 0; i < 12; i++) {
                UUID uui = UUID.randomUUID();
                playerLevels.put(uui, 0);
                Plot plot = getPlot(uui);
                Location plotLoc = plot.getPosition().toLocation(plot.getPosition().getWorld());
                Location teleportLoc = plotLoc.add((plotSpacing + (double) plotWidth / 2), 1, (double) (plotLength / 2) / 2);
                //player.teleport(teleportLoc);
                getWorld().setBlockData(teleportLoc, Material.RED_WOOL.createBlockData());
            }
        });
    }

    @Override
    public void join(Player player) {
        if (victim == null) victim = Bukkit.getPlayer(UUID.fromString(plugin.getConfig().getString("events.replika.victimUUID")));
        if (player.getUniqueId().equals(victim.getUniqueId())) return;
        this.players.add(player); //todo check if player is already in this map
        this.playerLevels.put(player.getUniqueId(), isStarted ? 1 : 0);
        Plot plot = getPlot(player.getUniqueId());
        Location plotLoc = plot.getPosition().toLocation(plot.getPosition().getWorld());
        Location teleportLoc = plotLoc.add((plotSpacing + (double) plotWidth / 2), 1, (double) (plotLength / 2) / 2);
        if (isStarted) {
            placeLevel(1, player.getUniqueId());
        }
        player.teleport(teleportLoc);
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

    @Override
    public void start() {
        //todo Game info for player boden immer ausgefüllt,
        //todo enable movement, set fly, set gamemode
        Bukkit.getScheduler().runTask(HardSMP.getInstance(), scheduledTask -> {
            plots.forEach((uuid, plot) -> {
                placeLevel(1, uuid);
                playerLevels.put(uuid, 1);
            });
        });
        this.isStarted = true;
    }
}
