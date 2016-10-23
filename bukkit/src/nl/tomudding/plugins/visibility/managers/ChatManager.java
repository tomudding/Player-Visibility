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
	
	private Class<?> getNMSClass(String nmsClassName) throws ClassNotFoundException {
			return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	}

	public void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Player Visibility >> " + ChatColor.translateAlternateColorCodes('&', s));
	}

	public void sendMessage(Player player, String message) {
		if (Visibility.actionBar) {
			try {
				if ((Visibility.getServerVersion().equalsIgnoreCase("v1_9_R1")) || (Visibility.getServerVersion().equalsIgnoreCase("v1_9_R2")) || (Visibility.getServerVersion().equalsIgnoreCase("v1_10_R1"))) {
					// if the server does not use this version it should have been disabled
					Object chatComponentText = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { ChatColor.translateAlternateColorCodes('&', message)});
					Class<?> iChatBaseComponent = getNMSClass("IChatBaseComponent");
					Object packetPlayOutChat = getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { iChatBaseComponent, Byte.TYPE }).newInstance(new Object[] { chatComponentText, Byte.valueOf((byte) 2) });
					
					Object playerNMS = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
					Object playerConnection = playerNMS.getClass().getField("playerConnection").get(playerNMS);
					Class<?> playerPacket = getNMSClass("Packet");
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
