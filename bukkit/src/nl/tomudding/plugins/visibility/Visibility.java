package nl.tomudding.plugins.visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import nl.tomudding.plugins.visibility.commands.Commands;
import nl.tomudding.plugins.visibility.listeners.PlayerListener;
import nl.tomudding.plugins.visibility.managers.ChatManager;
import nl.tomudding.plugins.visibility.managers.PlayerManager;

public class Visibility extends JavaPlugin {

	public final Logger logger = Logger.getLogger("Minecraft");
	protected Logger log;
	public PlayerManager settings = PlayerManager.getInstance();

	public static boolean isDyeEnabled = false;
	public static boolean actionBar = true;

	public static int timeCooldown = 10;
	public static int itemSlot = 8;

	public static String itemIdOn = "SLIME_BALL";
	public static String itemIdOff = "MAGMA_CREAM";
	public static String dyeColorOn = "LIME";
	public static String dyeColorOff = "GRAY";
	public static String itemNameOn = "&7Players are &aon!";
	public static String itemNameOff = "&7Players are &coff!";
	public static String itemLoreOn = "&7Toggle player visibility to &coff";
	public static String itemLoreOff = "&7Toggle player visibility to &aon";
	
	public static String messagePrefix = "&9Visibility > ";
	public static String messageCooldown = "&7Please wait 10 seconds before toggling again.";
	public static String messagePermission = "&cYou don't have the permissions to do that.";
	public static String messageToggleOn = "&7All players are turned &aon!";
	public static String messageToggleOff = "&7All players are turned &coff!";
	public static String messageWorld = "&cYou can't hide/unhide players in this world.";
	public static String messageAlreadyOn = "&7All players are already &aon!";
	public static String messageAlreadyOff = "&7All players are already &coff!";
	public static String messageNoSwitch = "&cYou can't change this item its place.";
	public static String configVersion = "0.0";

	public static List<String> enabledWorlds;
	public static ArrayList<UUID> inCooldown = new ArrayList<UUID>();
	
	public void onEnable() {
		ChatManager.getInstance().log("Starting Player Visibility for Bukkit");
		
		if (!(getServerVersion().equalsIgnoreCase("v1_9_R1")) && !(getServerVersion().equalsIgnoreCase("v1_9_R2")) && !(getServerVersion().equalsIgnoreCase("v1_10_R1"))) {
			ChatManager.getInstance().log("&c==========================================");
			ChatManager.getInstance().log("&cWARNING: Your server software is outdated!");
			ChatManager.getInstance().log("&cWARNING: This plugin requires at least");
			ChatManager.getInstance().log("&cWARNING: Minecraft version 1.9 or higher.");
			ChatManager.getInstance().log("&c==========================================");
			ChatManager.getInstance().log("Disabling Player Visibility for Bukkit");
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Visibility");
			plugin.getPluginLoader().disablePlugin(plugin);
			return;
		}
		
		saveDefaultConfig();
		
		this.settings.setup(this);
		this.settings.saveData();
		
		loadConfig();
		
		if (!StringUtils.substring(getDescription().getVersion(), 0, 3).equals(configVersion)) {
			ChatManager.getInstance().log("&cWARNING: Config.yml version: "+Visibility.configVersion+". Plugin version: "+getDescription().getVersion()+"!");
			ChatManager.getInstance().log("&cWARNING: Config.yml is not the correct version, delete 'config.yml' and restart the server!");
		} else {
			ChatManager.getInstance().log("&aConfig.yml version matches required version.");
		}
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getCommand("visibility").setExecutor(new Commands(this));
		getCommand("hide").setExecutor(new Commands(this));
		getCommand("show").setExecutor(new Commands(this));
		
		ChatManager.getInstance().log("Player Visibility for Bukkit is now enabled");
	}
	
	public void onDisable() {
		ChatManager.getInstance().log("Disabling Player Visibility for Bukkit");
		ChatManager.getInstance().log("Player Visibility for Bukkit is now disabled");
	}
	
	public static String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}
	
	public void loadConfig() {
		Visibility.isDyeEnabled = getConfig().getBoolean("enableDye");
		Visibility.actionBar = getConfig().getBoolean("messages.actionbar");
		
		Visibility.dyeColorOn = getConfig().getString("item.true.dye");
		Visibility.dyeColorOff = getConfig().getString("item.false.dye");
		Visibility.itemIdOn = getConfig().getString("item.true.item");
		Visibility.itemIdOff = getConfig().getString("item.false.item");
		
		Visibility.timeCooldown = getConfig().getInt("cooldown");
		Visibility.itemSlot = getConfig().getInt("item.slot");
		
		Visibility.itemNameOn = getConfig().getString("item.true.name");
		Visibility.itemNameOff = getConfig().getString("item.false.name");
		Visibility.itemLoreOn = getConfig().getString("item.true.lore");
		Visibility.itemLoreOff = getConfig().getString("item.false.lore");
		
		Visibility.messagePrefix = getConfig().getString("messages.prefix");
		Visibility.messageCooldown = getConfig().getString("messages.cooldown");
		Visibility.messagePermission = getConfig().getString("messages.permission");
		Visibility.messageToggleOn = getConfig().getString("messages.toggle.true");
		Visibility.messageToggleOff = getConfig().getString("messages.toggle.false");
		Visibility.messageWorld = getConfig().getString("messages.world");
		Visibility.messageAlreadyOn = getConfig().getString("messages.toggle.already.true");
		Visibility.messageAlreadyOff = getConfig().getString("messages.toggle.already.false");
		
		Visibility.enabledWorlds = getConfig().getStringList("Enabled-Worlds");
		
		Visibility.configVersion = getConfig().getString("config");
	}
}
