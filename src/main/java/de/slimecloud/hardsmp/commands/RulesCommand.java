package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.event.PlayerVerifyEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class RulesCommand implements CommandExecutor, EmptyTabCompleter, Listener {
    private final Component message;

    public RulesCommand() {
        ConfigurationSection section = HardSMP.getInstance().getConfig().getConfigurationSection("rules");

        if (section == null) {
            this.message = Component
                    .text("Es ist ein fehler aufgetreten.\nBitte melde dieses Problem dem support")
                    .color(Formatter.getColorFormattings().get('4'));
        }

        else {

            Component msg = Formatter.parseText("§ä----- §öRegeln §ä-----");

            for (String key : section.getKeys(false)) {
                ConfigurationSection s = section.getConfigurationSection(key);
                msg = msg.appendNewline();
                if (s == null) msg = msg.append(Formatter.parseText("§ö" + key + "§7: §ä" + section.getString(key, "rule")));
                else {
                    msg = msg.append(Formatter.parseText("§ö" + key + "§7:§ä"));

                    for (String sKey : s.getKeys(false)) {
                        msg = msg.appendNewline()
                                .appendSpace()
                                .appendSpace()
                                .append(Formatter.parseText("§ö" + sKey + "§7: §ä" + s.getString(sKey)));
                    }
                }
            }

            this.message = msg;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(this.message);
        return true;
    }

    @EventHandler
    public void onVerify(PlayerVerifyEvent event) {
        event.getPlayer().sendMessage(this.message);
    }
}
