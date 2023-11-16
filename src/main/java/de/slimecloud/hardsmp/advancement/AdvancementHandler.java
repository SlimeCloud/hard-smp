package de.slimecloud.hardsmp.advancement;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.slimecloud.hardsmp.advancement.handler.*;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.function.Consumer;

public class AdvancementHandler implements Listener {

    //boat
    //lava
    //treasure
    private final AdvancementType type;
    protected final Plugin plugin;

    public AdvancementHandler(Plugin plugin, AdvancementType type) {
        this.plugin = plugin;
        this.type = type;
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

    public static void register(Plugin plugin, Consumer<Listener> c) {
        Set.of(
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
        ).forEach(c);
    }

}
