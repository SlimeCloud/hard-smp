package de.slimecloud.hardsmp.commands;

import de.cyklon.spigotutils.serial.InventorySerializer;
import de.cyklon.spigotutils.ui.Gui;
import de.slimecloud.hardsmp.HardSMP;
import de.slimecloud.hardsmp.item.ChestKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KeyChainCommand implements CommandExecutor, EmptyTabCompleter, Listener {

    private final Set<UUID> open = new HashSet<>();
    private final File directory;

    public KeyChainCommand(Plugin plugin) {
        this.directory = new File(plugin.getDataFolder(), "keychain");
        directory.mkdirs();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            player.openInventory(getInventory(player));
            open.add(player.getUniqueId());
        } else commandSender.sendMessage("Command kann nur von spielern auseführt werden");
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (open.remove(event.getPlayer().getUniqueId())) saveInventory(event.getPlayer(), event.getInventory());
    }

    @EventHandler
    public void onInventoryDrag(InventoryClickEvent event) {
        if (open.contains(event.getWhoClicked().getUniqueId())) {
            ChestKey chestKey = HardSMP.getInstance().getChestKey();
            if (!(chestKey.isItem(event.getCurrentItem()) || chestKey.isItem(event.getCursor()))) event.setCancelled(true);
        }
    }

    private File getFile(UUID uuid) {
        return new File(directory, uuid + ".inv");
    }

    private Inventory getInventory(Player player) {
        File file = getFile(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(player, 9, Component.text("Schlüsselbund"));
        if (file.exists()) {
            try {
                return InventorySerializer.loadInv(file, inv);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return inv;
    }

    private void saveInventory(HumanEntity player, Inventory inventory) {
        try {
            InventorySerializer.saveInv(getFile(player.getUniqueId()), inventory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
