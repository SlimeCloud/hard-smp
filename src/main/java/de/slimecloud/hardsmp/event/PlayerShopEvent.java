package de.slimecloud.hardsmp.event;

import de.slimecloud.hardsmp.shop.Offer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class PlayerShopEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;

	private final Offer offer;
	private ItemStack item;

	public PlayerShopEvent(@NotNull Player who, Offer offer, ItemStack item) {
		super(who);
		this.offer = offer;
		this.item = item;
	}

	public @NotNull HandlerList getHandlers() {
		return handlers;
	}


	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}
