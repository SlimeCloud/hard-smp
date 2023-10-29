package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.advancement.AdvancementType;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public class GoldAdvancement extends AdvancementHandler {

    private final int requiredGold;
    private final NamespacedKey key;
    public GoldAdvancement(Plugin plugin, AdvancementType type, int requiredGold) {
        super(plugin, type);
        this.requiredGold = requiredGold;
        this.key = new NamespacedKey(plugin, "farmed.gold_ore");
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.DEEPSLATE_GOLD_ORE)) {
            if (isDone(player)) return;
            int gold = PersistentDataHandler.get(player).reviseIntWithDefault(key, c -> ++c, 0);
            if (gold>=requiredGold) unlock(player);
        }
    }

    public static class Gold1 extends GoldAdvancement {
        public Gold1(Plugin plugin) {
            super(plugin, CustomAdvancement.GOLD1, 100);
        }
    }

    public static class Gold2 extends GoldAdvancement {
        public Gold2(Plugin plugin) {
            super(plugin, CustomAdvancement.GOLD2, 500);
        }
    }

    public static class Gold3 extends GoldAdvancement {
        public Gold3(Plugin plugin) {
            super(plugin, CustomAdvancement.GOLD3, 1000);
        }
    }
}
