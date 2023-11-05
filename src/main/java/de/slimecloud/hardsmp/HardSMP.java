package de.slimecloud.hardsmp;

import de.cyklon.spigotutils.item.ItemBuilder;
import de.slimecloud.hardsmp.advancement.AdvancementHandler;
import de.slimecloud.hardsmp.commands.SpawnShopNPCCommand;
import de.slimecloud.hardsmp.database.Database;
import de.slimecloud.hardsmp.item.ItemManager;
import de.slimecloud.hardsmp.player.data.PointsListener;
import de.slimecloud.hardsmp.shop.SlimeHandler;
import de.slimecloud.hardsmp.verify.MinecraftVerificationListener;
import lombok.Getter;
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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardSMP extends JavaPlugin {

	public final NamespacedKey TEAM_KEY = new NamespacedKey(this, "team");
	public final NamespacedKey SHOP_KEY = new NamespacedKey(this, "shop");

	@Getter
	private static HardSMP instance;

	@Getter
	private Database database;

	@Getter
	private ItemManager itemManager;

	@Getter
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

		itemManager.registerItem("chest-key", () -> new ItemBuilder(Material.IRON_HOE).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(ChatColor.RESET + "Chest Key").build());

		SlimeHandler.setupOffers(getConfig());

		//Events
		registerEvent(new MinecraftVerificationListener());
		registerEvent(new SlimeHandler());
		registerEvent(new PointsListener());

		AdvancementHandler.register(this, this::registerEvent);

		try {
			new DiscordBot();
		} catch (Exception e) {
			getLogger().warning("Failed to init Discord bot: %s".formatted(e));
		}
	}

	@Override
	public void onDisable() {

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
