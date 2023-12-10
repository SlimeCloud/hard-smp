package de.slimecloud.hardsmp.subevent.replika;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.subevent.SubEvent;
import de.slimecloud.hardsmp.util.InventoryStorage;
import de.slimecloud.hardsmp.ui.Chat;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    private ArrayList<Player> finishedPlayer;

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
    private Boolean isSetuped = false;
    private World world = null;

    private Player victim = null;
    private int maxLevel = 10;

    private ScheduledTask actionbarTask;

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
        this.maxLevel = plugin.getConfig().getInt("events.replika.max-levels");
        this.finishedPlayer = new ArrayList<>();
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
        Player player = Bukkit.getPlayer(uuid);
        playerPlot.build();
        wierdBuild(playerPlot.getPosition().toLocation(getWorld()).add(plotSpacing + 1, 0, (double) plotLength / 2 + 1), String.valueOf(level));
        plugin.getLogger().info("Placed " + level + ". for " + Bukkit.getPlayer(uuid).getName());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5000, 1);
        PlayerController.getPlayer(Bukkit.getOfflinePlayer(player.getUniqueId())).addPoints(100);
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

            if (level - 1 == this.maxLevel) {
                player.sendMessage(HardSMP.getPrefix().append(
                        Component.text("Glückwunsch! Du hast alle Level geschafft!")
                ));

                player.setGameMode(GameMode.SPECTATOR);
                players.remove(player);
                Firework firework = getWorld().spawn(player.getLocation(), Firework.class);
                FireworkMeta data = firework.getFireworkMeta();
                data.addEffects(FireworkEffect.builder().withColor(Color.fromRGB(0xF6ED82)).withColor(Color.fromRGB(0x88d657)).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
                data.setPower(1);
                firework.setFireworkMeta(data);
                finishedPlayer.add(player);
                player.sendTitlePart(TitlePart.TITLE, Component.text(finishedPlayer.size(), HardSMP.yellowColor).append(Component.text(". Platz", HardSMP.getGreenColor())));
                Bukkit.broadcast(
                        HardSMP.getPrefix()
                                .append(Chat.getName(player)
                                .append(Component.text(" hat alle Level als ", HardSMP.greenColor)
                                .append(Component.text(finishedPlayer.size(), HardSMP.getYellowColor())
                                .append(Component.text(". geschafft!", HardSMP.getGreenColor())))))
                );
                //todo add top 3 points
                //  1. 20k
                //  2. 15k
                //  3. 10k
                //  default 1k wenn geschafft
                return true;
            }

            placeLevel(level, player.getUniqueId());
            return true;
        }
        return false;
    }

    private Build getCurrentBuild(Player player) {
        Plot playerPlot = plots.get(player.getUniqueId());
        Location loc1 = playerPlot.getPosition().toLocation(getWorld()).add(plotSpacing + 1, 0, 1);
        Location loc2 = loc1.toLocation(getWorld()).add(plotWidth - 3, topBorderHeight, plotWidth - 3); //we can use as z the same as in x because our build space is currently always a square
        return Build.scan(loc1, loc2, false, false);
    }

    private ArrayList<Build> registerLevel() {
        ArrayList<Build> newLevel = new ArrayList<>();


        for (int level = 1; level < this.maxLevel; level++) {
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
        isSetuped = true;
        getWorld(true);
        registerLevel();
        Bukkit.getScheduler().runTask(HardSMP.getInstance(), scheduledTask -> {
            this.players.addAll(players);

            if (victim == null) victim = Bukkit.getPlayer(UUID.fromString(plugin.getConfig().getString("events.replika.victimUUID")));
            this.players.remove(victim);

            this.players.forEach(player -> {
                UUID uui = player.getUniqueId();
                playerLevels.put(uui, 0);
                Plot plot = getPlot(uui);
                Location plotLoc = plot.getPosition().toLocation(plot.getPosition().getWorld());
                Location teleportLoc = plotLoc.add((plotSpacing + (double) plotWidth / 2), 1, (double) (plotLength / 2) / 2);
                player.teleport(teleportLoc);
                InventoryStorage.saveInventory(player);
            });


        });
    }

    @Override
    public void join(Player player) {
        if (victim == null) victim = Bukkit.getPlayer(UUID.fromString(plugin.getConfig().getString("events.replika.victimUUID")));
        if (player.getUniqueId().toString().contains(victim.getUniqueId().toString())) return;

        if (!this.players.contains(player)) this.players.add(player);
        if (!this.playerLevels.containsKey(player.getUniqueId())) this.playerLevels.put(player.getUniqueId(), isStarted ? 1 : 0);

        System.out.println(plots.get(player.getUniqueId()));
        Plot plot = getPlot(player.getUniqueId());
        Location plotLoc = plot.getPosition().toLocation(plot.getPosition().getWorld());
        Location teleportLoc = plotLoc.add((plotSpacing + (double) plotWidth / 2), 1, (double) (plotLength / 2) / 2);

        player.teleport(teleportLoc);
        player.setGameMode(GameMode.CREATIVE);
        InventoryStorage.saveInventory(player);


        if (isStarted) {
            System.out.println("player lvl: " + this.playerLevels.get(player.getUniqueId()));
            placeLevel(this.playerLevels.get(player.getUniqueId()), player.getUniqueId());
        }
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
        players.forEach(player -> {
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            InventoryStorage.restoreInventory(player);
        });
        players.removeAll(Bukkit.getOnlinePlayers());
        actionbarTask.cancel();
        isSetuped = false;
        isStarted = false;
    }

    @Override
    public void start() {
        this.players.forEach(player -> {

            player.sendTitlePart(TitlePart.TITLE, Component.text("ERKLÄRUNG", HardSMP.getYellowColor()));
            player.sendTitlePart(TitlePart.SUBTITLE, Component.text("Chat lesen!", HardSMP.getGreenColor()));

            player.sendMessage(Formatter.parseText(plugin.getConfig().getString("events.replika.info-message").replace("%finishCommand", "/replika finishLevel")));

            player.setGameMode(GameMode.CREATIVE);

            player.sendMessage(Component.newline().appendNewline().append(HardSMP.getPrefix()
                            .append(Component.text("Event startet in ", HardSMP.getGreenColor())
                                    .append(Component.text("60s", HardSMP.getYellowColor())))
                    ));

            placeLevel(1, player.getUniqueId());
            playerLevels.put(player.getUniqueId(), 1);
        });


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            isStarted = true;
            setActionbar();
            Bukkit.broadcast(HardSMP.getPrefix().append(Component.text("LETS GOO!", HardSMP.getGreenColor(), TextDecoration.BOLD)));
            Bukkit.broadcast(HardSMP.getPrefix().append(Component.text("LETS GOO!", HardSMP.getGreenColor(), TextDecoration.BOLD)));
            Bukkit.broadcast(HardSMP.getPrefix().append(Component.text("LETS GOO!", HardSMP.getGreenColor(), TextDecoration.BOLD)));
        }, 20*60);

    }

    private void setActionbar() {
         actionbarTask = Bukkit.getAsyncScheduler().runAtFixedRate(getPlugin(), scheduledTask -> {
            players.forEach(player -> player.sendActionBar(Component.text("Nutze ", HardSMP.getGreenColor()).append(
                    Component.text("/replika finishLevel", HardSMP.getYellowColor())
                    .append(Component.text( " um das Level zu beenden!", HardSMP.getGreenColor()))
            )));
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}
