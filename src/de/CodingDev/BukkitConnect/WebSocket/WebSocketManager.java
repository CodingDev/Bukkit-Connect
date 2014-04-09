package de.CodingDev.BukkitConnect.WebSocket;

import de.CodingDev.BukkitConnect.BukkitConnect;
import org.webbitserver.*;

public class WebSocketManager {
	private BukkitConnect bukkitConnect;
	private WebServer webServer;
	
	public WebSocketManager(BukkitConnect bukkitConnect){
		this.bukkitConnect = bukkitConnect;
		WebServer webServer = WebServers.createWebServer(8080);
	    registerChannel("/static-files", new StaticFileHandler());
	    webServer.start();
	}
	
	public void registerChannel(String channel, HttpHandler handler){
		webServer.add(channel, handler);
	}
}
