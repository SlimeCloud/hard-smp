package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FishAdvancement extends AdvancementHandler {

	private final static List<Material> FISHS = List.of(Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH);

	private final NamespacedKey key;

	public FishAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.FISH);
		this.key = new NamespacedKey(plugin, "fish.cocked");
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (isDone(player)) return;
		Material material = event.getItem().getType();
		material = material == Material.COOKED_COD ? Material.COD : material;
		material = material == Material.COOKED_SALMON ? Material.SALMON : material;
		int index = FISHS.indexOf(material);
		Set<Integer> collected = Arrays.stream(PersistentDataHandler.get(player).reviseIntArrayWithDefault(key, a -> {
			Set<Integer> set = Arrays.stream(a).boxed().collect(Collectors.toSet());
			set.add(index);
			return set.stream().mapToInt(i -> i).toArray();
		}, new int[0])).boxed().collect(Collectors.toSet());
		if (collected.size() == FISHS.size()) unlock(player);
	}
}
