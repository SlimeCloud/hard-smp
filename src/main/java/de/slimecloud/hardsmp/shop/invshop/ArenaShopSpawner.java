package de.slimecloud.hardsmp.shop.invshop;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;
import java.util.Random;

import static net.kyori.adventure.text.format.TextColor.color;

public class ArenaShopSpawner {
    public static Map<Material, EntityType> entities = Map.of(
            Material.ZOMBIE_SPAWN_EGG, EntityType.ZOMBIE,
            Material.SPIDER_SPAWN_EGG, EntityType.SPIDER,
            Material.CAVE_SPIDER_SPAWN_EGG, EntityType.CAVE_SPIDER,
            Material.SKELETON_SPAWN_EGG, EntityType.SKELETON,
            Material.WITHER_SKELETON_SPAWN_EGG, EntityType.WITHER_SKELETON,
            Material.CREEPER_SPAWN_EGG, EntityType.CREEPER,
            Material.WITCH_SPAWN_EGG, EntityType.WITCH
    );

    public static void buy(Player player, Material mat, int amount) {
        if (entities.get(mat) == null) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cEs ist etwas schiefgelaufen! Bitte wende dich ans Team!")));
            return;
        }

        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("arenashop.location");

        if (section == null) {
            player.sendMessage(HardSMP.getPrefix().append(Component.text("§cEs ist etwas schiefgelaufen! Bitte wende dich ans Team!")));
            return;
        }

        Location spawnLocation = new Location(
                player.getWorld(),
                new Random().nextInt(section.getInt("x1"), section.getInt("x2")) + 0.5,
                section.getInt("y") + 0.5,
                new Random().nextInt(section.getInt("z1"), section.getInt("z2")) + 0.5
        );

        while (spawnLocation.getBlock().getType() != Material.AIR)
            spawnLocation = new Location(
                    player.getWorld(),
                    new Random().nextInt(section.getInt("x1"), section.getInt("x2")) + 0.5,
                    section.getInt("y") + 0.5,
                    new Random().nextInt(section.getInt("z1"), section.getInt("z2")) + 0.5
            );

        LivingEntity entity;
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta lmeta = ((LeatherArmorMeta) helmet.getItemMeta());
        lmeta.setColor(Color.RED);
        helmet.setItemMeta(lmeta);

        for (int i = 0; i < amount; i++) {
            entity = (LivingEntity) player.getWorld().spawnEntity(spawnLocation, entities.get(mat));

            if (entity.getEquipment() == null) continue;


            entity.getEquipment().setHelmet(helmet);
        }

        player.sendMessage(HardSMP.getPrefix().append(Component.text(entities.get(mat).toString().replace('_', ' ') + " " + amount + "x gespawnt!", color(0x88D657))));
    }
}
