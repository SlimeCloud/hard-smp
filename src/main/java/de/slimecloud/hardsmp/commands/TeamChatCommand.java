package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TeamChatCommand implements CommandExecutor, EmptyTabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!commandSender.hasPermission(command.getPermission()))
            commandSender.sendMessage(command.permissionMessage());

        for (Player player : HardSMP.getInstance().getServer().getOnlinePlayers()) {
            if (!player.hasPermission("hardsmp.teamchat.read")) continue;

            String format = HardSMP.getInstance().getConfig().getString("teamchat.messageFormat");
            String messageColor = HardSMP.getInstance().getConfig().getString("teamchat.messageColor");

            Component messageFormat = Formatter.parseText(format.replace("%player", commandSender.getName()));

            StringBuilder message = new StringBuilder();
            Arrays.stream(args).toList().forEach(arg -> message.append(" ").append(arg));

            player.sendMessage(messageFormat.append(Formatter.parseText(messageColor + message)));

        }

        return true;
    }
}
