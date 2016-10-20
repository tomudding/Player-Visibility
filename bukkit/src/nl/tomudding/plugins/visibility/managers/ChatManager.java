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

	public void sendMessage(Player player, String s) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', Visibility.messagePrefix + s));
	}
	
	public void sendCommandMessage(Player player, String s) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}
}
