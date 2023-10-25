package de.slimecloud.hardsmp.advancement.handler;

import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DiamondAdvancement extends AdvancementHandler {
    public DiamondAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.DIAMOND);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakBlockEvent event) {
        Block block = event.getBlock();
        Player player = null;
        if (block.getType().equals(Material.DIAMOND_ORE) || block.getType().equals(Material.DEEPSLATE_DIAMOND_ORE)) {
            List<ItemStack> drops = event.getDrops();
            PersistentDataContainer container = player.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "farmed.diamonds");
            int diamonds = 0;
            if (container.has(key)) diamonds = container.get(key, PersistentDataType.INTEGER);
            for (ItemStack drop : drops) if (drop.getType().equals(Material.DIAMOND)) diamonds+=drop.getAmount();
            container.set(key, PersistentDataType.INTEGER, diamonds);
            if (diamonds>=100) unlock(player);
        }
    }
}
