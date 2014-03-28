package de.CodingDev.BukkitConnect;

import java.awt.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import de.CodingDev.BukkitConnect.Events.BukkitConnectEvent;
import de.CodingDev.BukkitConnect.Events.BukkitConnectRequestType;

public class BukkitConnect extends JavaPlugin implements Listener{
	private ServerHandler serverHandler;
	public void onEnable(){
		configManager();
		serverHandler = new ServerHandler(this);
		serverHandler.setRunning(true);
		serverHandler.start();
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Bukkit Connect has been enabled.");
	}
 
	private void configManager() {
		getConfig().addDefault("port", 7363);
		getConfig().addDefault("allowShowPluginRequest", true);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void onDisable(){
		serverHandler.setRunning(false);
		serverHandler.stopServer();
		getLogger().info("Bukkit Connect has been disabled.");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void BukkitConnectEvent(BukkitConnectEvent e){
		if(e.getRequestType() == BukkitConnectRequestType.GET){
			if(e.getGETParameters().containsKey("method")){
				if(e.getGETParameters().get("method").equalsIgnoreCase("getServerInfos")){
					e.getWebsiteReturnJsonObject().put("serverVersion", getServer().getBukkitVersion());
					e.getWebsiteReturnJsonObject().put("minecraftVersion", getServer().getVersion());
					e.getWebsiteReturnJsonObject().put("players", getServer().getMaxPlayers());
					e.getWebsiteReturnJsonObject().put("maxPlayers", getServer().getOnlinePlayers().length);
					e.getWebsiteReturnJsonObject().put("motd", getServer().getMotd());
				}else if(e.getGETParameters().get("method").equalsIgnoreCase("getPlayers")){
					ArrayList playerList = new ArrayList();
					e.getWebsiteReturnJsonObject().put("maxPlayers", getServer().getOnlinePlayers().length);
					for(Player p : getServer().getOnlinePlayers()){
						playerList.add(p.getName());
					}
					e.getWebsiteReturnJsonObject().put("players", playerList);
				}else if(e.getGETParameters().get("method").equalsIgnoreCase("getPlayerExact")){
					if(e.getGETParameters().containsKey("username")){
						Player p = getServer().getPlayerExact(e.getGETParameters().get("username"));
						e.getWebsiteReturnJsonObject().put("name", p.getName());
						e.getWebsiteReturnJsonObject().put("health", p.getHealth());
						e.getWebsiteReturnJsonObject().put("foodLevel", p.getFoodLevel());
					}
				}else if(e.getGETParameters().get("method").equalsIgnoreCase("getPlugins")){
					if(getConfig().getBoolean("allowShowPluginRequest")){
						ArrayList pluginList = new ArrayList();
						e.getWebsiteReturnJsonObject().put("maxPlayers", getServer().getOnlinePlayers().length);
						for(Plugin p : getServer().getPluginManager().getPlugins()){
							pluginList.add(p.getName());
						}
						e.getWebsiteReturnJsonObject().put("plugins", pluginList);
					}
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("bukkitconnect")){
			
		}
		return true; 
	}
}
