package nl.tomudding.plugins.visibility.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.tomudding.plugins.visibility.Visibility;
import nl.tomudding.plugins.visibility.managers.ChatManager;
import nl.tomudding.plugins.visibility.managers.PlayerManager;

public class Commands implements CommandExecutor {

	private Visibility plugin;
	
	public Commands(Visibility plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (cmd.getName().equalsIgnoreCase("visibility")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.GOLD + "---------- " + ChatColor.GRAY + "Player Visibility" + ChatColor.GOLD + " ----------");
					player.sendMessage(ChatColor.GOLD + "/"+cmd.getName().toLowerCase().toString()+"" + ChatColor.DARK_AQUA + " - This help menu");
					player.sendMessage(ChatColor.GOLD + "/"+cmd.getName().toLowerCase().toString()+" info" + ChatColor.DARK_AQUA + " - Information about the plugin");
					player.sendMessage(ChatColor.GOLD + "/hide" + ChatColor.DARK_AQUA + " - Hide players");
					player.sendMessage(ChatColor.GOLD + "/show" + ChatColor.DARK_AQUA + " - Show players");
					return true;
				} else if ((args.length == 1) && (args[0].equalsIgnoreCase("info"))) {
					player.sendMessage(ChatColor.GOLD + "---------- " + ChatColor.GRAY + "Player Visibility" + ChatColor.GOLD + " ----------");
					player.sendMessage(ChatColor.DARK_AQUA + "Plugin by - " + ChatColor.GOLD + "tomudding");
					player.sendMessage(ChatColor.DARK_AQUA + "Plugin version - " + ChatColor.GOLD + plugin.getDescription().getVersion());
					return true;
				}
			} else if (cmd.getName().equalsIgnoreCase("hide")) {
				if (!player.hasPermission("visibility.command.hide")) { ChatManager.getInstance().sendMessage(player, Visibility.messagePermission, true); return true; }
				if (!Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString())) { ChatManager.getInstance().sendMessage(player, Visibility.messageWorld, true); return true; }
				if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == false) { ChatManager.getInstance().sendMessage(player, Visibility.messageAlreadyOff, true); return true; }
				if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == true) { ChatManager.getInstance().sendMessage(player, Visibility.messageAlreadyOn, true); return true; }
				if (Visibility.inCooldown.containsKey(player.getUniqueId())) {
					long timeLeft = Visibility.inCooldown.get(player.getUniqueId()).longValue() / 1000L + Visibility.timeCooldown - (System.currentTimeMillis() / 1000L);
					if (timeLeft > 0L) {
						ChatManager.getInstance().sendMessage(player, Visibility.messageCooldown.replace("%time%", Long.toString(timeLeft)), true);
					} else {
						Visibility.removeCooldown(player, false);
					}
					return true;
				}
				
				if (!player.hasPermission("visibility.bypass.cooldown")) { Visibility.setCooldown(player, false); }
				player.getInventory().setItem(Visibility.itemSlot, Visibility.createItemStack(false));
				
				for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
					if (!onlinePlayers.hasPermission("visibility.ignore")) {
						player.hidePlayer(onlinePlayers);
					}
				}

				PlayerManager.getInstance().setToggle(player.getUniqueId(), false);
				ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOff, true);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("show")) {
				if (!player.hasPermission("visibility.command.show")) { ChatManager.getInstance().sendMessage(player, Visibility.messagePermission, true); return true; }
				if (!Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString())) { ChatManager.getInstance().sendMessage(player, Visibility.messageWorld, true); return true; }
				if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == true) { ChatManager.getInstance().sendMessage(player, Visibility.messageAlreadyOn, true); return true; }
				if (Visibility.inCooldown.containsKey(player.getUniqueId())) {
					long timeLeft = Visibility.inCooldown.get(player.getUniqueId()).longValue() / 1000L + Visibility.timeCooldown - (System.currentTimeMillis() / 1000L);
					if (timeLeft > 0L) {
						ChatManager.getInstance().sendMessage(player, Visibility.messageCooldown.replace("%time%", Long.toString(timeLeft)), true);
					} else {
						Visibility.removeCooldown(player, true);
					}
					return true;
				}
				
				if (!player.hasPermission("visibility.bypass.cooldown")) { Visibility.setCooldown(player, true); }
				player.getInventory().setItem(Visibility.itemSlot, Visibility.createItemStack(true));
				
				for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
					player.showPlayer(onlinePlayers);
				}
				
				PlayerManager.getInstance().setToggle(player.getUniqueId(), true);
				ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOn, true);
				return true;
			}
		} else {
			if (cmd.getName().equalsIgnoreCase("visibility")) {
				if (args.length == 0) {
					ChatManager.getInstance().log(ChatColor.GOLD + "---------- " + ChatColor.GRAY + "Player Visibility" + ChatColor.GOLD + " ----------");
					ChatManager.getInstance().log(ChatColor.GOLD + "/"+cmd.getName().toLowerCase().toString()+"" + ChatColor.DARK_AQUA + " - This help menu");
					ChatManager.getInstance().log(ChatColor.GOLD + "/"+cmd.getName().toLowerCase().toString()+" info" + ChatColor.DARK_AQUA + " - Information about the plugin");
					return true;
				} else if ((args.length == 1) && (args[0].equalsIgnoreCase("info"))) {
					ChatManager.getInstance().log(ChatColor.GOLD + "---------- " + ChatColor.GRAY + "Player Visibility" + ChatColor.GOLD + " ----------");
					ChatManager.getInstance().log(ChatColor.DARK_AQUA + "Plugin by - " + ChatColor.GOLD + "tomudding");
					ChatManager.getInstance().log(ChatColor.DARK_AQUA + "Plugin version - " + ChatColor.GOLD + plugin.getDescription().getVersion());
					return true;
				}
			} else if ((cmd.getName().equalsIgnoreCase("hide")) || (cmd.getName().equalsIgnoreCase("show"))) {
				ChatManager.getInstance().log("&cThis command is player-only.");
				return true;
			}
		}
		return false;
	}
}
