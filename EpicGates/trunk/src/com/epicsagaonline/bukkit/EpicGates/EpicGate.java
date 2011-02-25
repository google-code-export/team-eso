package com.epicsagaonline.bukkit.EpicGates;

import org.bukkit.Location;

public class EpicGate {

	private String tag = "";
	private Location location = null;
	private String targetTag = "";
	private EpicGate target = null;
	
	public EpicGate(String data)
	{
		
		String[] split = data.split(",");
		
		this.tag = split[0].trim();
		
		if(IsNumeric(split[1]))
		{
			this.location = new Location(General.plugin.getServer().getWorlds().get(0), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()), Integer.parseInt(split[3].trim()));
		}
		else
		{
			this.location = new Location(General.plugin.getServer().getWorld(split[1].trim()), Integer.parseInt(split[2].trim()), Integer.parseInt(split[3].trim()), Integer.parseInt(split[4].trim()));
		}
		this.targetTag=split[5].trim();
	}
	
	public String getTag(){return tag;}
	public Location getLocation(){return location;}
	public String getTargetTag(){return targetTag;}
	public EpicGate getTarget(){return target;}
	
	public void setTarget(EpicGate value)
	{
		this.target = value;
	}
	
	private static boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
	}
	
}
