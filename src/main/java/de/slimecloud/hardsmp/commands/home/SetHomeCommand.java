package de.slimecloud.hardsmp.commands.home;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.commands.EmptyTabCompleter;
import de.slimecloud.hardsmp.database.DataClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.naming.Name;
import javax.xml.crypto.Data;

public class SetHomeCommand implements CommandExecutor, EmptyTabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommand kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(HardSMP.getPrefix().append(
                    Component.text("Bitte gib einen Namen für dein Home an!", NamedTextColor.RED)
            ));
            return true;
        }

        if (!player.hasPermission("hardsmp.command.home.multiple") && HomeData.load(player.getUniqueId()).toArray().length > 1) {
            player.sendMessage(HardSMP.getPrefix().append(
                    Component.text("Du hast die maximale Anzahl an Homes erreicht!")
            ));
            return true;
        }

        new HomeData()
            .newHome(player.getLocation(), player.getWorld(), args[0], player.getUniqueId())
            .save();

        player.sendMessage(HardSMP.getPrefix().append(
                Component.text("Dein Home ", TextColor.color(0x88d657))
                        .append(Component.text(args[0], TextColor.color(0xF6ED82)))
                        .append(Component.text(" wurde gesetzt!", TextColor.color(0x88d657)))
        ));

        return true;
    }
}
