package de.slimecloud.hardsmp.ui;

import de.cyklon.spigotutils.adventure.Formatter;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.player.PlayerController;
import de.slimecloud.hardsmp.ui.scoreboard.ScoreboardManager;
import de.slimecloud.hardsmp.verify.Verification;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Listener {

    private final String prefix;
    private final String format;
    private final Map<Integer, String> nameColor;

    public Chat(FileConfiguration config) {
        this.prefix = config.getString("ui.chat.prefix", "[%prefix]");
        this.format = config.getString("ui.chat.format", "%rank %prefix %name:");
        this.nameColor = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("ui.chat.color");

        if (section != null) for (String key : section.getKeys(false)) {
            String line = section.getString(key);
            if (line != null) {
                if (key.equals("name_default")) nameColor.put(-1, line);
                else if (key.startsWith("name_")) nameColor.put(Integer.parseInt(key.split("_")[1]), line);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!player.hasPermission("hardsmp.verify.bypass")) {
            if (PunishmentManager.get().isMuted(UUIDManager.get().getUUID(player.getName()))) return;
            else if (!Verification.load(player.getUniqueId().toString()).isVerified()) {
                player.sendMessage(HardSMP.getPrefix().append(Component.text("Bitte verifiziere dich bevor du schreiben kannst!", NamedTextColor.RED))
                        .appendNewline().appendNewline()
                        .append(HardSMP.getPrefix())
                        .append(Component.text("Bei Problemen, öffne bitte ein Ticket im Discord", TextColor.color(0x88D657))));
                return;
            }
        }

        Component message = formatName(event.getPlayer())
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(Formatter.parseText("&", "&r" + LegacyComponentSerializer.legacySection().serialize(event.originalMessage())));

        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    public Component formatName(Player sender) {
        User user = HardSMP.getInstance().getLuckPerms().getUserManager().getUser(sender.getUniqueId());
        String prefix;
        if (user == null) prefix = null;
        else prefix = user.getCachedData().getMetaData().getPrefix();

        prefix = prefix == null ? "" : this.prefix.replace("%prefix", prefix.replace("&", "§").replace("#88D657", "ä").replace("#F6ED82", "ö"));
        String rank = ScoreboardManager.STATS.get(sender.getUniqueId()).first().toString();
        String nameColor = this.nameColor.getOrDefault(Integer.valueOf(rank), this.nameColor.get(-1));

        rank = switch (rank) {
            case "1" -> "§r\uE002";
            case "2" -> "§r\uE003";
            case "3" -> "§r\uE004";
            default -> "§7#" + rank;
        };

        if(sender.getPlayer().hasPermission("hardsmp.chat.highlight")) {
            rank = "";
            nameColor = HardSMP.getInstance().getConfig().getString("ui.chat.color.team");
        } else rank += " ";

        String formattedFormat = this.format
                .replace("%coln", nameColor)
                .replace("%rank", rank)
                .replace("%prefix", prefix)
                .replace("%name", sender.getName());

        return Component.empty().append(Formatter.parseText(formattedFormat)
                .clickEvent(ClickEvent.suggestCommand("/msg " + sender.getName()))
                .hoverEvent(HoverEvent.showText(
                        Component.text("Punkte: ", TextColor.color(0xF6ED82))
                                .append(Component.text((int) PlayerController.getPlayer((OfflinePlayer) sender).getActualPoints(), TextColor.color(0x88D657))
                )))).append(Component.empty()).clickEvent(null).hoverEvent(null);
    }

    public static Component getName(Player player) {
        return HardSMP.getInstance().getChat().formatName(player);
    }
}
