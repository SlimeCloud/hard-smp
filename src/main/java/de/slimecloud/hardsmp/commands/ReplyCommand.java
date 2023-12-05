package de.slimecloud.hardsmp.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.ui.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

public class ReplyCommand implements CommandExecutor, EmptyTabCompleter {
    public final static Cache<UUID, UUID> reply = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(3))
            .build();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Kann nur als Spieler ausgeführt werden");
            return true;
        }

        var target = reply.getIfPresent(player.getUniqueId());

        if(target == null) {
            sender.sendMessage(HardSMP.getPrefix().append(Component.text("Du hast keine laufende Unterhaltung!")));
            return true;
        }

        var tPlayer = Bukkit.getOfflinePlayer(target).getPlayer();

        if(tPlayer == null) {
            sender.sendMessage(HardSMP.getPrefix().append(Component.text("Spieler ist nicht mehr online!")));
            return true;
        }

        Component message = Formatter.parseText("&", "&r&7&o " + String.join(" ", args))
                .clickEvent(ClickEvent.suggestCommand("/r "))
                .hoverEvent(HoverEvent.showText(Component.text("Antworten", TextColor.color(0xF6ED82))));;

        tPlayer.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("ui.chat.replyPrefix.receive"))
                .append(Chat.getName(player))
                .append(Component.text(": "))
                .append(message)
        );
        sender.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("ui.chat.replyPrefix.outgoing"))
                .append(Chat.getName(tPlayer))
                .append(Component.text(": "))
                .append(message)
        );

        ReplyCommand.reply.put(player.getUniqueId(), target);
        ReplyCommand.reply.put(target, player.getUniqueId());

        return true;
    }
}
