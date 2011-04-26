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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.bukkit.entity.Player;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZoneDAL;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class General {

	public static Map<String, EpicZone> myZones = new HashMap<String, EpicZone>();
	public static ArrayList<String> myZoneTags = new ArrayList<String>();
	public static ArrayList<EpicZonePlayer> myPlayers = new ArrayList<EpicZonePlayer>();
	public static Config config;
	public static final String NO_PERM_ENTER = "You do not have permission to enter ";
	public static final String NO_PERM_BORDER = "You have reached the border of the map.";
	public static EpicZones plugin;

	private static final String ZONE_FILE = "zones.txt";
	//private static File myFile;

	public static EpicZonePlayer getPlayer(String name)
	{
		for(EpicZonePlayer ezp: myPlayers)
		{
			if(ezp.getName().equalsIgnoreCase(name))
				return ezp;
		}

		return null;

	}

	public static EpicZonePlayer getPlayer(int entityID)
	{
		for(EpicZonePlayer ezp: myPlayers)
		{
			if(ezp.getEntityID() == entityID)
				return ezp;
		}

		return null;

	}

	public static void addPlayer(int entityID, String name)
	{
		myPlayers.add(new EpicZonePlayer(entityID, name));
	}

	public static void removePlayer(int entityID)
	{
		int index = -1;

		for(int i = 0; i < myPlayers.size(); i++)
		{
			if(myPlayers.get(i).getEntityID() == entityID)
			{
				index = i;
				break;
			}
		}

		if (index > -1){myPlayers.remove(index);}

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
		
		reconsileZoneTags();
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
					myZoneTags.clear();

					try {
						while(scanner.hasNext())
						{
							EpicZone newZone;
							line = scanner.nextLine().trim();
							if(line.startsWith("#") || line.isEmpty()){continue;}
							newZone = new EpicZone(line);;
							General.myZones.put(newZone.getTag(), newZone);
							General.myZoneTags.add(newZone.getTag());
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
		for(String zoneTag : myZoneTags)
		{
			EpicZoneDAL.Save(myZones.get(zoneTag));
		}
	}

	private static void reconsileZoneTags()
	{
		myZoneTags.clear();
		Iterator<Entry<String, EpicZone>> it = myZones.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, EpicZone> pairs = (Entry<String, EpicZone>)it.next();
			myZoneTags.add(pairs.getKey());
		}
	}

	private static void reconcileChildren()
	{

		ArrayList<String> badChildren = new ArrayList<String>();

		try
		{
			for(String zoneTag: myZoneTags)
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

							myZones.remove(child);
							myZones.put(child, childZone);
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
				myZones.remove(zoneTag);
				myZones.put(zoneTag, zone);
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
		for(String zoneTag: General.myZoneTags)
		{
			EpicZone zone = General.myZones.get(zoneTag);
			if(zone.getWorld().equalsIgnoreCase(worldName))
			{
				resultTag = General.isPointInZone(zone, elevation, location, worldName);
				if(resultTag.length() > 0)
				{
					result = General.myZones.get(resultTag);
					break;
				}
			}
		}

		return result;

	}

	public static String isPointInZone(EpicZone zone, int playerHeight, Point playerPoint, String worldName)
	{

		String result = "";

		if(zone.hasChildren())
		{
			for(String zoneTag: zone.getChildrenTags())
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

		return result;
	}

	public static boolean pointWithinBorder(Point point, Player player)
	{

		if(General.config.enableRadius)
		{

			EpicZonePlayer ezp = General.getPlayer(player.getName());
			double xsquared = point.x * point.x;
			double ysquared = point.y * point.y;
			double distanceFromCenter = Math.sqrt(xsquared + ysquared);

			ezp.setDistanceFromCenter((int)distanceFromCenter);

			if(General.config.mapRadius.get(player.getWorld().getName()) != null)
			{
				if(distanceFromCenter <= General.config.mapRadius.get(player.getWorld().getName()))
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
		if (ezp.getLastCheck().before(new Date()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
	}

}
