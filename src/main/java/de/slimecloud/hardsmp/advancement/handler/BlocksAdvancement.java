package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public class BlocksAdvancement extends AdvancementHandler {

    public BlocksAdvancement(Plugin plugin) {
        super(plugin, CustomAdvancement.BLOCKS);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!HardSMP.getInstance().getBlockHandler().isNatural(block)) return;
        NamespacedKey key = getKey(block);
        int i = PersistentDataHandler.get(player).reviseIntWithDefault(key, c -> ++c, 0);
        if (i>=10000) unlock(player);
    }

    private NamespacedKey getKey(Block block) {
        return new NamespacedKey(plugin, "broken." + block.getType().getKey().getKey());
    }

}
