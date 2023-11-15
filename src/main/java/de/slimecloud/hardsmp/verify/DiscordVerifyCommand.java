package de.slimecloud.hardsmp.verify;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.option.Option;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.event.PlayerVerifyEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Instant;

@ApplicationCommand(name = "verify", description = "Verifier deinen Minecraft Account")
public class DiscordVerifyCommand {
    @ApplicationCommandMethod
    public void performCommand(SlashCommandInteractionEvent event,
                               @Option(description = "Gebe den Code ein der dir im Minecraft Chat angezeigt wird") String code
    ) {
        if (!MinecraftVerificationListener.activeCodes.asMap().containsValue(code)) {
            event.replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("Code nicht gefunden")
                            .setDescription("Der Code **" + code + "** wurde nicht gefunden\n" +
                                    "Bitte joine `" + HardSMP.getInstance().getServer().getIp() + "` und versuche es erneut mit dem angezeigten Code")
                            .setColor(Color.decode("#569d3c"))
                            .setTimestamp(Instant.now())
                            .build()
            ).setEphemeral(true).queue();
            return;
        }

        MinecraftVerificationListener.activeCodes.asMap().forEach((uuid, c) -> {
            if (!c.equals(code)) return;

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;

            Group group = HardSMP.getInstance().getLuckPerms().getGroupManager().getGroup("verified");

            if (group == null) {
                HardSMP.getInstance().getLogger().warning("Group 'verified' not found!");
                event.reply("Es ist ein Fehler aufgetreten, bitte wende dich an das Team!").setEphemeral(true).queue();

                player.sendMessage(HardSMP.getPrefix().append(Component.text("Es ist ein Fehler aufgetreten, bitte wende dich an das Team!", NamedTextColor.RED)));
                return;
            }

            HardSMP.getInstance().getLuckPerms().getUserManager().modifyUser(uuid, (User user) -> {
                user.data().clear(NodeType.INHERITANCE::matches);
                Node node = InheritanceNode.builder(group).build();
                user.data().add(node);
            });

            MinecraftVerificationListener.activeCodes.invalidate(uuid);

            Verification.load(uuid.toString())
                    .setDiscordId(event.getUser().getIdLong())
                    .save();

            PlayerVerifyEvent vevent = new PlayerVerifyEvent(player, event.getMember(),
                    Component.text("Erfolgreich Verifiziert!", TextColor.color(0x88d657)),
                    HardSMP.getPrefix()
                            .append(Component.text("Du wurdest erfolgreich", TextColor.color(0x88d657)))
                            .append(Component.text(" Verifiziert", TextColor.color(0x55cfc4), TextDecoration.BOLD))
                            .append(Component.text("!", TextColor.color(0x88d657))));

            Bukkit.getScheduler().runTask(HardSMP.getInstance(), () -> Bukkit.getPluginManager().callEvent(vevent));

            player.sendActionBar(vevent.getActionbarMessage());

            player.sendMessage(vevent.getMessage());

            event.replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("✅ Erfolgreich Verifiziert")
                            .setDescription("Du wurdest erfolgreich Verifiziert. Viel Spaß!")
                            .setColor(Color.decode("#569d3c"))
                            .setTimestamp(Instant.now())
                            .build()
            ).setEphemeral(true).queue();
        });
    }
}

