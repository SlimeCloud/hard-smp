package de.slimecloud.hardsmp.event;

import de.slimecloud.hardsmp.database.DataClass;
import de.slimecloud.hardsmp.player.data.Claim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class ClaimProtectionHandler implements Listener {

    private boolean isClaimed(Location loc, Player player) {
        return DataClass.loadAll(
                Claim::new,
                Collections.emptyMap()
        ).stream().anyMatch(claim -> claim.contains(loc) && !claim.uuid.equals(player.getUniqueId().toString()));
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du kannst hier nicht abbauen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du kannst hier nicht bauen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onEntityPlace(EntityPlaceEvent event) {
        if (event.getPlayer() != null && isClaimed(event.getEntity().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du kannst das hier nicht benutzen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && isClaimed(event.getEntity().getLocation(), player)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Du kannst das hier nicht boxen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du kannst das hier nicht benutzen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onBucketFill(PlayerBucketFillEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du kannst das hier nicht benutzen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onButtonPress(PlayerInteractEvent event) {
        if (event.getAction().isRightClick() && event.getClickedBlock() != null && event.getClickedBlock().getBlockData().getMaterial().toString().contains("BUTTON") && isClaimed(event.getClickedBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du kannst das hier nicht benutzen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    private void onExplosion(BlockExplodeEvent event) {
        List<Claim> claims = DataClass.loadAll(Claim::new, Collections.emptyMap());
        event.blockList().removeIf(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation())));
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        List<Claim> claims = DataClass.loadAll(Claim::new, Collections.emptyMap());
        event.blockList().removeIf(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation())));
    }

    @EventHandler
    private void onFlow(BlockFromToEvent event) {
        List<Claim> claims = DataClass.loadAll(Claim::new, Collections.emptyMap());
        if (claims.stream().anyMatch(claim -> claim.contains(event.getToBlock().getLocation()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPistonExtend(BlockPistonExtendEvent event) {
        List<Claim> claims = DataClass.loadAll(Claim::new, Collections.emptyMap());
        if (event.getBlocks().stream().anyMatch(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation().add(event.getDirection().getDirection()))))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPistonRetract(BlockPistonRetractEvent event) {
        List<Claim> claims = DataClass.loadAll(Claim::new, Collections.emptyMap());
        if (event.getBlocks().stream().anyMatch(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation())))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityEnterBlock(EntityEnterBlockEvent event) {
        if (!(event.getEntityType() == EntityType.FIREBALL ||
                event.getEntityType() == EntityType.DRAGON_FIREBALL ||
                event.getEntityType() == EntityType.SMALL_FIREBALL ||
                event.getEntityType() == EntityType.PRIMED_TNT ||
                event.getEntityType().toString().contains("MINECART") ||
                event.getEntityType().toString().contains("BOAT") ||
                event.getEntityType() == EntityType.FALLING_BLOCK ||
                event.getEntityType() == EntityType.ARMOR_STAND ||
                event.getEntityType() == EntityType.SNOWMAN ||
                event.getEntityType() == EntityType.WITHER_SKULL)) return;

        List<Claim> claims = DataClass.loadAll(Claim::new, Collections.emptyMap());
        if (claims.stream().anyMatch(claim -> claim.contains(event.getBlock().getLocation()) && !claim.contains(event.getEntity().getLocation()))) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(-1));
        }

    }

}
