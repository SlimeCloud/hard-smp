package de.slimecloud.hardsmp.commands.home;

import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RemoveHomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommand kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        if (args.length > 0) {
            HomeData.load(player.getUniqueId(), args[0]).ifPresentOrElse(
                    homeData -> remove(homeData, player),

                    () -> player.sendMessage(HardSMP.getPrefix().append(Component.text("Das Home ", NamedTextColor.RED)
                            .append(Component.text(args[0], NamedTextColor.RED, TextDecoration.BOLD)
                                    .append(Component.text(" konnte nicht gefunden werden!", NamedTextColor.RED)))))
            );
        } else {
            if (HomeData.load(player.getUniqueId()).toArray().length > 1) {
                player.sendMessage(HardSMP.getPrefix().append(
                        Component.text("Du hast mehr als ein Home! Bitte gebe den namen eines Homes an.", NamedTextColor.RED)
                ));
                return true;
            }
            remove(HomeData.loadOne(player.getUniqueId()), player);
        }

        return true;
    }

    private void remove(HomeData home, Player player) {
        home.delete();
        player.sendMessage(HardSMP.getPrefix().append(
                Component.text("Das Home ", TextColor.color(0x88d657))
                        .append(Component.text(home.getHomeName(), TextColor.color(0xF6ED82), TextDecoration.BOLD))
                        .append(Component.text(" wurde entfernt!", TextColor.color(0x88d657)))
        ));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return HomeData.load(Bukkit.getPlayer(commandSender.getName()).getUniqueId()).stream()
                    .map(HomeData::getHomeName)
                    .filter(p -> p.startsWith(args[0]))
                    .toList();
        }
        return Collections.emptyList();
    }
}
