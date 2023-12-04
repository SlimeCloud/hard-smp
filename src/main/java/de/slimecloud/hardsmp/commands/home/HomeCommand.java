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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommand kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        if (args.length > 0) {
            HomeData.load(player.getUniqueId(), args[0]).ifPresentOrElse(
                    homeData -> teleport(homeData, player),

                    () -> player.sendMessage(HardSMP.getPrefix().append(Component.text("Das Home \"", NamedTextColor.RED)
                            .append(Component.text(args[0], NamedTextColor.RED)
                                    .append(Component.text("\" konnte nicht gefunden werden!", NamedTextColor.RED)))))
            );
        } else return false;

        return true;
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

    private void teleport(HomeData home, Player player) {
        player.sendMessage(HardSMP.getPrefix().append(
                Component.text("Du wirst in 3sek zum Home ", TextColor.color(0x88d657))
                        .append(Component.text(home.getHomeName(), TextColor.color(0xF6ED82), TextDecoration.BOLD))
                        .append(Component.text(" teleportiert!"))
        ));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(home.getLocation());
            }
        }.runTaskLater(HardSMP.getInstance(), 60);
    }

}
