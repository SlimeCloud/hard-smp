package de.slimecloud.hardsmp.verify;


import de.slimecloud.hardsmp.HardSMP;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.TextColor.*;

public class Verify implements Listener {

    public static HashMap<UUID, String> activeCodes = new HashMap<>();
    private final HardSMP plugin;
    private final LuckPerms luckPerms;

    public Verify(HardSMP plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        User user = this.luckPerms.getPlayerAdapter(Player.class).getUser(event.getPlayer());
        if (!(user.getPrimaryGroup().equals("default"))) return;

        String code = generateCode(HardSMP.getInstance().getConfig().getInt("verify.code-length"));

        activeCodes.put(
                event.getPlayer().getUniqueId(),
                code
        );
        new VerifyData(event.getPlayer().getUniqueId().toString())
                .save();

        sendInfoMessage(event);
        sendCodeActionBar(event);
    }

    @EventHandler()
    private void onLeave(PlayerQuitEvent event) {
        activeCodes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler()
    private void onMove(PlayerMoveEvent event) {
        User user = this.luckPerms.getPlayerAdapter(Player.class).getUser(event.getPlayer());
        if (!(user.getPrimaryGroup().equals("default"))) return;

        event.setCancelled(true);

        sendInfoMessage(event);
        sendCodeActionBar(event);
    }

    private void sendCodeActionBar(PlayerEvent event) {
        String code = activeCodes.get(event.getPlayer().getUniqueId());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int c = 0;
            @Override
            public void run() {
                event.getPlayer().sendActionBar(
                        text("Dein Verifikations-Code: ", color(0x88d657))
                                .append(text(code, color(0x55cfc4), TextDecoration.BOLD)
                                        .clickEvent(ClickEvent.copyToClipboard(code)))
                );

                if (c >= 20) timer.cancel();
                c ++;
            }
        }, 0, 500);
    }

    private void sendInfoMessage(PlayerEvent event){

        String code = activeCodes.get(event.getPlayer().getUniqueId());

        if (code == null) return;

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
                char c;
                try {
                    c = alphabet[(int) (Math.round(Math.random()*alphabet.length)-1)];
                } catch (ArrayIndexOutOfBoundsException e) {
                    c = alphabet[0];
                }

                sb.append(String.valueOf(c).toUpperCase());
            }
        }
        Bukkit.getLogger().log(Level.INFO, "new code generated: " + sb);
        return sb.toString();
    }

}
