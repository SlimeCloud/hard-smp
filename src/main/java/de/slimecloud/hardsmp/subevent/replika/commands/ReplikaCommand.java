package de.slimecloud.hardsmp.subevent.replika.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.subevent.SubEvent;
import de.slimecloud.hardsmp.subevent.replika.Replika;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
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
            case "start" -> {
                player.sendMessage(
                        HardSMP.getPrefix().append(
                                Component.text("Starte Replika...", plugin.getGreenColor())));

                if (event.getPlayers().isEmpty()) {
                    player.sendMessage(
                            HardSMP.getPrefix().append(
                                    Component.text("FEHLER! Du musst zuerst ein Setup des Events durchführen!", plugin.getGreenColor())));
                    return true;
                }

                event.start();

                player.sendMessage(
                        HardSMP.getPrefix().append(
                                Component.text("Replika Event gestartet!", plugin.getGreenColor())));
            }
            case "stop" -> {
                player.sendMessage(
                        HardSMP.getPrefix().append(
                                Component.text("Stoppe Replika...", plugin.getGreenColor())));

                event.stop();

                player.sendMessage(
                        HardSMP.getPrefix().append(
                                Component.text("Replika Event gestoppt!", plugin.getGreenColor())));
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                list.add("start");
                list.add("stop");
            }
        }
        return list;
    }
}
