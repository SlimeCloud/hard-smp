package de.slimecloud.hardsmp.subevent.replika;

import de.slimecloud.hardsmp.HardSMP;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class ReplikaListener implements Listener {

	private final Replika replika;

	public ReplikaListener(Replika replika) {
		this.replika = replika;
	}

	private boolean canEdit(Player player, Location location) {
		Plot plot = replika.getPlot(player.getUniqueId());
		if (!plot.getPosition().getWorld().equals(location.getWorld())) return true;
		double x = location.getX(), y = location.getY(), z = location.getZ();
		Vector pos1 = plot.getPosition().toVector().add(new Vector(replika.getPlotSpacing() + 1, 0, 1));
		Vector pos2 = pos1.clone().add(new Vector(replika.getPlotWidth() - 3, replika.getTopBorderHeight(), replika.getPlotWidth() - 3));
		return x>=pos1.getX() && y>=pos1.getY() && z>=pos1.getZ() &&
				x<=pos2.getX() && y<=pos2.getY() && z<=pos2.getZ();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getWorld().equals(replika.getWorld()) && event.getBlockPlaced().getType().equals(Material.ENDER_CHEST)) event.setCancelled(true);
		if (!canEdit(event.getPlayer(), event.getBlockPlaced().getLocation()) && !event.getPlayer().hasPermission("hardsmp.events.replika.bypass")) event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!canEdit(event.getPlayer(), event.getBlock().getLocation()) && !event.getPlayer().hasPermission("hardsmp.events.replika.bypass")) event.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Replika replika = HardSMP.getInstance().getSubEvents().getReplika();
		if (!replika.getIsStarted() && event.getPlayer().getWorld().equals(replika.getWorld()) && replika.getPlayers().contains(event.getPlayer()) && !event.getPlayer().hasPermission("hardsmp.events.replika.bypass")) event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Replika replika = HardSMP.getInstance().getSubEvents().getReplika();
		if (replika.getIsSetuped() || replika.getIsStarted()) replika.join(event.getPlayer());
	}
}
