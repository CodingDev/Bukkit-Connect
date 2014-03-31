package de.CodingDev.BukkitConnect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import de.CodingDev.BukkitConnect.Events.BukkitConnectEvent;
import de.CodingDev.BukkitConnect.Events.BukkitConnectRequestType;

public class ServerHandler extends Thread{
	private BukkitConnect bukkitConnect;
	private ServerSocket socket;
	private boolean running;
	
	public ServerHandler(BukkitConnect bukkitConnect){
		this.bukkitConnect = bukkitConnect;
		try{
			socket = new ServerSocket(bukkitConnect.getConfig().getInt("port"));
			bukkitConnect.getLogger().info("Bukkit Connect has been enabled!");
		}catch(Exception e){
			bukkitConnect.getLogger().warning("Disable Bukkit Connect... Error Message: " + e);
		}
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}
	
	public void run(){
		while (running) {
		    try{
		    	//Vars for the Event
				Map<String, String> GETparameters = new HashMap<String, String>();
				Map<String, String> POSTparameters = new HashMap<String, String>();
				BukkitConnectRequestType requestType = BukkitConnectRequestType.UNKNOW;
				//Get Connections
		    	Socket connectionSocket = socket.accept();
			    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			    String firstLine = inFromClient.readLine();
			    //Decode Fist GET/POST line
			    if(firstLine != null){
			    	if(firstLine.startsWith("GET") || firstLine.startsWith("POST")){
			    		String splitFistLine[] = firstLine.split(" ");
			    		if(splitFistLine[0].equalsIgnoreCase("GET")){
			    			requestType = BukkitConnectRequestType.GET;
			    			GETparameters = decodeRecivedData(splitFistLine[1], requestType);
			    		}else if(splitFistLine[0].equalsIgnoreCase("POST")){
			    			requestType = BukkitConnectRequestType.POST;
			    			POSTparameters = decodeRecivedData(splitFistLine[1], requestType);
			    		}
					    //Fire Event and send Return
					    BukkitConnectEvent connectEvent = new BukkitConnectEvent(GETparameters, POSTparameters, requestType, connectionSocket);
					    bukkitConnect.getServer().getPluginManager().callEvent(connectEvent);
					    outToClient.writeBytes(connectEvent.getWebsiteReturnJsonObject().toJSONString()+'\n');
			    	}else{
				    	bukkitConnect.getLogger().warning("Can not handle Bukkit Connect Request... Error Message: The fist line dosent start with GET or POST.");
				    	outToClient.writeBytes("Can not handle Bukkit Connect Request... Error Message: The fist line dosent start with GET or POST.\n");
				    }
			    }else{
			    	bukkitConnect.getLogger().warning("Can not handle Bukkit Connect Request... Error Message: We cant read the fist data package.");
			    	outToClient.writeBytes("Can not handle Bukkit Connect Request... Error Message: The fist line dosent start with GET or POST.\n");
			    }
			    connectionSocket.close();
		    }catch(Exception e){
		    	bukkitConnect.getLogger().warning("Can not handle Bukkit Connect Request... Error Message: " + e);
		    	e.printStackTrace();
		    }
		}
	}

	private Map<String, String> decodeRecivedData(String data, BukkitConnectRequestType requestType) {
		Map<String, String> decodedData = new HashMap<String, String>();
		if(requestType == BukkitConnectRequestType.GET){
			String splitData[] = data.split("\\?");
			if(splitData.length > 1){
				for(String splitDataData : splitData[1].split("&")){
					if(splitDataData.contains("=")){
						String keyVal[] = splitDataData.split("=");
						decodedData.put(keyVal[0], keyVal[1]);
					}else{
						decodedData.put(splitDataData, null);
					}
				}
			}
		}
		return decodedData;
	}

	public void stopServer() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
