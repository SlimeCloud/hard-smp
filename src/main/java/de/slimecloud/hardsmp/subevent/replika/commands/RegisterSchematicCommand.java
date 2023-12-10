package de.slimecloud.hardsmp.subevent.replika.commands;

import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.build.Build;
import de.slimecloud.hardsmp.subevent.replika.Replika;
import de.slimecloud.hardsmp.util.ListLookingCords;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterSchematicCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Replika replika = HardSMP.getInstance().getSubEvents().getReplika();
        if (sender instanceof Player player && args.length >= 7) {
            Vector playerVec = player.getLocation().toVector();
            String name = args[0];
            File file = replika.getFile(name);

            if (file.exists()) {
                sender.sendMessage(HardSMP.getPrefix().append(Component.text("Schematic mit dem name '" + name + "' existiert bereits.", NamedTextColor.RED)));
                return true;
            }

            Vector cord1 = parseVector(args[1].replace("~", Double.toString(playerVec.getX())), args[2].replace("~", Double.toString(playerVec.getY())), args[3].replace("~", Double.toString(playerVec.getZ())));
            Vector cord2 = parseVector(args[4].replace("~", Double.toString(playerVec.getX())), args[5].replace("~", Double.toString(playerVec.getY())), args[6].replace("~", Double.toString(playerVec.getZ())));

            try {
                Build build = Build.scan(cord1.toLocation(player.getWorld()), cord2.toLocation(player.getWorld()), Boolean.parseBoolean(args[7]), Boolean.parseBoolean(args[8]));

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(build.getBytes());
                }
                replika.registerSchematic(name, build);
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(HardSMP.getPrefix().append(Component.text("Es ist ein fehler aufgetreten:\n" + e.getMessage(), NamedTextColor.RED)));
                return true;
            }
            sender.sendMessage(HardSMP.getPrefix().append(Component.text("Schematic '" + name + "' erfolgreich registriert", HardSMP.getInstance().getGreenColor())));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0) return list;
        if (sender instanceof Player player) {
            switch (args.length) {
                case 2, 5 -> ListLookingCords.listAutomaticLoockingCoords(list, player, "x", true);
                case 3, 6 -> ListLookingCords.listAutomaticLoockingCoords(list, player, "y", true);
                case 4, 7 -> ListLookingCords.listAutomaticLoockingCoords(list, player, "z", true);
                case 8, 9 -> {
                    list.add("true");
                    list.add("false");
                }
            }
        }
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return list;
    }

    private static Vector parseVector(String s1, String s2, String s3) {
        return new Vector(Double.parseDouble(s1), Double.parseDouble(s2), Double.parseDouble(s3));
    }

}
