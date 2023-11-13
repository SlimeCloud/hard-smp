package de.slimecloud.hardsmp.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class ListLookingCords {

	private static final int DISTANCE = 5;

	public static void listAutomaticLoockingCoords(List<String> list, Player p, String which, boolean includeCurrent) {
		ListLookingCords llc = new ListLookingCords(p);
		switch (which.toLowerCase()) {
			case "x" -> {
				list.add(llc.getX(false));
				list.add(llc.getX(false) + " " + llc.getY(false));
				list.add(llc.getX(true));
				if (includeCurrent) {
					list.add("~ ~ ~");
					list.add("~ ~");
					list.add("~");
				}
			}
			case "y" -> {
				list.add(llc.getY(false));
				list.add(llc.getY(true));
				if (includeCurrent) {
					list.add("~ ~");
					list.add("~");
				}
			}
			case "z" -> {
				list.add(llc.getZ());
				if (includeCurrent) list.add("~");
			}
		}
	}
	private final Player player;

	public ListLookingCords(Player player) {
		this.player = player;
	}

	public String getX(boolean b) {
		Block block = player.getTargetBlock(null, DISTANCE);
		String s = "";
		if (block.getType().equals(Material.AIR)) {
			s += "~";
		} else {
			s += Integer.toString(block.getLocation().getBlockX());
		}
		if (b) {
			s += " " + getY(true);
		}
		return s;
	}

	public String getY(boolean b) {
		Block block = player.getTargetBlock(null, DISTANCE);
		String s = "";
		if (block.getType().equals(Material.AIR)) {
			s += "~";
		} else {
			s += Integer.toString(block.getLocation().getBlockY());
		}
		if (b) {
			s += " " + getZ();
		}
		return s;
	}

	public String getZ() {
		Block block = player.getTargetBlock(null, DISTANCE);
		if (block.getType().equals(Material.AIR)) {
			return "~";
		} else {
			return Integer.toString(block.getLocation().getBlockZ());
		}
	}
}
