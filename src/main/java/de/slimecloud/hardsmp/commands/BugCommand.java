package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class BugCommand implements CommandExecutor, EmptyTabCompleter {

    private final Component msg;

    public BugCommand() {
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("bugform");
        if (section == null)
            this.msg = Component.text("Es ist ein fehler aufgetreten.\nBitte melde dieses Problem dem support").color(Formatter.getColorFormattings().get('4'));
        else {
            Component msg = Formatter.parseText("§a----- §bBugformular §a-----");
            ConfigurationSection s = section.getConfigurationSection("link");
            msg = msg.appendNewline();
            msg = msg.append(Formatter.parseText("§b" + section.getString("link", "bugform")));

            this.msg = msg;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        commandSender.sendMessage(this.msg);
        return true;
    }

}
