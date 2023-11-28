package de.slimecloud.hardsmp.verify;

import de.slimecloud.hardsmp.HardSMP;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinVerifyListener implements Listener {

    private final HardSMP plugin;
    private final Boolean isPreVerify;

    public OnJoinVerifyListener(HardSMP plugin) {
        this.plugin = plugin;
        this.isPreVerify = plugin.getConfig().getBoolean("verify.pre-verify", false);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Verification verification = Verification.load(player.getUniqueId().toString());
        if (isPreVerify && verification.isVerified() && !player.hasPermission("hardsmp.verify.bypass")) Punishment.create(player.getName(), UUIDManager.get().getUUID(player.getName()), "@VerifyJoinKick", "AutoVerify", PunishmentType.KICK, 0L, null, false);
    }
}
