package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class HelpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Component msg = null;
        if (args.length != 0) {
            String cmd = args[0];
            if (!cmd.isBlank()) switch (cmd.toLowerCase()) {
                case "claim" ->
                        msg = Formatter.parseText(HardSMP.getInstance().getConfig().getString("help.claim", "Claim info..."));
                case "event" ->
                        msg = Formatter.parseText(HardSMP.getInstance().getConfig().getString("help.event-info", "Event info..."));
                case "support" ->
                        msg = Formatter.parseText(HardSMP.getInstance().getConfig().getString("help.support-info", "Support info..."));
                case "command" -> {
                    ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("help.command-info");
                    if (section == null || section.getKeys(false).isEmpty()) {
                        msg = Component.text("Command info...");
                        break;
                    }
                    msg = Formatter.parseText("§ä----- §öCommands §ä-----");
                    for (String key : section.getKeys(false)) {
                        msg = msg.appendNewline()
                                .append(Component.text(key)
                                        .decorate(TextDecoration.BOLD)
                                        .clickEvent(ClickEvent.suggestCommand("/" + key))
                                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum ausführen.").color(TextColor.color(0xF6ED82))))
                                        .color(TextColor.color(0xF6ED82)))
                                .append(Component.text(":", TextColor.color(NamedTextColor.GRAY)))
                                .appendSpace()
                                .append(Formatter.parseText("§ä" + section.getString(key, "...")));
                    }
                    msg= msg.appendNewline().append(Formatter.parseText("§ä----- §öCommands §ä-----"));
                }
            }
        }
        if (msg == null)
            sender.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("help.root", "Help...")));
        else sender.sendMessage(msg);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Stream.of("event", "support", "command", "claim")
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
