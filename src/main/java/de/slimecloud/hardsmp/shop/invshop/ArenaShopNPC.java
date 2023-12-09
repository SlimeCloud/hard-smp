package de.slimecloud.hardsmp.shop.invshop;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;
import org.bukkit.persistence.PersistentDataType;

import static net.kyori.adventure.text.format.TextColor.color;

public class ArenaShopNPC {
    public ArenaShopNPC(Location location) {
        Warden shop = (Warden) location.getWorld().spawnEntity(location, EntityType.WARDEN);
        shop.setSilent(true);
        shop.customName(Component.text("Wächter der Arena", color(0x88D657)));
        shop.getPersistentDataContainer().set(HardSMP.getInstance().ARENA_SHOP_KEY, PersistentDataType.STRING, "arenashop");
        shop.setCustomNameVisible(true);
        shop.setPersistent(true);
        shop.setInvulnerable(true);
        shop.setAI(false);
        shop.setBodyYaw(location.getYaw());
    }
}
