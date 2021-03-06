package nl.tomudding.plugins.visibility.managers;

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
		if (Visibility.actionBar) {
			try {
				if (Visibility.getServerVersion().startsWith("v1_8_")) {
					// if the server does not use this version it should have been disabled
					Object chatComponentText = Visibility.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { ChatColor.translateAlternateColorCodes('&', message)});
					Class<?> iChatBaseComponent = Visibility.getNMSClass("IChatBaseComponent");
					Object packetPlayOutChat = Visibility.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { iChatBaseComponent, Byte.TYPE }).newInstance(new Object[] { chatComponentText, Byte.valueOf((byte) 2) });
					
					Object playerNMS = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
					Object playerConnection = playerNMS.getClass().getField("playerConnection").get(playerNMS);
					Class<?> playerPacket = Visibility.getNMSClass("Packet");
					playerConnection.getClass().getMethod("sendPacket", new Class[] { playerPacket }).invoke(playerConnection, new Object[] { packetPlayOutChat });
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Visibility.messagePrefix + message));
		}
	}
	
	public void sendCommandMessage(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
