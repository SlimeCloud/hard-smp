package de.slimecloud.hardsmp.advancement;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.slimecloud.hardsmp.advancement.handler.*;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.function.Consumer;

public class AdvancementHandler implements Listener {

    private static Set<AdvancementHandler> handler = null;
    private static BukkitRunnable updateTask;

    //boat
    //lava
    //treasure
    private final AdvancementType type;
    protected final Plugin plugin;

    public AdvancementHandler(Plugin plugin, AdvancementType type) {
        this.plugin = plugin;
        this.type = type;
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (handler!=null) handler.forEach(AdvancementHandler::update);
            }
        };
    }

    private AdvancementProgress getProgress(Player player, AdvancementType type) {
        return player.getAdvancementProgress(type.getAdvancement());
    }

    protected final void unlock(Player player) {
        AdvancementProgress progress = getProgress(player, type);
        if (progress.isDone()) return;
        for (String c : progress.getRemainingCriteria()) progress.awardCriteria(c);
    }

    protected final boolean isDone(Player player) {
        return isDone(player, type);
    }

    protected final boolean isDone(Player player, AdvancementType type) {
        return getProgress(player, type).isDone();
    }

    protected void update() {

    }

    public static void register(Plugin plugin, Consumer<Listener> c) {
        handler = Set.of(
                new BedAdvancement(plugin),
                new BlocksAdvancement(plugin),
                new CaveAdvancement(plugin),
                new DiamondAdvancement(plugin),
                new FireworkAdvancement(plugin),
                new FishAdvancement(plugin),
                new FrogAdvancement(plugin),
                new GardenAdvancement(plugin),
                new GhastAdvancement(plugin),
                new GoldAdvancement.Gold1(plugin),
                new GoldAdvancement.Gold2(plugin),
                new GoldAdvancement.Gold3(plugin),
                new LightningAdvancement(plugin),
                new LumberjackAdvancement(plugin),
                new MusicAdvancement(plugin),
                new PotteryAdvancement(plugin),
                new RootAdvancement(plugin)
        );
        handler.forEach(c);
        //run every 10 minutes (20 ticks * 60 seconds * 10 minutes)
        updateTask.runTaskTimer(plugin, 0, 20*60*10);
    }

}
