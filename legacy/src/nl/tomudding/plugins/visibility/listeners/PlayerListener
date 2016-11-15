package nl.tomudding.plugins.visibility.listeners;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.scheduler.BukkitScheduler;

import nl.tomudding.plugins.visibility.Visibility;
import nl.tomudding.plugins.visibility.managers.ChatManager;
import nl.tomudding.plugins.visibility.managers.PlayerManager;

public class PlayerListener implements Listener {
	private Visibility plugin;
	
	public PlayerListener(Visibility plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString())) {
			if (PlayerManager.getInstance().checkIfExists(uuid) == false) {
				ChatManager.getInstance().log("Player " + uuid + " is not in data.yml, injecting...");
				
				PlayerManager.getInstance().setToggle(uuid, true);
				for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
					player.showPlayer(onlinePlayers);
				}

				player.getInventory().setItem(Visibility.itemSlot, createItemStack(true));
				ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOn);
			} else if (PlayerManager.getInstance().checkIfExists(uuid) == true) {
				ChatManager.getInstance().log("Player " + uuid + " is in data.yml");
				
				if (PlayerManager.getInstance().getToggleState(uuid) == true) {
	  		  		for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
	  		  			if (Visibility.enabledWorlds.contains(onlinePlayers.getWorld().getName().toString())) {
	  		  				player.showPlayer(onlinePlayers);
	  		  			}
	  		  		}
	  		  		
					player.getInventory().setItem(Visibility.itemSlot, createItemStack(true));
					ChatManager.getInstance().sendMessage(player,  Visibility.messageToggleOn);
				} else if (PlayerManager.getInstance().getToggleState(uuid) == false) {
	  		  		for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
	  		  			if (Visibility.enabledWorlds.contains(onlinePlayers.getWorld().getName().toString())) {
	  		  				if (!onlinePlayers.hasPermission("visibility.ignore")) {
	  		  					player.hidePlayer(onlinePlayers);
	  		  				}
	  		  			}
	  		  		}
	  		  		
					player.getInventory().setItem(Visibility.itemSlot, createItemStack(false));
					ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOff);
				}
			}
			
			for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
				if (Visibility.enabledWorlds.contains(onlinePlayers.getLocation().getWorld().getName().toString())) {
					if (PlayerManager.getInstance().getToggleState(onlinePlayers.getUniqueId()) == true) {
						onlinePlayers.showPlayer(player);
					} else if (PlayerManager.getInstance().getToggleState(onlinePlayers.getUniqueId()) == false) {
						if (!player.hasPermission("visibility.ignore")) {
							onlinePlayers.hidePlayer(player);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getInventory().getItemInHand().equals(null) || player.getInventory().getItemInHand().equals(Material.AIR)) return;
		if (player.getInventory().getItemInHand().equals(createItemStack(true))) {
			if (!Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString())) { ChatManager.getInstance().sendMessage(player, Visibility.messageWorld); return; }
			if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
			if (event.getItem().getType().isBlock()) event.setCancelled(true);
			if (player.hasPermission("visibility.hide")) {
				if (!Visibility.inCooldown.contains(player.getUniqueId())) {
					for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
						if (!onlinePlayers.hasPermission("visibility.ignore")) {
							player.hidePlayer(onlinePlayers);
						}
					}
					
					player.getInventory().setItem(Visibility.itemSlot, createItemStack(false));
					PlayerManager.getInstance().setToggle(player.getUniqueId(), false);
					
					if (!player.hasPermission("visibility.cooldown")) {
						Visibility.inCooldown.add(player.getUniqueId());
						
	  		  			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	  		  			scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
							@Override
							public void run() {
								Visibility.inCooldown.remove(player.getUniqueId());
							}
						}, Visibility.timeCooldown * 20);
					}

					ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOff);
				} else {
					ChatManager.getInstance().sendMessage(player, Visibility.messageCooldown);
				}
			} else {
				ChatManager.getInstance().sendMessage(player, Visibility.messagePermission);
			}
		} else if (player.getInventory().getItemInHand().equals(createItemStack(false))) {
			if (!Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString())) { ChatManager.getInstance().sendMessage(player, Visibility.messageWorld); return; }
			if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
			if (event.getItem().getType().isBlock()) event.setCancelled(true);
			if (player.hasPermission("visibility.show")) {
				if (!Visibility.inCooldown.contains(player.getUniqueId())) {
					for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
						player.showPlayer(onlinePlayers);
					}
					
					player.getInventory().setItem(Visibility.itemSlot, createItemStack(true));
					PlayerManager.getInstance().setToggle(player.getUniqueId(), true);
					
					if (!player.hasPermission("visibility.cooldown")) {
						Visibility.inCooldown.add(player.getUniqueId());
						
	  		  			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	  		  			scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
							@Override
							public void run() {
								Visibility.inCooldown.remove(player.getUniqueId());
							}
						}, Visibility.timeCooldown * 20);
					}

					ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOn);
				} else {
					ChatManager.getInstance().sendMessage(player, Visibility.messageCooldown);
				}
			} else {
				ChatManager.getInstance().sendMessage(player, Visibility.messagePermission);
			}
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		
		if (Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString())) {
			if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == true) {
				player.getPlayer().getInventory().setItem(Visibility.itemSlot, createItemStack(true));
			} else if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == false) {
				player.getPlayer().getInventory().setItem(Visibility.itemSlot, createItemStack(false));
			}
			
			for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
				if (onlinePlayers.getWorld().equals(player.getLocation().getWorld())) {
					if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == true) {
	  		  			player.showPlayer(onlinePlayers);
					} else if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == false) {
	  		  			if (!onlinePlayers.hasPermission("visibility.ignore")) {
	  		  				player.hidePlayer(onlinePlayers);
	  		  			}
					}
					
  		  			if (PlayerManager.getInstance().getToggleState(onlinePlayers.getUniqueId()) == true) {
  		  				onlinePlayers.showPlayer(player);
  		  			} else if (PlayerManager.getInstance().getToggleState(onlinePlayers.getUniqueId()) == false) {
  		  				if (!player.hasPermission("visibility.ignore")) {
  		  					onlinePlayers.hidePlayer(player);
  		  				}
  		  			}
				}
			}
			
			if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == true) {
				ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOn);
			} else if (PlayerManager.getInstance().getToggleState(player.getUniqueId()) == false) {
				ChatManager.getInstance().sendMessage(player, Visibility.messageToggleOff);
			}
		} else {
			for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
				player.showPlayer(onlinePlayers);
				onlinePlayers.showPlayer(player); // Not sure about this one
			}
			
			if (player.getInventory().contains(createItemStack(true))) {
				player.getInventory().remove(createItemStack(false));
			} else if (player.getInventory().contains(createItemStack(false))) {
				player.getInventory().remove(createItemStack(false));
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (Visibility.enabledWorlds.contains(event.getRespawnLocation().getWorld().getName().toString())) {
			player.getInventory().setItem(Visibility.itemSlot, createItemStack(PlayerManager.getInstance().getToggleState(player.getUniqueId())));
		} else {
			for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
				player.showPlayer(onlinePlayers);
				onlinePlayers.showPlayer(player); // Not sure about this one
			}
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (Visibility.enabledWorlds.contains(player.getLocation().getWorld().getName().toString()) && player.getInventory().getHeldItemSlot() == Visibility.itemSlot) {
			event.setCancelled(true);
			ChatManager.getInstance().sendMessage(player, Visibility.messageNoSwitch);
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		if (Visibility.enabledWorlds.contains(event.getWhoClicked().getLocation().getWorld().getName().toString()) && 
			event.getSlot() == Visibility.itemSlot) {
				event.setCancelled(true);
				ChatManager.getInstance().sendMessage(plugin.getServer().getPlayer(event.getWhoClicked().getUniqueId()), Visibility.messageNoSwitch);
		}
	}

    public static ItemStack createItemStack(boolean toggleState) {
    	ItemStack itemStack = null;
    	LinkedList<String> itemLore = new LinkedList<String>();
    	DyeColor dyeColor = null;
    	
    	if (toggleState) {
    		if (Visibility.isDyeEnabled) {
    			Dye dye = new Dye();
    			
    			try {
    				dyeColor = DyeColor.valueOf(Visibility.dyeColorOn);
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid DyeColor type (for 'on' state)!");
    				dyeColor = DyeColor.LIME; // fallback
    			}
    			
    			dye.setColor(dyeColor);
    			itemStack = dye.toItemStack(1);
    		} else {
    			try {
    				itemStack = new ItemStack(Material.valueOf(Visibility.itemIdOn));
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid ItemStack type (for 'on' state)!");
    				itemStack = new ItemStack(Material.SLIME_BALL); // fallback
    			}
    		}
    		
    		ItemMeta itemMeta = itemStack.getItemMeta();
	  		itemLore.add(ChatColor.translateAlternateColorCodes('&', Visibility.itemLoreOn));
	  		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Visibility.itemNameOn));
	  		itemMeta.setLore(itemLore);
	  		itemStack.setItemMeta(itemMeta);
    	} else {
    		if (Visibility.isDyeEnabled) {
    			Dye dye = new Dye();
    			
    			try {
    				dyeColor = DyeColor.valueOf(Visibility.dyeColorOff);
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid DyeColor type (for 'off' state)!");
    				dyeColor = DyeColor.GRAY; // fallback
    			}
    			
    			dye.setColor(dyeColor);
    			itemStack = dye.toItemStack(1);
    		} else {
    			try {
    				itemStack = new ItemStack(Material.valueOf(Visibility.itemIdOff));
    			} catch (Exception exception) {
    				ChatManager.getInstance().log("&cWARNING: Config.yml contains invalid ItemStack type (for 'off' state)!");
    				itemStack = new ItemStack(Material.MAGMA_CREAM); // fallback
    			}
    		}
    		
    		ItemMeta itemMeta = itemStack.getItemMeta();
	  		itemLore.add(ChatColor.translateAlternateColorCodes('&', Visibility.itemLoreOff));
	  		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Visibility.itemNameOff));
	  		itemMeta.setLore(itemLore);
	  		itemStack.setItemMeta(itemMeta);
    	}
    	
		return itemStack;
    }
}
