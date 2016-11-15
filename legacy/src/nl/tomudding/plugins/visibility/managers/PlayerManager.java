package nl.tomudding.plugins.visibility.managers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import nl.tomudding.plugins.visibility.Visibility;

public class PlayerManager {
	static PlayerManager instance = new PlayerManager();
	private Visibility p;
  
	FileConfiguration data;
	File dfile;

	public static PlayerManager getInstance() {
		return instance;
	}

	public Plugin getPlugin() {
		return this.p;
	}

	public void setup(Visibility p) {
		this.dfile = new File(p.getDataFolder(), "data.yml");

		if (!this.dfile.exists()) {
			ChatManager.getInstance().log("&cWARNING: data.yml does not exist.");
			ChatManager.getInstance().log("Creating data.yml.");
			try {
				this.dfile.createNewFile();
				ChatManager.getInstance().log("&aSuccessfully created data.yml.");
			} catch (IOException e) {
				ChatManager.getInstance().log("&cWARNING: Could not create data.yml.");
			}
		}
		this.data = YamlConfiguration.loadConfiguration(this.dfile);
	}

	public FileConfiguration getData() {
		return this.data;
	}

	public void saveData() {
		try {
			this.data.save(this.dfile);
		} catch (IOException e) {
			ChatManager.getInstance().log("&cWARNING: Could not save data.yml!");
		}
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(this.dfile);
	}
	
	public boolean getToggleState(UUID uuid) {
		String playerData = uuid.toString();
		return data.getBoolean(playerData);
	}
  
	public boolean checkIfExists(UUID uuid) {
		String UUID = uuid.toString();
		if(data.contains(UUID)) {
			return true;
		} else {
		  	return false;
		}
	}
  
	public void setToggle(UUID uuid, Boolean state) {
		String p = uuid.toString();
		data.set(p, state);
		saveData();
	}
}
