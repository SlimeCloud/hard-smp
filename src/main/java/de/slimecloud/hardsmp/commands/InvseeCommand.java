package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.persistence.PersistentDataHandler;
import de.cyklon.spigotutils.serial.InventorySerializer;
import de.slimecloud.hardsmp.HardSMP;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InvseeCommand implements CommandExecutor, TabCompleter {

    private final NamespacedKey key = new NamespacedKey(HardSMP.getInstance(), "current-inv");
    private final File directory = new File(HardSMP.getInstance().getDataFolder(), "cache");
    private Map<String, String> configMessages = new HashMap<>();

    public InvseeCommand() {
        directory.mkdirs();

        configMessages.put("notInInvsee", HardSMP.getInstance().getConfig().getString("invsee.notInInvsee", "§cDu bist nicht in einem Invsee!"));
        configMessages.put("couldntNotRestoreInv", HardSMP.getInstance().getConfig().getString("invsee.couldntNotRestoreInv", "§cDein Inventar konnte nicht wieder hergestellt werden!"));
        configMessages.put("couldntNotRestoreInvWithError", HardSMP.getInstance().getConfig().getString("invsee.couldntNotRestoreInvWithError", "§cDein Inventar konnte nicht wieder hergestellt werden!\nError: %error"));
        configMessages.put("leavedInvsee", HardSMP.getInstance().getConfig().getString("invsee.leavedInvsee", "§2Invsee wurde verlassen!"));
        configMessages.put("exitInvseeBeforeNew", HardSMP.getInstance().getConfig().getString("invsee.exitInvseeBeforeNew", "§cDu bist bereits in einem Invsee! Benutze /invsee exit zum schließen!"));
        configMessages.put("invSaveError", HardSMP.getInstance().getConfig().getString("invsee.invSaveError", "§cEtwas mit der Inventory Speicherung hat nicht geklappt! Bitte reporte diesen Fehler!"));
        configMessages.put("invseeHintMessage", HardSMP.getInstance().getConfig().getString("invsee.invseeHintMessage", "§2Benutze /invsee exit, um das Inventar zu aktualisieren und um dein Inventar zurück zu bekommen."));
        configMessages.put("playerNotFoundErrorMessage", HardSMP.getInstance().getConfig().getString("invsee.playerNotFoundErrorMessage", "§cSpieler wurde nicht gefunden!"));
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length > 0) {
                File inventoryLocation = new File(directory, "%s.inv".formatted(player.getUniqueId()));

                switch (args[0].toLowerCase()) {
                    case "exit" -> {
                        String uuid = getKeyData(player);
                        if (uuid == null) {
                            player.sendMessage(Formatter.parseText(configMessages.get("notInInvsee")));
                            return true;
                        }
                        removeKeyData(player);
                        Player targetPlayer = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (targetPlayer == null) player.sendMessage(Formatter.parseText(HardSMP.getInstance().getConfig().getString("invsee.couldntUpdatePlayer", "§cDer InvseePlayer konnte nicht aktualisiert werden!")));
                        else targetPlayer.getInventory().setContents(player.getInventory().getContents());
                        if (inventoryLocation.exists()) {
                            try { InventorySerializer.loadInv(inventoryLocation, player.getInventory()); }
                            catch (IOException | ClassNotFoundException e) { player.sendMessage(Formatter.parseText(configMessages.get("couldntNotRestoreInvWithError").replace("%error", e.getMessage()))); }
                        } else player.sendMessage(Formatter.parseText(configMessages.get("couldntNotRestoreInv")));
                        player.sendMessage(Formatter.parseText(configMessages.get("leavedInvsee")));
                    }
                    case "from" -> {
                        if (args.length >= 2) {

                            String uuid = getKeyData(player);
                            if (uuid != null) {
                                player.sendMessage(Formatter.parseText(configMessages.get("exitInvseeBeforeNew")));
                                return true;
                            }

                            Player targetPlayer = Bukkit.getPlayer(args[1]);
                            if (targetPlayer != null) {

                                try { InventorySerializer.saveInv(inventoryLocation, player.getInventory()); }
                                catch (IOException e) {
                                    player.sendMessage(Formatter.parseText(configMessages.get("invSaveError")));
                                    throw new RuntimeException(e);
                                }
                                player.sendMessage(Formatter.parseText(configMessages.get("invseeHintMessage")));
                                setKeyData(player, targetPlayer);

                                player.getInventory().setContents(targetPlayer.getInventory().getContents());


                            } else sender.sendMessage(Formatter.parseText(configMessages.get("playerNotFoundErrorMessage")));

                        } else return false;
                    }
                    default -> {
                        return false;
                    }

                }

            } else return false;

        } else sender.sendMessage("Der Command kann nur als Spieler ausgeführt werden!");

        return true;
    }

    private String getKeyData(Player player) {
        PersistentDataHandler playerPersistentData = PersistentDataHandler.get(player);
        return playerPersistentData.getString(key);
    }
    private void setKeyData(Player player, Player targetPlayer) {
        PersistentDataHandler.get(player).set(key, targetPlayer.getUniqueId().toString());
    }
    private void removeKeyData(Player player) {
        PersistentDataHandler playerPersistentData = PersistentDataHandler.get(player);
        playerPersistentData.remove(key);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                list.add("from");
                list.add("exit");
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("from")) {
                    list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                }
            }
        }
        list.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return list;
    }
}
