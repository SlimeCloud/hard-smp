package de.slimecloud.hardsmp.commands.home;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.ui.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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

public class ListHomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player target = Bukkit.getPlayer(commandSender.getName());
        Player sender = target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null){
                sender.sendMessage(HardSMP.getPrefix().append(
                        Component.text("Spieler konnte nicht gefunden werden", NamedTextColor.RED)
                ));
                return true;
            }
        }

        Component homes = Component.text("Homes von ", TextColor.color(0x88d657))
                .append(Chat.getName(target))
                .append(Component.text(":", TextColor.color(0x88d657)))
                .appendNewline();

        for (HomeData home : HomeData.load(target.getUniqueId())) {
            homes = homes.append(Component.text("   - " + home.getHomeName())
                    .clickEvent(ClickEvent.suggestCommand("/home " + home.getHomeName()))
                    .appendNewline());
        }

        sender.sendMessage(homes);
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender.hasPermission("hardsmp.command.home.other") && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(p -> p.startsWith(args[0]))
                    .toList();
        }
        return Collections.emptyList();
    }
}
