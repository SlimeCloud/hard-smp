package de.slimecloud.hardsmp.advancement.handler;

import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.advancement.CustomAdvancement;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class BedAdvancement extends AdvancementHandler {

	//10 in game days in ticks
	private final static int TICKS = 24000 * 10;

	public BedAdvancement(Plugin plugin) {
		super(plugin, CustomAdvancement.BED);
	}

	@Override
	protected void update() {
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (!isDone(p) && p.getStatistic(Statistic.TIME_SINCE_REST) >= TICKS) unlock(p);
		});
	}
}
