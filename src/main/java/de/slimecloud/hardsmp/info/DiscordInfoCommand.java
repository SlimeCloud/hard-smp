package de.slimecloud.hardsmp.info;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.Option;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.verify.Verification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

@ApplicationCommand(name = "minecraft", defer = true)
public class DiscordInfoCommand {
	@ApplicationCommand(name = "Event Informationen", type = Command.Type.USER, defer = true)
	public static class UserInfoCommand {
		@ApplicationCommandMethod
		public void performCommand(UserContextInteractionEvent event) {
			Verification verification = Verification.load(event.getTarget());

			if(!verification.isVerified()) {
				event.getHook().editOriginal("Der Spieler gesuchte Spieler ist nicht verifiziert!").queue();
				return;
			}

			event.getHook().editOriginalEmbeds(buildInfo(Bukkit.getOfflinePlayer(verification.getMinecraftID()), verification)).queue();
		}
	}

	@Autocomplete("minecraft-name")
	public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
		event.replyChoices(Arrays.stream(Bukkit.getOfflinePlayers())
				.filter(p -> p.getName().startsWith(event.getFocusedOption().getValue()))
				.limit(OptionData.MAX_CHOICES)
				.map(p -> new Command.Choice(p.getName(), p.getUniqueId().toString()))
				.toList()
		).queue();
	}

	@ApplicationCommandMethod
	public void performCommand(SlashCommandInteractionEvent event, @Option(name = "minecraft-name", description = "Der Name des Spielers im Minecraft") String minecraftName) {
		OfflinePlayer player = null;

		try {
			player = Bukkit.getOfflinePlayer(UUID.fromString(minecraftName));
		} catch(IllegalArgumentException ignored) {}

		if(player == null) player = Bukkit.getOfflinePlayer(minecraftName);

		if(player.getName() == null) {
			event.getHook().editOriginal("Spieler nicht gefunden!").queue();
			return;
		}

		Verification verification = Verification.load(player.getUniqueId().toString());

		if(!verification.isVerified()) {
			event.getHook().editOriginal("Der Spieler gesuchte Spieler ist nicht verifiziert!").queue();
			return;
		}

		event.getHook().editOriginalEmbeds(buildInfo(player, verification)).queue();
	}

	public static MessageEmbed buildInfo(OfflinePlayer player, Verification verification) {
		return new EmbedBuilder()
				.setColor(Color.GREEN)
				.setTitle("Informationen zu **" + player.getName() + "**")
				.addField("Minecraft Name", player.getName(), true)
				.addField("Minecraft UUID", player.getUniqueId().toString(), true)
				.addField("Discord Name", "<@" + verification.getDiscordID() + ">", true)
				.addField("Punkte", "" + PlayerController.getPlayer(player).getActualPoints(), false)
				.build();
	}
}