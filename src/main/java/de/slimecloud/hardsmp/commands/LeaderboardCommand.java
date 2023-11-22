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
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaderboardCommand implements CommandExecutor, EmptyTabCompleter {

    int playersPerPage;

    public LeaderboardCommand() {
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("leaderboard");
        if (section != null) {
            playersPerPage = section.getInt("playersPerPage");
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        int page;
        int maxPlayers = Bukkit.getOfflinePlayers().length;
        int maxPages = maxPlayers % playersPerPage == 0 ? maxPlayers / playersPerPage : maxPlayers / playersPerPage + 1;

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

        BoardStats stats = new BoardStats();

        List<Map.Entry<UUID, Integer>> contents = stats.getTopPlayers(page != maxPages ? page * playersPerPage : maxPlayers).entrySet().stream()
                .skip((long) (page - 1) * playersPerPage)
                .toList();

        Component msg = Formatter.parseText("§a--- §bRangliste Seite " + page + " von " + maxPages + " §a---");
        for (Map.Entry<UUID, Integer> c : contents) {
            msg = msg.appendNewline();
            String color = switch (stats.get(c.getKey()).first()) {
                case 1 -> "§6";
                case 2 -> "§i";
                case 3 -> "§y";
                default -> "§7";
            };
            msg = msg.append(Formatter.parseText(color + stats.get(c.getKey()).first()
                    + ". " + color + Bukkit.getOfflinePlayer(c.getKey()).getName()
                    + "§8 - §a" + c.getValue()));
        }

        commandSender.sendMessage(msg);

        return true;
    }

}