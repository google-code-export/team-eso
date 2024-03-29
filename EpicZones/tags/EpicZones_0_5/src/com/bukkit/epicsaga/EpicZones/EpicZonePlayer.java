package com.bukkit.epicsaga.EpicZones;

import org.bukkit.Location;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EpicZonePlayer {

	private EpicZone currentZone;
	private int entityID;
	private String name;
	private Location currentLocation;
	private Date lastWarned = new Date();
	private int distanceFromCenter;
	private boolean teleporting = false;
	private Date lastCheck = new Date();
	
	public EpicZone getCurrentZone(){return currentZone;}
	public int getEntityID(){return entityID;}
	public String getName(){return name;}
	public Location getCurrentLocation(){return currentLocation;}
	public Date getLastWarned(){return lastWarned;}
	public Date getLastCheck(){return lastCheck;}
	public int getDistanceFromCenter(){return distanceFromCenter;}
	public boolean isTeleporting(){return teleporting;}
	
	public void setEntityID(int value)
	{
		this.entityID = value;
	}
	
	public EpicZonePlayer(int entityID, String name)
	{
		this.entityID = entityID;
		this.name = name;
	}
	
	public void setCurrentZone(EpicZone z)
	{
		this.currentZone = z;
	}
	
	public void setDistanceFromCenter(int distance)
	{
		this.distanceFromCenter = distance;
	}
	
	public void setCurrentLocation(Location l)
	{
		this.currentLocation = l;
	}
	
	public void Warn()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 2);
		this.lastWarned = cal.getTime();
	}
	
	public void Check()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, 500);
		this.lastCheck = cal.getTime();
	}
	
	public void setIsTeleporting(boolean value)
	{
		teleporting = value;
	}
}
