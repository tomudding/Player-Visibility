package nl.tomudding.plugins.visibility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
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

	public static boolean enableActionbar = true;
	public static boolean enableDyes = false;
	public static boolean enableItemSwitchMessage = false;	
	public static boolean enableJoinMessage = true;
	public static boolean enableOffHand = false;
	public static boolean enableWorldToggleMessage = false;
	public static boolean enableWorldSwitchMessage = false;

	public static int toggleCooldown = 10;
	public static int itemSlot = 8;

	public static String itemIdOn = "SLIME_BALL";
	public static String itemIdOff = "MAGMA_CREAM";
	public static String dyeColorOn = "LIME";
	public static String dyeColorOff = "GRAY";
	public static String itemNameOn = "&7Players are &aon!";
	public static String itemNameOff = "&7Players are &coff!";
	
	public static String messagePrefix = "&9Visibility > ";
	public static String messageCooldown = "&7Please wait 10 seconds before toggling again.";
	public static String messagePermission = "&cYou don't have the permissions to do that.";
	public static String messageToggleOn = "&7All players are turned &aon!";
	public static String messageToggleOff = "&7All players are turned &coff!";
	public static String messageWorld = "&cYou can't hide/unhide players in this world.";
	public static String messageAlreadyOn = "&7All players are already &aon!";
	public static String messageAlreadyOff = "&7All players are already &coff!";
	public static String messageSwitch = "&cYou can't change this item its place.";
	public static String configVersion = "0.0";
	
	public static List<String> enabledWorlds;
	public static ArrayList<String> itemLoreOn = new ArrayList<String>(Arrays.asList("&7Toggle player visibility to &coff"));
	public static ArrayList<String> itemLoreOff = new ArrayList<String>(Arrays.asList("&7Toggle player visibility to &aon"));
	public static HashMap<UUID, Long> inCooldown = new HashMap<UUID, Long>();
	
	public void onEnable() {
		ChatManager.getInstance().log("Starting Player Visibility for Bukkit/Spigot");
		
		if (checkServerVersion()) {
			ChatManager.getInstance().log("&c==========================================");
			ChatManager.getInstance().log("&cWARNING: Your server software is outdated!");
			ChatManager.getInstance().log("&cWARNING: This plugin requires at least");
			ChatManager.getInstance().log("&cWARNING: Minecraft version 1.9 or higher.");
			ChatManager.getInstance().log("&c==========================================");
			ChatManager.getInstance().log("Disabling Player Visibility for Bukkit/Spigot");
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Visibility");
			plugin.getPluginLoader().disablePlugin(plugin);
			return;
		}
		
		saveDefaultConfig();
		
		this.settings.setup(this);
		this.settings.saveData();
		
		loadConfig();
		
		if (!getDescription().getVersion().substring(0, 3).contains(configVersion)) {
			ChatManager.getInstance().log("&cWARNING: Config.yml version: "+Visibility.configVersion+". Plugin version: "+getDescription().getVersion()+"!");
			ChatManager.getInstance().log("&cWARNING: Config.yml is not the correct version, delete 'config.yml' and restart the server!");
		} else {
			ChatManager.getInstance().log("&aConfig.yml version matches required version.");
		}
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getCommand("visibility").setExecutor(new Commands(this));
		getCommand("hide").setExecutor(new Commands(this));
		getCommand("show").setExecutor(new Commands(this));
		
		ChatManager.getInstance().log("Player Visibility for Bukkit/Spigot is now enabled");
	}
	
	public void onDisable() {
		ChatManager.getInstance().log("Disabling Player Visibility for Bukkit/Spigot");
		ChatManager.getInstance().log("Player Visibility for Bukkit/Spigot is now disabled");
	}
	
	public static Plugin getInstance() {
		return Bukkit.getServer().getPluginManager().getPlugin("Visibility");
	}
	
	public static boolean checkServerVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		return (!(version.contains("1_9")) && !(version.contains("1_10")) && !(version.contains("1_11")) && !(version.contains("1_12")));
	}
	
	public static boolean checkServerVersionAbove1_12() {
		String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		return (!(version.contains("1_9")) && !(version.contains("1_10")) && !(version.contains("1_11")));
	}
	
	public static Class<?> getNMSClass(String nmsClassName) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	}
	
    public static ItemStack createItemStack(boolean toggleState) {
    	ItemStack itemStack = null;
    	DyeColor dyeColor = null;
    	
    	if (toggleState) {
    		if (Visibility.enableDyes) {
    			Dye dye = new Dye();
    			
    			try {
    				dyeColor = DyeColor.valueOf(Visibility.dyeColorOn);
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid DyeColor type (for 'on' state)!");
    				dyeColor = DyeColor.LIME;
    			}
    			
    			dye.setColor(dyeColor);
    			itemStack = dye.toItemStack(1);
    		} else {
    			try {
    				itemStack = new ItemStack(Material.valueOf(Visibility.itemIdOn));
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid ItemStack type (for 'on' state)!");
    				itemStack = new ItemStack(Material.SLIME_BALL);
    			}
    		}
    		
    		ItemMeta itemMeta = itemStack.getItemMeta();
    		itemMeta.setLore(doChatColor(Visibility.itemLoreOn));
	  		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Visibility.itemNameOn));
	  		itemStack.setItemMeta(itemMeta);
    	} else {
    		if (Visibility.enableDyes) {
    			Dye dye = new Dye();
    			
    			try {
    				dyeColor = DyeColor.valueOf(Visibility.dyeColorOff);
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid DyeColor type (for 'off' state)!");
    				dyeColor = DyeColor.GRAY;
    			}
    			
    			dye.setColor(dyeColor);
    			itemStack = dye.toItemStack(1);
    		} else {
    			try {
    				itemStack = new ItemStack(Material.valueOf(Visibility.itemIdOff));
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid ItemStack type (for 'off' state)!");
    				itemStack = new ItemStack(Material.MAGMA_CREAM);
    			}
    		}
    		
    		ItemMeta itemMeta = itemStack.getItemMeta();
    		itemMeta.setLore(doChatColor(Visibility.itemLoreOff));
	  		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Visibility.itemNameOff));
	  		itemStack.setItemMeta(itemMeta);
    	}
    	
		return itemStack;
    }
    
    private static ArrayList<String> doChatColor(ArrayList<String> array) {
    	ArrayList<String> list = new ArrayList<String>();
    	for (String string : array) {
    		list.add(ChatColor.translateAlternateColorCodes('&', string));
    	}
    	
    	return list;
    }
    
    public static ItemStack getItemInHand(Player player) {
    	if (Visibility.enableOffHand) {
    		return player.getInventory().getItemInOffHand();
    	} else {
    		if (Visibility.itemSlot >= 0 && Visibility.itemSlot <= 8) {
    			return player.getInventory().getItemInMainHand();
    		} else {
    			ChatManager.getInstance().log("&cProvided slot ID does not exist! Should be between 0 and 8!");
    			return null;
    		}
    	}
    }
    
    public static void setItemInHand(Player player, ItemStack itemStack) {
    	if (Visibility.enableOffHand) {
    		player.getInventory().setItemInOffHand(itemStack);
    	} else {
    		if (Visibility.itemSlot >= 0 && Visibility.itemSlot <= 8) {
    			player.getInventory().setItem(Visibility.itemSlot, itemStack);
    		} else {
    			ChatManager.getInstance().log("&cProvided slot ID does not exist! Should be between 0 and 8!");
    		}
    	}
    }
	
	public static void setCooldown(Player player, boolean toggledState) {
		Visibility.inCooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));

		try {
			Class<?> nmsItemClass = Visibility.getNMSClass("Item");
			Method nmsItemMethod = nmsItemClass.getMethod("getById", int.class);
			@SuppressWarnings("deprecation")
			Object nmsItemId = nmsItemMethod.invoke(null, Visibility.createItemStack(toggledState).getTypeId());
			Object packetPlayOutSetCooldown = Visibility.getNMSClass("PacketPlayOutSetCooldown").getConstructor(new Class[] { nmsItemClass, int.class }).newInstance(new Object[] { nmsItemId, 20 * Visibility.toggleCooldown});
	
			Object playerNMS = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = playerNMS.getClass().getField("playerConnection").get(playerNMS);
			Class<?> playerPacket = Visibility.getNMSClass("Packet");
			playerConnection.getClass().getMethod("sendPacket", new Class[] { playerPacket }).invoke(playerConnection, new Object[] { packetPlayOutSetCooldown });
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static void removeCooldown(Player player, boolean toggledState) {
		Visibility.inCooldown.remove(player.getUniqueId());
		
		try {
			Class<?> nmsItemClass = Visibility.getNMSClass("Item");
			Method nmsItemMethod = nmsItemClass.getMethod("getById", int.class);
			@SuppressWarnings("deprecation")
			Object nmsItemId = nmsItemMethod.invoke(null, Visibility.createItemStack(toggledState).getTypeId());
			Object packetPlayOutSetCooldown = Visibility.getNMSClass("PacketPlayOutSetCooldown").getConstructor(new Class[] { nmsItemClass, int.class }).newInstance(new Object[] { nmsItemId, 0 });
	
			Object playerNMS = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = playerNMS.getClass().getField("playerConnection").get(playerNMS);
			Class<?> playerPacket = Visibility.getNMSClass("Packet");
			playerConnection.getClass().getMethod("sendPacket", new Class[] { playerPacket }).invoke(playerConnection, new Object[] { packetPlayOutSetCooldown });
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void loadConfig() {
		Visibility.enableActionbar = getConfig().getBoolean("options.enableActionbar");
		Visibility.enableDyes = getConfig().getBoolean("options.enableDyes");
		Visibility.enableItemSwitchMessage = getConfig().getBoolean("options.enableItemSwitchMessage");
		Visibility.enableJoinMessage = getConfig().getBoolean("options.enableJoinMessage");
		Visibility.enableOffHand = getConfig().getBoolean("options.enableOffHand");
		Visibility.enableWorldToggleMessage = getConfig().getBoolean("options.enableWorldToggleMessage");
		Visibility.enableWorldSwitchMessage = getConfig().getBoolean("options.enableWorldSwitchMessage");
		Visibility.toggleCooldown = getConfig().getInt("options.toggleCooldown");
		Visibility.dyeColorOn = getConfig().getString("item.true.dye");
		Visibility.dyeColorOff = getConfig().getString("item.false.dye");
		Visibility.itemIdOn = getConfig().getString("item.true.item");
		Visibility.itemIdOff = getConfig().getString("item.false.item");
		Visibility.itemSlot = getConfig().getInt("item.slot");
		Visibility.itemNameOn = getConfig().getString("item.true.name");
		Visibility.itemNameOff = getConfig().getString("item.false.name");
		Visibility.itemLoreOn = (ArrayList<String>) getConfig().getStringList("item.true.lore");
		Visibility.itemLoreOff = (ArrayList<String>) getConfig().getStringList("item.false.lore");
		Visibility.messagePrefix = getConfig().getString("messages.prefix");
		Visibility.messageCooldown = getConfig().getString("messages.toggleCooldown");
		Visibility.messagePermission = getConfig().getString("messages.permission");
		Visibility.messageToggleOn = getConfig().getString("messages.toggle.true");
		Visibility.messageToggleOff = getConfig().getString("messages.toggle.false");
		Visibility.messageWorld = getConfig().getString("messages.world");
		Visibility.messageSwitch = getConfig().getString("messages.itemSwitch");
		Visibility.messageAlreadyOn = getConfig().getString("messages.toggle.already.true");
		Visibility.messageAlreadyOff = getConfig().getString("messages.toggle.already.false");
		Visibility.enabledWorlds = getConfig().getStringList("enabled-Worlds");
		Visibility.configVersion = getConfig().getString("config");
	}
}
