package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MsgCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length <= 1) return false;
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Kann nur als Spieler ausgeführt werden");
            return true;
        }

        Bukkit.getOfflinePlayer(args[0]).getPlayer().sendMessage(
                Formatter.parseText(HardSMP.getInstance().getConfig().getString("ui.chat.msgPrefix"))
                        .append(HardSMP.getInstance().getChat().formatName(player))
                        .append(Formatter.parseText("&", "&r&7&o " + String.join(" ", Arrays.copyOfRange(args, 1, args.length))))
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }

        return Collections.emptyList();
    }
}
