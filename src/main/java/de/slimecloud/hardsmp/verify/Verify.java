package de.slimecloud.hardsmp.verify;


import de.slimecloud.hardsmp.Main;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.TextColor.*;

public class Verify implements Listener {

    public static HashMap<UUID, String> activeCodes = new HashMap<>();

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
        if (user.getPrimaryGroup().equals("verified")) return;


        activeCodes.put(
                event.getPlayer().getUniqueId(),
                generateCode(Main.getInstance().getConfig().getInt("verify.code-length"))
        );

        sendInfoMessage(event);
    }

    @EventHandler()
    private void onLeave(PlayerQuitEvent event) {
        activeCodes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler()
    private void onMove(PlayerMoveEvent event) {
        User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(event.getPlayer());
        if (user.getPrimaryGroup().equals("verified")) return;

        event.setCancelled(true);
        sendInfoMessage(event);
    }


    private void sendInfoMessage(PlayerEvent event){

        String code = activeCodes.get(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(
                text("\n\n\n\n==========================\n", color(0x88d657))
                        .append(text("Du bist noch nicht", color(0x88d657))
                                .append(text(" verifiziert", color(0x88d657), TextDecoration.BOLD))
                                .append(text("!\n", color(0x88d657)))
                                .append(text("==========================\n\n", color(0x88d657)))
                                .append(text("Zum Verifizieren:\n\n", color(0x55cfc4), TextDecoration.BOLD, TextDecoration.UNDERLINED))
                                .append(text("1. ", color(0x55cfc4)))
                                .append(text("Joine dem ", color(0x88d657)))
                                .append(text("Discord", color(0x55cfc4), TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                        .hoverEvent(HoverEvent.showText(text("Klicke hier um zu Joinen", color(0x55cfc4))
                                                .clickEvent(ClickEvent.openUrl("https://discord.gg/slimecloud"))))
                                        .clickEvent(ClickEvent.openUrl("https://discord.gg/slimecloud")))
                                .append(text(".\n", color(0x88d657)))
                                .append(text("2. ", color(0x55cfc4)))
                                .append(text("Nutze auf dem Discord den Befehl ", color(0x88d657)))
                                .append(text("/verify\n", color(0x55cfc4))
                                        .hoverEvent(HoverEvent.showText(text("Gib diesen Befehl auf Discord ein.", color(0x55cfc4))))))
                        .append(text("3. ", color(0x55cfc4)))
                        .append(text("Gib als Code ", color(0x88d657)))
                        .append(text(code, color(0x55cfc4), TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.showText(text("Klicke hier um den Code zu kopieren", color(0x55cfc4))
                                        .clickEvent(ClickEvent.copyToClipboard(code)))))
                        .clickEvent(ClickEvent.copyToClipboard(code))
                        .append(text(" ein.\n", color(0x88d657)))
        );
    }

    private static String generateCode(int length) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (Math.random()>0.5) sb.append(Math.round(Math.random()*9));
            else {
                char c = alphabet[(int) (Math.round(Math.random()*alphabet.length)-1)];
                sb.append(String.valueOf(c).toUpperCase());
            }
        }
        return sb.toString();
    }

}
