package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class DiamondAdvancement extends AdvancementHandler {

    private final NamespacedKey key;
    public DiamondAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.DIAMOND);
        this.key = new NamespacedKey(plugin, "farmed.diamonds");
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType().equals(Material.DIAMOND_ORE) || block.getType().equals(Material.DEEPSLATE_DIAMOND_ORE)) {
            Collection<ItemStack> drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player);
            int dias = PersistentDataHandler.get(player).reviseIntWithDefault(key, diamonds -> {
                for (ItemStack drop : drops) if (drop.getType().equals(Material.DIAMOND)) diamonds+=drop.getAmount();
                return diamonds;
            }, 0);
            if (dias>=100) unlock(player);
        }
    }
}
