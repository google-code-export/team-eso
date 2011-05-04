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

package com.epicsagaonline.bukkit.EpicZones;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.World;
import org.bukkit.entity.Player;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone.ZoneType;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZoneDAL;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class General {

	public static Map<String, EpicZone> myZones = new HashMap<String, EpicZone>();
	public static Map<String, EpicZone> myGlobalZones = new HashMap<String, EpicZone>();
	public static Map<String, EpicZonePlayer> myPlayers = new HashMap<String, EpicZonePlayer>();
	public static Config config;
	public static final String NO_PERM_ENTER = "You do not have permission to enter ";
	public static final String NO_PERM_BORDER = "You have reached the border of the map.";
	public static EpicZones plugin;

	private static final String ZONE_FILE = "zones.txt";
	//private static File myFile;

	public static EpicZonePlayer getPlayer(String name)
	{
		return myPlayers.get(name.toLowerCase());
	}

	public static void addPlayer(int entityID, String name)
	{
		myPlayers.put(name.toLowerCase(), new EpicZonePlayer(entityID, name));
	}

	public static void removePlayer(String playerName)
	{
		EpicZonePlayer ezp = myPlayers.get(playerName);
		if (ezp != null)
		{
			if(ezp.getMode() != EpicZoneMode.None)
			{
				if(ezp.getEditZone() != null)
				{
					ezp.getEditZone().HidePillars();
				}
			}
			myPlayers.remove(playerName);
		}
	}

	public static void LoadZones()
	{

		myZones.clear();

		if (loadZonesFromText())
		{
			Log.Write("Converting Zones.txt into new zone format...");
			SaveZones();
			File file = new File(plugin.getDataFolder() + File.separator + ZONE_FILE);
			file.renameTo(new File(plugin.getDataFolder() + File.separator + ZONE_FILE + ".old"));
			myZones.clear();
		}

		myZones = EpicZoneDAL.Load();

		reconsileGlobalZones();
		reconcileChildren();

	}

	public static boolean loadZonesFromText()
	{
		try
		{

			String line;
			File file = new File(plugin.getDataFolder() + File.separator + ZONE_FILE);

			if(file.exists())
			{
				try
				{

					Scanner scanner = new Scanner(file);
					myZones.clear();

					try {
						while(scanner.hasNext())
						{
							EpicZone newZone;
							line = scanner.nextLine().trim();
							if(line.startsWith("#") || line.isEmpty()){continue;}
							newZone = new EpicZone(line);;
							General.myZones.put(newZone.getTag(), newZone);
						}

					}
					finally {
						scanner.close();
					}
				}
				catch(Exception e)
				{
					Log.Write(e.getMessage());
				}

				reconcileChildren();

				return true;

			}

		}
		catch(Exception e)
		{
			Log.Write(e.getMessage());
		}
		return false;

	}

	public static void SaveZones()
	{
		for(String zoneTag : myZones.keySet())
		{
			EpicZoneDAL.Save(myZones.get(zoneTag));
		}
	}

	public static void reconsileGlobalZones()
	{
		for(String zoneTag : myZones.keySet())
		{
			EpicZone zone = myZones.get(zoneTag);
			if(zone.getType() == ZoneType.GLOBAL)
			{
				myGlobalZones.put(zone.getTag(), zone);
			}
		}

		for (World world : plugin.getServer().getWorlds())
		{
			if (myGlobalZones.get(world.getName().toLowerCase()) == null)
			{

				EpicZone newGlobal = new EpicZone();

				newGlobal.setTag(world.getName().toLowerCase());
				newGlobal.setName(world.getName());
				newGlobal.setRadius(1000);
				newGlobal.setType("GLOBAL");
				newGlobal.setMobs("all");
				newGlobal.setWorld(world.getName());

				for(String zoneTag : myZones.keySet())
				{
					EpicZone zone = myZones.get(zoneTag);
					if(zone.getWorld().equalsIgnoreCase(world.getName()))
					{
						newGlobal.addChild(zone);	
					}
				}

				myZones.put(newGlobal.getTag(), newGlobal);
				myGlobalZones.put(world.getName(), newGlobal);

				EpicZoneDAL.Save(myZones.get(newGlobal.getTag()));

				Log.Write("Global Zone Created For World [" + world.getName() + "]");

			}
			else
			{
				myGlobalZones.put(world.getName(), myGlobalZones.get(world.getName()));
			}
		}



	}

	private static void reconcileChildren()
	{

		ArrayList<String> badChildren = new ArrayList<String>();

		try
		{
			for(String zoneTag: myZones.keySet())
			{
				EpicZone zone = myZones.get(zoneTag);
				if(zone.hasChildren())
				{			
					for(String child: zone.getChildrenTags())
					{
						EpicZone childZone = myZones.get(child);
						if(childZone != null)
						{
							childZone.setParent(zone);
							zone.addChild(childZone);
						}
						else
						{
							Log.Write("The zone [" + zoneTag + "] has an invalid child > [" + child + "]");
							badChildren.add(child);
						}
					}
					if (badChildren.size() > 0)
					{
						for(String badChild: badChildren)
						{
							zone.removeChild(badChild);
						}
						badChildren = new ArrayList<String>();
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.Write(e.getMessage());
		}

	}

	public static EpicZone getZoneForPoint(int elevation, Point location, String worldName)
	{

		EpicZone result = null;
		String resultTag = "";
		for(String zoneTag: General.myZones.keySet())
		{
			EpicZone zone = General.myZones.get(zoneTag);
			if(zone.getWorld().equalsIgnoreCase(worldName))
			{
				resultTag = General.isPointInZone(zone, elevation, location, worldName);
				if(resultTag.length() > 0)
				{
					result = zone;
					break;
				}
			}
		}

		return result;

	}

	public static String isPointInZone(EpicZone zone, int playerHeight, Point playerPoint, String worldName)
	{

		String result = "";

		if(zone != null)
		{
			if(zone.hasChildren())
			{
				for(String zoneTag: zone.getChildren().keySet())
				{
					result = isPointInZone(zone.getChildren().get(zoneTag), playerHeight, playerPoint, worldName);
					if(result.length() > 0)
					{
						return result;
					}
				}
			}
			if(worldName.equalsIgnoreCase(zone.getWorld()))
			{
				if(playerHeight >= zone.getFloor() && playerHeight <= zone.getCeiling())
				{
					if(zone.pointWithin(playerPoint))
					{
						result = zone.getTag();
					}
				}
			}
		}

		return result;
	}

	public static boolean pointWithinBorder(Point point, Player player)
	{

		if(General.config.enableRadius)
		{

			EpicZonePlayer ezp = General.getPlayer(player.getName());
			EpicZone globalZone = myGlobalZones.get(player.getWorld().getName());

			double xsquared = point.x * point.x;
			double ysquared = point.y * point.y;
			double distanceFromCenter = Math.sqrt(xsquared + ysquared);

			ezp.setDistanceFromCenter((int)distanceFromCenter);

			if(globalZone != null)
			{
				if(distanceFromCenter <= globalZone.getRadius())
				{
					if(ezp.getPastBorder())
					{
						WarnPlayer(player, ezp, "You are inside the map radius border.");
						ezp.setPastBorder(false);
					}
					return true;
				}
				else
				{
					if(EpicZones.permissions.hasPermission(player, "epiczones.ignoremapradius"))
					{
						if(!ezp.getPastBorder())
						{
							WarnPlayer(player, ezp, "You are outside the map radius border.");
							ezp.setPastBorder(true);
						}
						return true;
					}
					else
					{
						return false;	
					}
				}
			}
			else //No border defined for the world in config.
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}

	public static void WarnPlayer(Player player, EpicZonePlayer ezp, String message)
	{
		if (ezp.getLastWarned().before(new Date()))
		{
			player.sendMessage(message);
			ezp.Warn();
		}
	}

	public static boolean ShouldCheckPlayer(EpicZonePlayer ezp)
	{
		boolean result = false;
		if(ezp!= null)
		{
			if (ezp.getLastCheck().before(new Date()))
			{
				result = true;
			}
		}
		return result;
	}

	public static boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
	}

}
