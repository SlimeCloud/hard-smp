package de.slimecloud.hardsmp.claim;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

import java.util.Collection;
import java.util.List;

public class ClaimProtectionHandler implements Listener {

    private boolean isClaimed(Location loc, Player player) {
        return Claim.allClaims.values().stream().anyMatch(claim -> claim.getUuid().contains("-") && claim.contains(loc) && !claim.getUuid().equals(player.getUniqueId().toString()));
    }

    @EventHandler
    private void onChest(InventoryOpenEvent event) {
        if(event.getInventory().getType() == InventoryType.WORKBENCH
                || event.getInventory().getType() == InventoryType.PLAYER
                || event.getInventory().getType() == InventoryType.CREATIVE
                || event.getInventory().getType() == InventoryType.ENDER_CHEST
                || event.getInventory().getType() == InventoryType.CARTOGRAPHY
                || event.getInventory().getType() == InventoryType.CRAFTING
                || event.getInventory().getType() == InventoryType.GRINDSTONE
                || event.getInventory().getType() == InventoryType.LOOM
                || event.getInventory().getType() == InventoryType.SMITHING
                || event.getInventory().getType() == InventoryType.STONECUTTER
                || event.getInventory().getLocation() == null) return;
        if (isClaimed(event.getInventory().getLocation(), (Player) event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst dies hier nicht öffnen!"));
        }
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst hier nicht abbauen!"));
        }
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst hier nicht bauen!"));
        }
    }

    @EventHandler
    private void onEntityPlace(EntityPlaceEvent event) {
        if (event.getPlayer() != null && isClaimed(event.getEntity().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst das hier nicht benutzen!"));
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && isClaimed(event.getEntity().getLocation(), player)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("§cDu kannst das hier nicht boxen!"));
        }
    }


    @EventHandler
    private void onSignEdit(SignChangeEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst dieses Schild nicht editieren!"));
        }
    }

    @EventHandler
    private void onLectern(PlayerTakeLecternBookEvent event) {
        if (isClaimed(event.getLectern().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst dieses Schild nicht editieren!"));
        }
    }

    @EventHandler
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst das hier nicht benutzen!"));
        }
    }

    @EventHandler
    private void onBucketFill(PlayerBucketFillEvent event) {
        if (isClaimed(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst das hier nicht benutzen!"));
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.FARMLAND))
            event.setCancelled(true);
        if (event.getAction().isRightClick() && event.getClickedBlock() != null && isClaimed(event.getClickedBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("§cDu kannst das hier nicht benutzen!"));
        }
    }

    @EventHandler
    private void onExplosion(BlockExplodeEvent event) {
        Collection<Claim> claims = Claim.allClaims.values();
        event.blockList().removeIf(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation())));
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        Collection<Claim> claims = Claim.allClaims.values();
        event.blockList().removeIf(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation())));
    }

    @EventHandler
    private void onFlow(BlockFromToEvent event) {
        Collection<Claim> claims = Claim.allClaims.values();
        if (claims.stream().anyMatch(claim -> claim.contains(event.getToBlock().getLocation()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPistonExtend(BlockPistonExtendEvent event) {
        Collection<Claim> claims = Claim.allClaims.values();
        List<Claim> first = claims.stream().filter(claim -> claim.contains(event.getBlock().getLocation().add(event.getDirection().getDirection().multiply(-1)))).toList();

        if (!first.isEmpty() && !event.getBlocks().stream().allMatch(block -> first.get(0).contains(block.getLocation().add(event.getDirection().getDirection())))) {
            event.setCancelled(true);
        } else if (first.isEmpty() && event.getBlocks().stream().anyMatch(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation().add(event.getDirection().getDirection()))))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPistonRetract(BlockPistonRetractEvent event) {
        Collection<Claim> claims = Claim.allClaims.values();
        List<Claim> first = claims.stream().filter(claim -> claim.contains(event.getBlock().getLocation().add(event.getDirection().getDirection()))).toList();

        if (!first.isEmpty() && !event.getBlocks().stream().allMatch(block -> first.get(0).contains(block.getLocation()))) {
            event.setCancelled(true);
        } else if (first.isEmpty() && event.getBlocks().stream().anyMatch(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation())))) {
            event.setCancelled(true);
        }
    }

    //ToDo: Find a workaround for fireballs being thrown into a claim
    @EventHandler
    private void onRide(PlayerInteractEntityEvent event) {
        if (isClaimed(event.getPlayer().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
