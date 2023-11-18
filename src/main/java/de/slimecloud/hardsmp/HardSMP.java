package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.item.ItemBuilder;
import de.cyklon.spigotutils.ui.scoreboard.ScoreboardUI;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.commands.*;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ChestKey;
import de.slimecloud.hardsmp.item.CustomItem;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.player.data.PointsListener;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import de.slimecloud.hardsmp.ui.Chat;
import de.slimecloud.hardsmp.ui.Tablist;
import de.slimecloud.hardsmp.ui.scoreboard.ScoreboardManager;
import de.slimecloud.hardsmp.verify.MinecraftVerificationListener;
import lombok.Getter;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardSMP extends JavaPlugin {

    public final NamespacedKey SHOP_KEY = new NamespacedKey(this, "shop");

    @Getter
    private static HardSMP instance;

    @Getter
    private Database database;

    @Getter
    private ItemManager itemManager;

    @Getter
    private LuckPerms luckPerms;

    @Getter
    private Spark spark;

    @Getter
    private DiscordBot discordBot;

    @Override
    public void onEnable() {
        instance = this;
        this.luckPerms = getServer().getServicesManager().load(LuckPerms.class);
        this.spark = SparkProvider.get();

        saveDefaultConfig();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        this.database = new Database(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        this.itemManager = new ItemManager();

        RulesCommand rules;

        registerCommand("spawn-shop-npc", new SpawnShopNPCCommand());
        registerCommand("point", new PointCommand());
        registerCommand("formatting", new FormattingCommand());
        registerCommand("verify", new VerifyCommand());
        registerCommand("unverify", new UnverifyCommand());
        registerCommand("help", new HelpCommand());
        registerCommand("rules", rules = new RulesCommand());
        registerCommand("teamchat", new TeamChatCommand());

        //Events
        registerEvent(new MinecraftVerificationListener());
        registerEvent(new SlimeHandler());
        registerEvent(new PointsListener());
        registerEvent(new ChestKey(this));
        registerEvent(rules);

        //UI
        registerEvent(new ScoreboardManager(this));
        registerEvent(new Tablist(this));
        registerEvent(new Chat(getConfig()));


        CustomItem.getItems().forEach(i -> itemManager.registerItem(i.getName(), i::getItem));
        itemManager.registerItem("mending-Infinity-bow", () -> new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_INFINITE, 1).addEnchantment(Enchantment.MENDING, 1).build());

        SlimeHandler.setupOffers(getConfig());

        ConfigurationSection formattings = getConfig().getConfigurationSection("ui.custom-formatting");
        for (String format : formattings.getKeys(false)) {
            Formatter.registerCustomFormatting(format.charAt(0), TextColor.fromHexString(formattings.getString(format)));
        }

        AdvancementHandler.register(this, this::registerEvent);

        try {
            this.discordBot = new DiscordBot();
        } catch (Exception e) {
            getLogger().warning("Failed to init Discord bot: %s".formatted(e));
        }
    }

    @Override
    public void onDisable() {
        ScoreboardUI.getScoreboards().forEach(ScoreboardUI::delete);

        this.discordBot.shutdown();
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
        if (command != null) command.setExecutor(executor);
        return command;
    }

}
