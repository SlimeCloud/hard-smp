package de.slimecloud.hardsmp.advancement;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.slimecloud.hardsmp.advancement.handler.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AdvancementHandler implements Listener {
    //boat
    //lava
    //treasure
    private final static List<Listener> handlers = new ArrayList<>();
    private final AdvancementType type;
    protected final Plugin plugin;

    public AdvancementHandler(Plugin plugin, AdvancementType type) {
        this.plugin = plugin;
        this.type = type;
        handlers.add(this);
    }

    private AdvancementProgress getProgress(Player player) {
        return player.getAdvancementProgress(type.getAdvancement());
    }

    protected void unlock(Player player) {
        AdvancementProgress progress = getProgress(player);
        if (progress.isDone()) return;
        for (String c : progress.getRemainingCriteria()) progress.awardCriteria(c);
    }

    protected boolean isDone(Player player) {
        return getProgress(player).isDone();
    }

    public static void register(Plugin plugin) {
        new BlocksAdvancement(plugin);
        new CaveAdvancement(plugin);
        new DiamondAdvancement(plugin);
        new GhastAdvancement(plugin);
        new GoldAdvancement.Gold1(plugin);
        new GoldAdvancement.Gold2(plugin);
        new GoldAdvancement.Gold3(plugin);
    }

    public static void registerListeners(Consumer<Listener> c) {
        handlers.forEach(c::accept);
    }

}
