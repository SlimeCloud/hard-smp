package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MusicAdvancement extends AdvancementHandler {

	private final static List<Material> DISCS;

	static {
		DISCS = new ArrayList<>();
		for (Material value : Material.values()) if (value.name().contains("MUSIC_DISC")) DISCS.add(value);
	}

	private final NamespacedKey key;

	public MusicAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.MUSIC);
		this.key = new NamespacedKey(plugin, "discs.played");
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.JUKEBOX) || event.getItem() == null) return;

		Material material = event.getItem().getType();
		int index = DISCS.indexOf(material);
		if (index == -1) return;
		Player player = event.getPlayer();
		if (isDone(player)) return;
		Set<Integer> collected = Arrays.stream(PersistentDataHandler.get(player).reviseIntArrayWithDefault(key, a -> {
			Set<Integer> set = Arrays.stream(a).boxed().collect(Collectors.toSet());
			set.add(index);
			return set.stream().mapToInt(i -> i).toArray();
		}, new int[0])).boxed().collect(Collectors.toSet());
		if (collected.size() == DISCS.size()) unlock(player);
	}
}
