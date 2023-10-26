package de.slimecloud.hardsmp.verify;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.event.PlayerVerifyEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DiscordVerifyCommand extends ListenerAdapter {

    private final HardSMP plugin;
    private final LuckPerms luckPerms;

    public DiscordVerifyCommand(HardSMP plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals("verify")) return;
        String code = event.getOption("code").getAsString().toUpperCase();

        if (!(Verify.activeCodes.containsValue(code))) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Code nicht gefunden")
                    .setDescription("Der Code **" + code + "** wurde nicht gefunden\n" +
                            "Bitte joine `" + HardSMP.getInstance().getServer().getIp() + "` und versuche es erneut mit dem angezeigten Code")
                    .setColor(Color.decode("#569d3c"))
                    .setTimestamp(Instant.now());

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            return;
        }

        for (Map.Entry<UUID, String> entry : Verify.activeCodes.entrySet()) {

            UUID uuid = entry.getKey();
            String c = entry.getValue();
            Player player = Bukkit.getPlayer(uuid);


            if (c.equals(code)) {

                Group group = this.luckPerms.getGroupManager().getGroup("verified");

                if (group == null) {
                    HardSMP.getInstance().getLogger().warning("Group" + group.toString() + "not found!");
                    event.reply("Es ist ein Fehler aufgetreten, bitte wende dich an das Team!").setEphemeral(true).queue();
                    player.sendMessage(HardSMP.getPrefix().append(Component.text("Es ist ein Fehler aufgetreten, bitte wende dich an das Team!", NamedTextColor.RED)));
                    return;
                }

                this.luckPerms.getUserManager().modifyUser(uuid, (User user) -> {
                    user.data().clear(NodeType.INHERITANCE::matches);
                    Node node = InheritanceNode.builder(group).build();
                    user.data().add(node);
                });

                Verify.activeCodes.remove(uuid);

                VerifyData data = VerifyData.load(player.getUniqueId().toString());
                data.setVerified(true);
                data.setDiscordID(event.getMember().getIdLong());
                data.save();

                PlayerVerifyEvent vevent = new PlayerVerifyEvent(player, event.getMember(),
                        Component.text("Erfolgreich Verifiziert!", TextColor.color(0x88d657)),
                        HardSMP.getPrefix()
                                .append(Component.text("Du wurdest erfolgreich", TextColor.color(0x88d657)))
                                .append(Component.text(" Verifiziert", TextColor.color(0x55cfc4), TextDecoration.BOLD))
                                .append(Component.text("!", TextColor.color(0x88d657))));

                Bukkit.getPluginManager().callEvent(vevent);

                player.sendActionBar(vevent.getActionbarMessage());

                player.sendMessage(vevent.getMessage());
                break;
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ Erfolgreich Verifiziert")
                .setDescription("Du wurdest erfolgreich Verifiziert. Viel Spaß!")
                .setColor(Color.decode("#569d3c"))
                .setTimestamp(Instant.now());
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();

    }
}

