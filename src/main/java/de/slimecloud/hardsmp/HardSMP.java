package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.commands.SpawnShopNPCCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import de.slimecloud.hardsmp.verify.Verify;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public final NamespacedKey TEAM_KEY = new NamespacedKey(this, "team");
    public final NamespacedKey SHOP_KEY = new NamespacedKey(this, "shop");

    @Getter
    private static HardSMP instance;

    @Getter
    private Database database;

    @Getter
    private ItemManager itemManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        instance = this;
        this.luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        saveDefaultConfig();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        this.itemManager = new ItemManager();

        registerCommand("spawn-shop-npc", new SpawnShopNPCCommand());

        registerEvent(new SlimeHandler());

        itemManager.registerItem("chest-key", () -> new ItemBuilder(Material.IRON_HOE).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(ChatColor.RESET + "Chest Key").build());

        SlimeHandler.setupOffers(getConfig());

        //Events
        registerEvent(new Verify(this, this.luckPerms));

        new DiscordBot();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TextComponent getPrefix() {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("HardSMP", TextColor.color(0x55cfc4)))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY));
    }

    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private PluginCommand registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command!=null) command.setExecutor(executor);
        return command;
    }

}
