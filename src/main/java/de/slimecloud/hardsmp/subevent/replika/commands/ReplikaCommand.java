package de.slimecloud.hardsmp.subevent.replika.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.subevent.replika.Replika;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReplikaCommand implements CommandExecutor, TabCompleter {

    private final HardSMP plugin;

    public ReplikaCommand(HardSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cBefehl kann nur als Spieler ausgeführt werden!");
            return true;
        }

        Replika event = plugin.getSubEvents().getReplika();
        switch (args[0]) {
            case "finishLevel" -> {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Überprüfe Plot auf Richtigkeit...", HardSMP.getGreenColor())));

                if (event.checkLevel(player)) {
                    player.sendMessage(HardSMP.getPrefix().append(Component.text("Glückwunsch! Du hast das Nächste Level erreicht!", HardSMP.getGreenColor())));
                    return true;
                }

                player.sendMessage(HardSMP.getPrefix().append(Component.text("Fehlgeschlagen! Schau dir dein Bauwerk nochmal genau an!", NamedTextColor.RED)));
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                list.add("finishLevel");
            }
            case 2 -> {
                if (args[0].equals("join")) {
                    list = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(p -> p.startsWith(args[1]))
                            .toList();
                }
            }
        }
        return list;
    }
}
