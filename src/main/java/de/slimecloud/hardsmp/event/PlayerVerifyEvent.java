package de.slimecloud.hardsmp.event;

import net.dv8tion.jda.api.entities.Member;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerVerifyEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private final Member member;
	private Component actionbarMessage;
	private Component message;

	public PlayerVerifyEvent(@NotNull Player player, @NotNull Member member, @NotNull Component actionbarMessage, @NotNull Component message) {
		super(player);
		this.member = member;
		this.actionbarMessage = actionbarMessage;
		this.message = message;
	}

	public Member getMember() {
		return member;
	}

	public Component getActionbarMessage() {
		return actionbarMessage;
	}

	public void setActionbarMessage(Component actionbarMessage) {
		this.actionbarMessage = actionbarMessage;
	}

	public Component getMessage() {
		return message;
	}

	public void setMessage(Component message) {
		this.message = message;
	}

	public @NotNull HandlerList getHandlers() {
		return handlers;
	}


	public static HandlerList getHandlerList() {
		return handlers;
	}


}
