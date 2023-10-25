package de.slimecloud.hardsmp.advancement.handler;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class BlocksAdvancement extends AdvancementHandler {

    public BlocksAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.BLOCKS);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakBlockEvent event) {
        Player player = null;
        Block block = event.getBlock();
        PersistentDataContainer container = player.getPersistentDataContainer();
        NamespacedKey key = getKey(block);
        int i = 0;
        if (container.has(key)) i = container.get(key, PersistentDataType.INTEGER);
        container.set(key, PersistentDataType.INTEGER, ++i);
        if (i>=10000) unlock(player);
    }

    private NamespacedKey getKey(Block block) {
        return new NamespacedKey(plugin, "broken." + block.getType().getKey().getKey());
    }

}
