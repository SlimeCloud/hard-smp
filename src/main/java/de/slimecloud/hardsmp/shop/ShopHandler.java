package de.slimecloud.hardsmp.shop;

import de.slimecloud.hardsmp.Main;
import de.slimecloud.hardsmp.item.ItemManager;
import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.BiConsumer;

public class ShopHandler implements BiConsumer<NPC, Player> {

    private static final Inventory shopUI = Bukkit.createInventory(null, 3*9, "Shop");

    static {
        shopUI.setItem(0, Main.getInstance().getItemManager().getStack("chest-key"));
    }

    @Override
    public void accept(NPC npc, Player player) {
        npc.playAnimation(NPC.Animation.SWING_MAIN_ARM);
    }
}
