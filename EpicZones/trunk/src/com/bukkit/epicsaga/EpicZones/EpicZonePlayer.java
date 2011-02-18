package com.bukkit.epicsaga.EpicZones;

import org.bukkit.Location;
import java.util.Calendar;
import java.util.Date;

public class EpicZonePlayer {

	private EpicZone currentZone;
	private int entityID;
	private String name;
	private Location currentLocation;
	private Date lastWarned = new Date();
	private int distanceFromCenter;
	private boolean teleporting = false;
	private Date lastCheck = new Date();
	private EpicZoneMode mode = EpicZoneMode.None; 
	private EpicZone editZone = null;
	private boolean pastBorder = false;
	private Date enteredZone = new Date();
	private String previousZoneTag = "";
	
	public EpicZone getCurrentZone(){return currentZone;}
	public int getEntityID(){return entityID;}
	public String getName(){return name;}
	public Location getCurrentLocation(){return currentLocation;}
	public Date getLastWarned(){return lastWarned;}
	public Date getLastCheck(){return lastCheck;}
	public int getDistanceFromCenter(){return distanceFromCenter;}
	public boolean isTeleporting(){return teleporting;}
	public EpicZoneMode getMode(){return mode;}
	public EpicZone getEditZone(){return editZone;}
	public boolean getPastBorder(){return pastBorder;}
	public Date getEnteredZone(){return enteredZone;}
	public String getPreviousZoneTag(){return previousZoneTag;}
	
	public enum EpicZoneMode{None, ZoneDraw, ZoneEdit, ZoneDrawConfirm, ZoneDeleteConfirm}
	
	public void setPreviousZoneTag(String value)
	{
		previousZoneTag = value;
	}
	
	public void setPastBorder(boolean value)
	{
		this.pastBorder = value;
	}
	
	public void setEntityID(int value)
	{
		this.entityID = value;
	}
	
	public void setMode(EpicZoneMode value)
	{
		this.mode = value;
	}
	
	public void setEditZone(EpicZone value)
	{
		this.editZone = value;
	}
	
	public EpicZonePlayer(int entityID, String name)
	{
		this.entityID = entityID;
		this.name = name;
	}
	
	public void setCurrentZone(EpicZone z)
	{
		this.currentZone = z;
		this.enteredZone = new Date();
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
