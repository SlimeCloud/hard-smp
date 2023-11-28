package de.slimecloud.hardsmp.listener;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.EventPlayer;
import de.slimecloud.hardsmp.player.PlayerController;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.util.UUID;

public class PunishmentListener implements Listener {

    private final HardSMP plugin;
    private final long channelID;

    public PunishmentListener(HardSMP plugin) {
        this.plugin = plugin;
        this.channelID = plugin.getConfig().getLong("discord.mod-log-channel");
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        handler(event.getPunishment(), false);
    }

    @EventHandler
    public void onRevokePunishment(RevokePunishmentEvent event) {
        handler(event.getPunishment(), true);
    }

    private void handler(Punishment punishment, Boolean isRevoke) {
        System.out.println("handler");

        PunishmentType type = punishment.getType();
        String executor = punishment.getOperator();
        int id = punishment.getId();
        String name = punishment.getName();
        String reason = punishment.getReason();
        String duration = punishment.getDuration(true);
        EventPlayer player = PlayerController.getPlayer(Bukkit.getOfflinePlayer(name));
        User discordUser = player.getDiscord();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.decode("#569d3c"))
                .setTitle(((isRevoke) ? "Un" : "") + type.getName())
                .setThumbnail("https://crafatar.com/renders/head/" + player.getUniqueId() + "?default=MHF_Steve&overlay")
                .addField("Teammitglied:", executor, true)
                .addField("ID:", String.valueOf(id), true)
                .addField("UserName:", name, true)
                .addField("UserUUID:", player.getUniqueId().toString(), true)
                .addField("Discord:", discordUser == null ? "nicht verifiziert" : discordUser.getAsMention() + "(" + discordUser.getEffectiveName() + ")", true)
                .addField("Grund:", reason, true);

        if (!isRevoke) embedBuilder.addField("Dauer:", duration, true);

        plugin.getDiscordBot().jdaInstance.getTextChannelById(channelID).sendMessageEmbeds(embedBuilder.build()).queue();


    }
}
