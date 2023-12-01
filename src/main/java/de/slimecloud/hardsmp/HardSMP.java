package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.adventure.Formatter;
import de.cyklon.spigotutils.item.ItemBuilder;
import de.cyklon.spigotutils.ui.scoreboard.ScoreboardUI;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.claim.ClaimCommand;
import de.slimecloud.hardsmp.claim.ClaimInfo;
import de.slimecloud.hardsmp.claim.ClaimProtectionHandler;
import de.slimecloud.hardsmp.commands.*;
import de.slimecloud.hardsmp.commands.info.MinecraftInfoCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ChestKey;
import de.slimecloud.hardsmp.item.CustomItem;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.item.LockPick;
import de.slimecloud.hardsmp.listener.DeathPointHandler;
import de.slimecloud.hardsmp.listener.PunishmentListener;
import de.slimecloud.hardsmp.player.data.PointsListener;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import de.slimecloud.hardsmp.ui.Chat;
import de.slimecloud.hardsmp.ui.Placeholders;
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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Listener;
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

    @Getter
    private ChestKey chestKey;

    @Getter
    private LockPick lockPick;

    @Override
    public void onEnable() {
        new Placeholders().register();

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

        ConfigurationSection formattings = getConfig().getConfigurationSection("ui.custom-formatting");
        for (String format : formattings.getKeys(false)) {
            Formatter.registerCustomFormatting(format.charAt(0), TextColor.fromHexString(formattings.getString(format)));
            getLogger().info("registered \"" + format + "\" as color code for " + formattings.getString(format));
        }

        ClaimCommand claim = new ClaimCommand();
        RulesCommand rules = new RulesCommand();
        KeyChainCommand keyChain;

        registerCommand("spawn-shop-npc", new SpawnShopNPCCommand());
        registerCommand("point", new PointCommand());
        registerCommand("formatting", new FormattingCommand());
        registerCommand("verify", new VerifyCommand());
        registerCommand("unverify", new UnverifyCommand());
        registerCommand("help", new HelpCommand());
        registerCommand("rules", rules);
        registerCommand("teamchat", new TeamChatCommand());
        registerCommand("keys", keyChain = new KeyChainCommand(this));
        registerCommand("bug", new BugCommand());
        registerCommand("feedback", new FeedbackCommand());
        registerCommand("leaderboard", new LeaderboardCommand());
        registerCommand("claim", claim);

        registerCommand("info", new MinecraftInfoCommand());
        //Events
        registerEvent(new MinecraftVerificationListener());
        registerEvent(new SlimeHandler());
        registerEvent(new PointsListener());
        registerEvent(rules);
        registerEvent(keyChain);
        registerEvent(new DeathPointHandler());
        registerEvent(claim);
        registerEvent(new ClaimProtectionHandler());
        registerEvent(new PunishmentListener(this));

        //UI
        registerEvent(new ScoreboardManager(this));
        registerEvent(new Tablist(this));
        registerEvent(new Chat(getConfig()));

        //Custom Items
        registerEvent(chestKey = new ChestKey(this));
        registerEvent(lockPick = new LockPick(chestKey));


        CustomItem.getItems().forEach(i -> itemManager.registerItem(i.getName(), i::getItem));
        itemManager.registerItem("mending-Infinity-bow", () -> new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_INFINITE, 1).addEnchantment(Enchantment.MENDING, 1).build());

        SlimeHandler.setupOffers(getConfig());

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

        Bukkit.getWorlds().forEach(
                world -> world.getEntitiesByClass(Shulker.class).removeIf(
                        shulker -> shulker.getScoreboardTags().stream().anyMatch(s -> s.contains("marker1")) || shulker.getScoreboardTags().stream().anyMatch(s -> s.contains("marker2"))
                )
        );

        ClaimCommand.claimingPlayers.values().forEach(ClaimInfo::stopTasks);
        ClaimCommand.claimingPlayers.clear();

        this.discordBot.shutdown();
    }

    public static TextComponent getPrefix() {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("Hard", TextColor.color(0x88D657)))
                .append(Component.text("SMP", TextColor.color(0xF6ED82)))
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
