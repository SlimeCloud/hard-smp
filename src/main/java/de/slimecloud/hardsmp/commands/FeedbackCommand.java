package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class FeedbackCommand implements CommandExecutor, EmptyTabCompleter {

    private final Component msg;

    public FeedbackCommand() {
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("feedback");
        if (section == null)
            this.msg = Component.text("Es ist ein fehler aufgetreten.\nBitte melde dieses Problem dem support").color(Formatter.getColorFormattings().get('4'));
        else {
            Component msg = Formatter.parseText("§a----- §bFeedback §a-----");
            msg = msg.appendNewline();
            msg = msg.append(Formatter.parseText("§b" + section.getString("link", "feedback")));

            this.msg = msg;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        commandSender.sendMessage(this.msg);
        return true;
    }

}
