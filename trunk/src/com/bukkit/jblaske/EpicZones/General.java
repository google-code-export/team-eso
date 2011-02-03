package com.bukkit.jblaske.EpicZones;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Scanner;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.bukkit.jblaske.EpicZones.EpicZone;
import com.bukkit.jblaske.EpicZones.EpicZonePermission;
import com.bukkit.jblaske.EpicZones.EpicZonePlayer;
import com.bukkit.jblaske.EpicZones.EpicZones;

public class General {

	public static ArrayList<EpicZone> myZones = new ArrayList<EpicZone>();
	public static ArrayList<EpicZonePlayer> myPlayers = new ArrayList<EpicZonePlayer>();
	public static PermissionHandler Perms;
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

	public static EpicZone getZone(String zoneName)
	{

		EpicZone result = null;

		if (myZones != null)
		{
			for(EpicZone ez: myZones)
			{
				if(zoneName.equalsIgnoreCase(ez.getName()))
				{
					result = ez;
					break;
				}
			}
		}

		return result;

	}

	public static boolean hasPermissions(EpicZonePlayer player, EpicZone zone, String flag)
	{

		boolean result = false;

		result = testPerms(player, zone, flag);

		if(result == false && zone.getParent() != null)
		{
			result = testPerms(player, zone.getParent(), flag);
		}

		return result;

	}

	private static boolean getDefaultPerm(String flag)
	{
		if(flag == "entry")
			return config.defaultEnter;
		if(flag == "destroy")
			return config.defaultDestroy;
		if(flag == "build")
			return config.defaultBuild;

		return false;
	}

	private static boolean testPerms(EpicZonePlayer player, EpicZone zone, String flag)
	{

		boolean result = getDefaultPerm(flag);
		String group = EpicZones.permissions.getGroup(player.getName());
		EpicZonePermission p;

		p = zone.getPermission(group);

		if(p == null)
		{
			p = zone.getPermission(player.getName());
		}

		if(p != null)
		{
			
			//We know permissions are defined for the player, reset the result to false, so that if permissions are not granted, they can be denied.
			result = false;
			
			Map<String,String> flags = p.getPermissionFlags();
			
			if(flags.containsKey(flag) &&
					flags.get(flag).equalsIgnoreCase("allow"))
			{
				result = true;
			}
//			else if(p.getPermissionObject().equalsIgnoreCase(player.getName()))
//			{
//				result = false;
//			}
		}

		return result;
	}

	 public static void loadZones(File path)
	 {
	        String line;
	        if (path != null){
	        	File file = new File(path + File.separator + ZONE_FILE);
	        	myFile = file;
	        }

	        try {
	            Scanner scanner = new Scanner(myFile);
	            myZones.clear();
	           try {
	                while(scanner.hasNext())
	                {
	                    line = scanner.nextLine().trim();
	                    if(line.startsWith("#") || line.isEmpty()){continue;}
	                    General.myZones.add(new EpicZone(line));
	                }
	            }
	            finally {
	                scanner.close();
	            }
	        }
	        catch(IOException e) {
	            e.printStackTrace();
	        }
	    }
}
