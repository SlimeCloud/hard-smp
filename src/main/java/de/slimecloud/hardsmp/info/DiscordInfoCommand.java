package de.slimecloud.hardsmp.info;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Autocomplete;
import de.mineking.discordutils.commands.option.Option;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.verify.Verification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
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

@ApplicationCommand(name = "info", description = "Zeigt informationen zu einem HardSMP-Spieler an")
public class DiscordInfoCommand {
    @ApplicationCommand(name = "Event Informationen", type = Command.Type.USER, defer = true)
    public static class UserInfoCommand {
        @ApplicationCommandMethod
        public void performCommand(UserContextInteractionEvent event) {
            Verification verification = Verification.load(event.getTarget());

            if (!verification.isVerified()) {
                event.getHook().editOriginal("Der Spieler gesuchte Spieler ist nicht verifiziert!").queue();
                return;
            }

            event.getHook().editOriginalEmbeds(buildInfo(Bukkit.getOfflinePlayer(UUID.fromString(verification.getMinecraftID())), verification)).queue();
        }
    }

    @ApplicationCommand(name = "discord", description = "Sucht nach einen Spieler über Discord", defer = true)
    public static class DiscordCommand {
        @ApplicationCommandMethod
        public void performCommand(SlashCommandInteractionEvent event, @Option(description = "Das Server-Mitglied") Member member) {
            Verification verification = Verification.load(member);

            if (!verification.isVerified()) {
                event.getHook().editOriginal("Der gesuchte Spieler ist nicht verifiziert!").queue();
                return;
            }

            event.getHook().editOriginalEmbeds(buildInfo(Bukkit.getOfflinePlayer(UUID.fromString(verification.getMinecraftID())), verification)).queue();
        }
    }

    @ApplicationCommand(name = "minecraft", description = "Sucht nach einem Spieler über Minecraft", defer = true)
    public static class MinecraftCommand {
        @Autocomplete("name")
        public void handleAutocomplete(CommandAutoCompleteInteractionEvent event) {
            event.replyChoices(Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(p -> p.getName() != null)
                    .filter(p -> p.getName().startsWith(event.getFocusedOption().getValue()))
                    .limit(OptionData.MAX_CHOICES)
                    .map(p -> new Command.Choice(p.getName(), p.getName()))
                    .toList()
            ).queue();
        }

        @ApplicationCommandMethod
        public void performCommand(SlashCommandInteractionEvent event, @Option(name = "name", description = "Der Name des Spielers im Minecraft") String minecraftName) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(minecraftName);

            if (player.getName() == null) {
                event.getHook().editOriginal("Spieler nicht gefunden!").queue();
                return;
            }

            Verification verification = Verification.load(player.getUniqueId().toString());

            if (!verification.isVerified()) {
                event.getHook().editOriginal("Der gesuchte Spieler ist nicht verifiziert!").queue();
                return;
            }

            event.getHook().editOriginalEmbeds(buildInfo(player, verification)).queue();
        }
    }

    private final static Color color = Color.decode("#569d3c");

    public static MessageEmbed buildInfo(OfflinePlayer player, Verification verification) {
        var user = HardSMP.getInstance().getDiscordBot().jdaInstance.getGuildById(HardSMP.getInstance().getConfig().getLong("discord.guild")).retrieveMemberById(verification.getDiscordID()).complete();

        return new EmbedBuilder()
                .setColor(color)
                .setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl())
                .setThumbnail("https://mc-heads.net/avatar/" + verification.getMinecraftID())
                .addField("Minecraft Name", player.getName(), true)
                .addField("Minecraft UUID", player.getUniqueId().toString(), true)
                .addField("Punkte", "" + (int) PlayerController.getPlayer(player).getActualPoints(), true)
                .build();
    }
}
