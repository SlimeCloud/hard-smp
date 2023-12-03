package de.slimecloud.hardsmp.shop.claimshop;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

import static net.kyori.adventure.text.format.TextColor.color;

public class ClaimShopNPC {
    public ClaimShopNPC(Location location) {
        Villager shop = (Villager)location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        shop.setSilent(true);
        shop.customName(Component.text("Bauamt", color(0x88D657)));
        shop.getPersistentDataContainer().set(HardSMP.getInstance().CLAIM_SHOP_KEY, PersistentDataType.STRING, "claimshop");
        shop.setCustomNameVisible(true);
        shop.setPersistent(true);
        shop.setAI(false);
        shop.setBodyYaw(location.getYaw());
    }
}
