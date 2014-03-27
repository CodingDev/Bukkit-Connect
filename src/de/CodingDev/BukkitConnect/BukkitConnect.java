package de.CodingDev.BukkitConnect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitConnect extends JavaPlugin{
	public void onEnable(){
		this.getLogger().info("Bukkit Connect has been enabled!");
	}
 
	public void onDisable(){
		this.getLogger().info("Bukkit Connect has been disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("bukkitconnect")){
			
		}
		return true; 
	}
}
