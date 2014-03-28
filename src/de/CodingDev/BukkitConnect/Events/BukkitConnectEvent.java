package de.CodingDev.BukkitConnect.Events;

import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;

public final class BukkitConnectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Map<String, String> GETparameters, POSTparameters;
    private JSONObject jsonObject = new JSONObject();
    private BukkitConnectRequestType requestType;
 
    public BukkitConnectEvent(Map<String, String> GETparameters, Map<String, String> POSTparameters, BukkitConnectRequestType requestType) {
    	this.GETparameters = GETparameters;
    	this.POSTparameters = POSTparameters;
    	this.requestType = requestType;
    }
 
    public Map<String, String> getGETParameters() {
        return GETparameters;
    }
    
    public Map<String, String> getPOSTParameters() {
        return POSTparameters;
    }
    
    public BukkitConnectRequestType getRequestType(){
    	return requestType;
    }
    
    public JSONObject getWebsiteReturnJsonObject(){
    	return jsonObject;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
