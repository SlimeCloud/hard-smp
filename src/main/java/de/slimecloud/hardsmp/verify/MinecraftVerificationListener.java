package de.slimecloud.hardsmp.verify;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.slimecloud.hardsmp.HardSMP;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public class MinecraftVerificationListener implements Listener {
    public static Cache<UUID, String> activeCodes = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Verification verification = Verification.load(event.getPlayer().getUniqueId().toString());
        if (verification.isVerified()) return;

        Group group = HardSMP.getInstance().getLuckPerms().getGroupManager().getGroup("verified");

        if (group != null) HardSMP.getInstance().getLuckPerms().getUserManager().modifyUser(
                event.getPlayer().getUniqueId(),
                (User user) -> user.data().clear(NodeType.INHERITANCE::matches)
        );

        String code;

        do {
            code = generateCode(HardSMP.getInstance().getConfig().getInt("verify.code-length"));
        } while (MinecraftVerificationListener.activeCodes.asMap().containsValue(code));

        activeCodes.put(
                event.getPlayer().getUniqueId(),
                code
        );

        new Verification(event.getPlayer().getUniqueId().toString()).save();

        sendInfoMessage(event);
        sendCodeActionBar(event.getPlayer());
    }

    @EventHandler()
    private void onLeave(PlayerQuitEvent event) {
        activeCodes.invalidate(event.getPlayer().getUniqueId());
    }

    @EventHandler()
    private void onMove(PlayerMoveEvent event) {
        User user = HardSMP.getInstance().getLuckPerms().getPlayerAdapter(Player.class).getUser(event.getPlayer());
        if (!user.getPrimaryGroup().equals("default")) return;

        event.setCancelled(true);

        sendInfoMessage(event);
        sendCodeActionBar(event.getPlayer());
    }

    private void sendCodeActionBar(Player player) {
        String code = activeCodes.getIfPresent(player.getUniqueId());
        if (code == null) return;

        Bukkit.getAsyncScheduler().runAtFixedRate(
                HardSMP.getInstance(),
                new Consumer<>() {
                    private int c = 0;

                    @Override
                    public void accept(ScheduledTask task) {
                        player.sendActionBar(
                                text("Dein Verifikations-Code: ", color(0x88d657))
                                        .append(text(code, color(0xF6ED82), TextDecoration.BOLD)
                                                .clickEvent(ClickEvent.copyToClipboard(code)))
                        );

                        if (c++ >= 20) task.cancel();
                    }
                },
                0,
                500,
                TimeUnit.MILLISECONDS
        );
    }

    private void sendInfoMessage(PlayerEvent event) {
        String code = activeCodes.getIfPresent(event.getPlayer().getUniqueId());
        if (code == null) return;

        event.getPlayer().sendMessage(
                text("\n\n\n\n==========================\n", color(0x88d657))
                        .append(text("Du bist noch nicht", color(0x88d657))
                                .append(text(" verifiziert", color(0x88d657), TextDecoration.BOLD))
                                .append(text("!\n", color(0x88d657)))
                                .append(text("==========================\n\n", color(0x88d657)))
                                .append(text("Zum Verifizieren:\n\n", color(0xF6ED82), TextDecoration.BOLD, TextDecoration.UNDERLINED))
                                .append(text("1. ", color(0xF6ED82)))
                                .append(text("Joine dem ", color(0x88d657)))
                                .append(text("Discord", color(0xF6ED82), TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                        .hoverEvent(HoverEvent.showText(text("Klicke hier um zu Joinen", color(0xF6ED82))
                                                .clickEvent(ClickEvent.openUrl("https://discord.gg/slimecloud"))))
                                        .clickEvent(ClickEvent.openUrl("https://discord.gg/slimecloud")))
                                .append(text(".\n", color(0x88d657)))
                                .append(text("2. ", color(0xF6ED82)))
                                .append(text("Nutze auf dem Discord den Befehl ", color(0x88d657)))
                                .append(text("/verify\n", color(0xF6ED82))
                                        .hoverEvent(HoverEvent.showText(text("Gib diesen Befehl auf Discord ein.", color(0xF6ED82))))))
                        .append(text("3. ", color(0xF6ED82)))
                        .append(text("Gib als Code ", color(0x88d657)))
                        .append(text(code, color(0xF6ED82), TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.showText(text("Klicke hier um den Code zu kopieren", color(0xF6ED82))
                                        .clickEvent(ClickEvent.copyToClipboard(code)))))
                        .clickEvent(ClickEvent.copyToClipboard(code))
                        .append(text(" ein.\n", color(0x88d657)))
        );
    }

    private final static Random random = new Random();
    private final static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String generateCode(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append(characters.charAt(random.nextInt(characters.length())));

        Bukkit.getLogger().log(Level.INFO, "new code generated: " + builder);
        return builder.toString();
    }
}
