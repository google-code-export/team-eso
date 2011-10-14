/*

This file is part of EpicZones

Copyright (C) 2011 by Team ESO

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

/**
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.epicsagaonline.bukkit.EpicZones.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.integration.HeroChatIntegration;

import java.util.Calendar;
import java.util.Date;

public class EpicZonePlayer
{

	private EpicZone currentZone;
	private Player player;
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
	private boolean hasMoved = false;
	private Date lastMoved = new Date();
	private boolean admin = false;
	private SpoutPlayer spoutPlayer = null;
	public EpicZonePlayerUI UI = new EpicZonePlayerUI();

	public EpicZonePlayer(Player newplayer)
	{
		this.player = newplayer;
		this.entityID = newplayer.getEntityId();
		this.name = newplayer.getName();
		setCurrentLocation(newplayer.getWorld().getSpawnLocation());
		setCurrentZone(General.myGlobalZones.get(newplayer.getWorld().getName().toLowerCase()));
	}

	public EpicZonePlayer(String username)
	{
		this.player = null;
		this.entityID = -9999;
		this.name = username;
		this.admin = true;
		setCurrentLocation(General.plugin.getServer().getWorlds().get(0).getSpawnLocation());
		setCurrentZone(General.myGlobalZones.get(General.plugin.getServer().getWorlds().get(0).getName().toLowerCase()));
	}
	
	public SpoutPlayer getSpoutPlayer()
	{
		if (spoutPlayer == null)
		{
			spoutPlayer = SpoutManager.getPlayer(this.player);
		}
		return spoutPlayer;
	}

	public EpicZone getCurrentZone()
	{
		return currentZone;
	}

	public int getEntityID()
	{
		return entityID;
	}

	public String getName()
	{
		return name;
	}

	public Location getCurrentLocation()
	{
		return currentLocation;
	}

	public Date getLastWarned()
	{
		return lastWarned;
	}

	public Date getLastCheck()
	{
		return lastCheck;
	}

	public int getDistanceFromCenter()
	{
		return distanceFromCenter;
	}

	public boolean isTeleporting()
	{
		return teleporting;
	}

	public EpicZoneMode getMode()
	{
		return mode;
	}

	public EpicZone getEditZone()
	{
		return editZone;
	}

	public boolean getPastBorder()
	{
		return pastBorder;
	}

	public Date getEnteredZone()
	{
		return enteredZone;
	}

	public String getPreviousZoneTag()
	{
		return previousZoneTag;
	}

	public boolean getHasMoved()
	{
		return hasMoved;
	}

	public Date getLastMoved()
	{
		return lastMoved;
	}

	public boolean getAdmin()
	{
		return admin;
	}

	public enum EpicZoneMode
	{
		None, ZoneDraw, ZoneEdit, ZoneDrawConfirm, ZoneDeleteConfirm
	}

	public void setHasMoved(boolean value)
	{
		this.hasMoved = value;
	}

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

	public void setAdmin(boolean value)
	{
		this.admin = value;
	}

	public void setCurrentZone(EpicZone z)
	{
		if (this.currentZone != null)
		{
			HeroChatIntegration.leaveChat(this.currentZone.getTag(), this);
		}
		this.currentZone = z;
		this.enteredZone = new Date();
		HeroChatIntegration.joinChat(z.getTag(), this);
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
		this.lastMoved = cal.getTime();
	}

	public void setIsTeleporting(boolean value)
	{
		teleporting = value;
	}
}
