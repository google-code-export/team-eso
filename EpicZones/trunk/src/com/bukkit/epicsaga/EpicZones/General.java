package com.bukkit.epicsaga.EpicZones;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.bukkit.epicsaga.EpicZones.EpicZone;
import com.bukkit.epicsaga.EpicZones.EpicZonePermission;
import com.bukkit.epicsaga.EpicZones.EpicZonePlayer;
import com.bukkit.epicsaga.EpicZones.EpicZones;
import com.bukkit.epicsaga.EpicZones.General;

public class General {

	public static Map<String, EpicZone> myZones = new HashMap<String, EpicZone>();
	public static ArrayList<String> myZoneTags = new ArrayList<String>();
	public static ArrayList<EpicZonePlayer> myPlayers = new ArrayList<EpicZonePlayer>();
	private static final String ZONE_FILE = "zones.txt";
	private static File myFile;
	public static EpicZonesConfig config;

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

	public static boolean hasPermissions(Player player, EpicZone zone, String flag)
	{

//		if(zone != null)
//		{
//		System.out.println("Zone Tag: " + zone.getTag());
//		System.out.println("Has Parent: " + zone.hasParent());
//		System.out.println("Permission Allow Check: " + "epiczones." + zone.getTag() + "." + flag);
//		System.out.println("Permission Deny Check: " + "epiczones." + zone.getTag() + "." + flag + ".deny");
//		System.out.println("Permission Allow Result: " + EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag));
//		System.out.println("Permission Deny Result: " + EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny"));
//		System.out.println("Permission Composite Result: " + (EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag) && !EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny")));
//		System.out.println("Player Can Ignore Permissions: " + EpicZones.permissions.has(player, "epiczones.ignorepermissions"));
//		}

		if(!EpicZones.permissions.has(player, "epiczones.ignorepermissions"))
		{
			if(zone == null)
			{
				//System.out.println("1");
				return getDefaultPerm(flag);
			}
			else if(EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag) && !EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny"))
			{
				//System.out.println("2");
				return true;
			}
			else if(zone.hasParent())
			{
				//System.out.println("3");
				return hasPermissions(player, zone.getParent(), flag);
			}
			else if(!EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny"))
			{
				//System.out.println("4");
				return getDefaultPerm(flag);	
			}
			else
			{
				//System.out.println("5");	
				return false;
			}
		}
		else
		{
			//System.out.println("6");
			return true;
		}
	}

	private static boolean getDefaultPerm(String flag)
	{
		if(flag.equals("entry"))
			return config.defaultEnter;
		if(flag.equals("destroy"))
			return config.defaultDestroy;
		if(flag.equals("build"))
			return config.defaultBuild;

		return false;
	}

	public static void loadZones(File path) throws FileNotFoundException
	{
		String line;
		if (path != null){
			File file = new File(path + File.separator + ZONE_FILE);
			myFile = file;
		}

		Scanner scanner = new Scanner(myFile);
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

		reconcileChildren();

	}

	private static void reconcileChildren()
	{

		for(String zoneTag: myZoneTags)
		{
			EpicZone zone = myZones.get(zoneTag);
			if(zone.hasChildren())
			{
				//System.out.println("Attaching Child Zones To " + zone.getName() + "[" + zone.getTag() + "].");
				for(String child: zone.getChildrenTags())
				{
					EpicZone childZone = myZones.get(child);
					//System.out.println("\t" + childZone.getName() + "[" + childZone.getTag() + "] added as a child of " + zone.getName() + "[" + zone.getTag() + "].");

					childZone.setParent(zone);
					zone.addChild(childZone);

					myZones.remove(child);
					myZones.put(child, childZone);
				}
			}
			myZones.remove(zoneTag);
			myZones.put(zoneTag, zone);
		}

	}

	public static EpicZone getZoneForPoint(Player player,	EpicZonePlayer ezp, int playerHeight, Point playerPoint)
	{

		EpicZone result = null;
		String resultTag = "";

		for(String zoneTag: General.myZoneTags)
		{
			EpicZone zone = General.myZones.get(zoneTag);
			resultTag = General.isPointInZone(zone, playerHeight, playerPoint);
			if(resultTag.length() > 0)
			{
				result = General.myZones.get(resultTag);
				break;
			}
		}

		return result;

	}

	public static String isPointInZone(EpicZone zone, int playerHeight, Point playerPoint)
	{

		String result = "";

		if(zone.hasChildren())
		{
			for(String zoneTag: zone.getChildrenTags())
			{
				result = isPointInZone(zone.getChildren().get(zoneTag), playerHeight, playerPoint);
				if(result.length() > 0)
				{
					return result;
				}
			}
		}

		if(playerHeight >= zone.getFloor() && playerHeight <= zone.getCeiling())
		{
			if(zone.pointWithin(playerPoint))
			{
				result = zone.getTag();
			}
		}

		return result;
	}
}