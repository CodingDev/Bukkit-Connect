package de.CodingDev.BukkitConnect;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.CodingDev.BukkitConnect.Events.BukkitConnectEvent;
import de.CodingDev.BukkitConnect.Events.BukkitConnectRequestType;
import de.CodingDev.BukkitConnect.Metrics.Metrics;
import de.CodingDev.BukkitConnect.UpdateChecker.Updater;
import de.CodingDev.BukkitConnect.UpdateChecker.Updater.UpdateResult;
import de.CodingDev.BukkitConnect.UpdateChecker.Updater.UpdateType;
import de.CodingDev.BukkitConnect.WebSocket.WebSocketManager;

public class BukkitConnect extends JavaPlugin implements Listener{
	private ServerHandler serverHandler;
	private boolean newVersion = false;
	private String prefix = "&6[Bukkit Connect] ";
	private String newVersionName = "";
	private WebSocketManager wsm;
	
	public void onEnable(){
		configManager();
		serverHandler = new ServerHandler(this);
		serverHandler.setRunning(true);
		serverHandler.start();
		wsm = new WebSocketManager(this);
		getServer().getPluginManager().registerEvents(this, this);
		
		//Updater
		Updater updater = new Updater(this, 76974, this.getFile(), UpdateType.NO_DOWNLOAD, true);
		if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
		    getLogger().info("New version available! " + updater.getLatestName());
		    newVersion = true;
		    newVersionName = updater.getLatestName();
		}else if (updater.getResult() == UpdateResult.NO_UPDATE) {
		    getLogger().info("No new version available");
		}else{
		    getLogger().info("Updater: " + updater.getResult());
		}
		//Metrics
		try{
			Metrics metrics = new Metrics(this);
			metrics.start();
		}catch (IOException localIOException) {}
		getLogger().info("Bukkit Connect has been enabled.");
	}
	
	public WebSocketManager getWebSocketManager(){
		return wsm;
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
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(newVersion){
			e.getPlayer().sendMessage(prefix + "A new Version is available! (&c" + newVersionName + "&6)");
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void BukkitConnectEvent(BukkitConnectEvent e){
		if(e.getRequestType() == BukkitConnectRequestType.GET){
			if(e.getGETParameters().containsKey("method")){
				if(e.getGETParameters().get("method").equalsIgnoreCase("getServerInfos")){
					e.getWebsiteReturnJsonObject().put("getBukkitVersion", getServer().getBukkitVersion());
					e.getWebsiteReturnJsonObject().put("getVersion", getServer().getVersion());
					e.getWebsiteReturnJsonObject().put("getMaxPlayers", getServer().getMaxPlayers());
					e.getWebsiteReturnJsonObject().put("getOnlinePlayers", getServer().getOnlinePlayers().length);
					e.getWebsiteReturnJsonObject().put("getMotd", getServer().getMotd());
				}else if(e.getGETParameters().get("method").equalsIgnoreCase("getOnlinePlayers")){
					ArrayList playerList = new ArrayList();
					e.getWebsiteReturnJsonObject().put("getMaxPlayers", getServer().getOnlinePlayers().length);
					for(Player p : getServer().getOnlinePlayers()){
						playerList.add(p.getName());
					}
					e.getWebsiteReturnJsonObject().put("getOnlinePlayers", playerList);
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
