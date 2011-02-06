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

		throw new AssertionError("An unknown player has been found");
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

	//	public static EpicZone getZone(String zoneName)
	//	{
	//
	//		EpicZone result = null;
	//
	//		if (myZones != null)
	//		{
	//			for(EpicZone ez: myZones)
	//			{
	//				if(zoneName.equalsIgnoreCase(ez.getName()))
	//				{
	//					result = ez;
	//					break;
	//				}
	//			}
	//		}
	//
	//		return result;
	//
	//	}

	public static boolean hasPermissions(Player player, EpicZone zone, String flag)
	{

		if(zone != null)
		{
			//System.out.println("Zone Tag: " + zone.getTag());
			//System.out.println("Has Parent: " + zone.hasParent());
			//System.out.println("Permission Check: " + "epiczones." + zone.getTag() + "." + flag);
			//System.out.println("Permission Result: " + EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag));
		}

		if(zone == null)
		{
			return getDefaultPerm(flag);
		}
		else if(EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag))
		{
			return true;
		}
		else if(zone.hasParent())
		{
			//System.out.println("Checking [" + zone.getName() + "] Parent [" + zone.getParent().getName() + "] Permissions. Result: " + hasPermissions(player, zone.getParent(), flag));
			return hasPermissions(player, zone.getParent(), flag);
		}
		else
		{
			return getDefaultPerm(flag);	
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

	//	private static boolean testPerms(EpicZonePlayer player, EpicZone zone, String flag)
	//	{
	//
	//		boolean result = getDefaultPerm(flag);
	//		String group = EpicZones.permissions.getGroup(player.getName());
	//		EpicZonePermission p;
	//
	//		p = zone.getPermission(group);
	//
	//		//System.out.println("Permissions: " + p.getPermissionObject());
	//
	//		if(p == null)
	//		{
	//			p = zone.getPermission(player.getName());
	//		}
	//
	//		if(p != null)
	//		{
	//
	//			//We know permissions are defined for the player, reset the result to false, so that if permissions are not granted, they can be denied.
	//			result = false;
	//
	//			Map<String,String> flags = p.getPermissionFlags();
	//
	//			//System.out.println("Flags: " + flags.toString());
	//			//System.out.println("Flag Checked: " + flag);
	//			if(flags.containsKey(flag) &&
	//					flags.get(flag).equalsIgnoreCase("allow"))
	//			{
	//				result = true;
	//				//System.out.println("Allowed!");
	//			}
	////			else if(p.getPermissionObject().equalsIgnoreCase(player.getName()))
	////			{
	////				result = false;
	////			}
	//		}
	//
	//		return result;
	//	}

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
				System.out.println("Attaching Child Zones To " + zone.getName() + "[" + zone.getTag() + "].");
				for(String child: zone.getChildrenTags())
				{
					EpicZone childZone = myZones.get(child);
					System.out.println("\t" + childZone.getName() + "[" + childZone.getTag() + "] added as a child of " + zone.getName() + "[" + zone.getTag() + "].");

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
			EpicZone z = General.myZones.get(zoneTag);
			resultTag = General.isPointInZone(z, playerHeight, playerPoint);
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
					break;
				}
			}
		}
		else
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
}