package de.slimecloud.hardsmp.claim;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.Collection;
import java.util.List;

public class ClaimProtectionHandler implements Listener {

    private boolean isClaimed(Location loc, Player player) {
        if (Claim.allClaims.values().stream().anyMatch(claim -> claim.contains(loc) && !claim.getUuid().equals(player.getUniqueId().toString()))) {
            if (player.hasPermission("hardsmp.claim.bypass")) {
                player.sendMessage(Component.text("Du führst eine Aktion in geclaimtem Gebiet aus!", NamedTextColor.GOLD));
                return false;
            }
            return true;
        }
        return false;
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
    private void onButtonPress(PlayerInteractEvent event) {
        if (event.getAction().isRightClick() && event.getClickedBlock() != null && event.getClickedBlock().getBlockData().getMaterial().toString().contains("BUTTON") && isClaimed(event.getClickedBlock().getLocation(), event.getPlayer())) {
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

        if (!first.isEmpty() && !event.getBlocks().stream().allMatch(block -> first.get(0).contains(block.getLocation().add(event.getDirection().getDirection())))) {
            event.setCancelled(true);
        } else if (first.isEmpty() && event.getBlocks().stream().anyMatch(block -> claims.stream().anyMatch(claim -> claim.contains(block.getLocation().add(event.getDirection().getDirection()))))) {
            event.setCancelled(true);
        }
    }

    //ToDo: Find a workaround for fireballs being thrown into a claim
    @EventHandler
    private void onVehicleMove(VehicleMoveEvent event) {
        Collection<Claim> claims = Claim.allClaims.values();
        if (claims.stream().anyMatch(claim -> claim.contains(event.getTo()) && !claim.contains(event.getFrom()))) {
            event.getVehicle().setVelocity(event.getVehicle().getVelocity().multiply(-1));
        }
    }
}
