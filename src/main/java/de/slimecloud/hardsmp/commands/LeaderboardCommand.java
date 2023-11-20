package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.ui.scoreboard.BoardStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int page;
        int playersPerPage = 10;
        int maxPlayers = Bukkit.getOnlinePlayers().size();
        int maxPages = maxPlayers % 10 == 0 ? maxPlayers / 10 : maxPlayers / 10 + 1;

        if (args.length == 0) {
            page = 1;
        } else if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /leaderboard [seite]!", NamedTextColor.RED)));
                return true;
            }
            if(page > maxPages) {
                commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Es gibt nur " + maxPages + " Seiten!", NamedTextColor.RED)));
                return true;
            }
        } else {
            commandSender.sendMessage(HardSMP.getPrefix().append(Component.text("Benutzung: /leaderboard [seite]!", NamedTextColor.RED)));
            return true;
        }

        int iterator = 0;
        BoardStats stats = new BoardStats();

        Map<UUID, Integer> contents = stats.getTopPlayers(page != maxPages ? page * playersPerPage : maxPlayers);
        for (Map.Entry<UUID, Integer> c : contents.entrySet()) {
            if (iterator < (page-1) * playersPerPage) {
                contents.remove(c.getKey());
            } else {
                break;
            }
            iterator++;
        }

        Component msg = Formatter.parseText("§a--- §bRangliste <Seite " + page + "von" + maxPages + "§a---");
        for (Map.Entry<UUID, Integer> c : contents.entrySet()) {
            msg = msg.appendNewline();
            String color = switch (stats.get(c.getKey()).first()) {
                case 1 -> "§6";
                case 2 -> "§i";
                case 3 -> "§y";
                default -> "§7";
            };
            msg = msg.append(Formatter.parseText(color + stats.get(c.getKey()).first()
                    + ". " + color + Bukkit.getPlayer(c.getKey()).getName()
                    + "§8 - §a" + c.getValue()));
        }

        commandSender.sendMessage(msg);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        list.add("<Seite>");
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return list;
    }
}