package nl.tomudding.plugins.visibility.managers;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.tomudding.plugins.visibility.Visibility;

public class ChatManager {
	private static ChatManager instance = new ChatManager();

	public static ChatManager getInstance() {
		return instance;
	}
	
	public void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Player Visibility >> " + ChatColor.translateAlternateColorCodes('&', s));
	}

	public void sendMessage(Player player, String message) {
		if (Visibility.enableActionbar) {
			try {
				Object packetPlayOutChat = null;
				if (Visibility.checkServerVersionAbove1_12()) {
		            Constructor<?> constructor = Visibility.getNMSClass("PacketPlayOutChat").getConstructor(Visibility.getNMSClass("IChatBaseComponent"), Visibility.getNMSClass("ChatMessageType"));
		            Object iChatBaseComponent = Visibility.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
		            packetPlayOutChat = constructor.newInstance(iChatBaseComponent, Visibility.getNMSClass("ChatMessageType").getEnumConstants()[2]);
		         } else {
					Constructor<?> constructor = Visibility.getNMSClass("PacketPlayOutChat").getConstructor(Visibility.getNMSClass("IChatBaseComponent"), byte.class);
					Object iChatBaseComponent = Visibility.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
					packetPlayOutChat = constructor.newInstance(iChatBaseComponent, (byte) 2);
				}
		
				Object playerNMS = player.getClass().getMethod("getHandle").invoke(player);
				Object playerConnection = playerNMS.getClass().getField("playerConnection").get(playerNMS);
				playerConnection.getClass().getMethod("sendPacket", Visibility.getNMSClass("Packet")).invoke(playerConnection, packetPlayOutChat);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Visibility.messagePrefix + message));
		}
	}
	
	public void sendMessage(Player player, String message, boolean cmd, boolean cmdPrefix) {
		if (Visibility.enableActionbar) {
			try {
				Object packetPlayOutChat = null;
				if (Visibility.checkServerVersionAbove1_12()) {
		            Constructor<?> constructor = Visibility.getNMSClass("PacketPlayOutChat").getConstructor(Visibility.getNMSClass("IChatBaseComponent"), Visibility.getNMSClass("ChatMessageType"));
		            Object iChatBaseComponent = Visibility.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
		            packetPlayOutChat = constructor.newInstance(iChatBaseComponent, Visibility.getNMSClass("ChatMessageType").getEnumConstants()[2]);
		         } else {
					Constructor<?> constructor = Visibility.getNMSClass("PacketPlayOutChat").getConstructor(Visibility.getNMSClass("IChatBaseComponent"), byte.class);
					Object iChatBaseComponent = Visibility.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
					packetPlayOutChat = constructor.newInstance(iChatBaseComponent, (byte) 2);
				}
		
				Object playerNMS = player.getClass().getMethod("getHandle").invoke(player);
				Object playerConnection = playerNMS.getClass().getField("playerConnection").get(playerNMS);
				playerConnection.getClass().getMethod("sendPacket", Visibility.getNMSClass("Packet")).invoke(playerConnection, packetPlayOutChat);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else if (cmd) {
			if (cmdPrefix) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', Visibility.messagePrefix + message));
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Visibility.messagePrefix + message));
		}
	}
}
