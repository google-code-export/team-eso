package com.epicsagaonline.bukkit.EpicZones;

import java.awt.Point;
import java.awt.Polygon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.bukkit.entity.Player;
import com.epicsagaonline.bukkit.EpicZones.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;

public class General {

	public static Map<String, EpicZone> myZones = new HashMap<String, EpicZone>();
	public static ArrayList<String> myZoneTags = new ArrayList<String>();
	public static ArrayList<EpicZonePlayer> myPlayers = new ArrayList<EpicZonePlayer>();
	private static final String ZONE_FILE = "zones.txt";
	private static File myFile;
	public static EpicZonesConfig config;
	public static final String NO_PERM_ENTER = "You do not have permission to enter ";
	public static final String NO_PERM_BORDER = "You have reached the border of the map.";
	
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

//				if(zone != null)
//				{
//				System.out.println("Zone Tag: " + zone.getTag());
//				System.out.println("Has Parent: " + zone.hasParent());
//				System.out.println("Permission Allow Check: " + "epiczones." + zone.getTag() + "." + flag);
//				System.out.println("Permission Deny Check: " + "epiczones." + zone.getTag() + "." + flag + ".deny");
//				System.out.println("Permission Allow Result: " + EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag));
//				System.out.println("Permission Deny Result: " + EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny"));
//				System.out.println("Permission Composite Result: " + (EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag) && !EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny")));
//				System.out.println("Player Can Ignore Permissions: " + EpicZones.permissions.has(player, "epiczones.ignorepermissions"));
//				}

		if(!EpicZones.permissions.has(player, "epiczones.ignorepermissions"))
		{
			if(zone == null)
			{
				//System.out.println("1");
				return getDefaultPerm(flag);
			}
			else if(EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag + ".deny"))
			{
				//System.out.println("1.5");
				return false;
			}
			else if(EpicZones.permissions.has(player, "epiczones." + zone.getTag() + "." + flag))
			{
				//System.out.println("2");
				return true;
			}
			else if(zone.hasParent())
			{
				//System.out.println("3");
				return hasPermissions(player, zone.getParent(), flag);
			}
			else
			{
				//System.out.println("4");
				return getDefaultPerm(flag);	
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

	public static void loadZones(File path)
	{
		String line;
		if (path != null){
			File file = new File(path + File.separator + ZONE_FILE);
			myFile = file;
		}

		try
		{
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
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
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

	public static void SaveZones()
	{
		try 
		{
			String data = BuildZoneData();
			Writer output = new BufferedWriter(new FileWriter(myFile, false));
			try 
			{
				output.write(data);
			}
			finally {
				output.close();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static String BuildZoneData()
	{
		String result = "#Zone Tag|World|Zone Name|Flags|Enter Message|Exit Message|Floor|Ceiling|Child Zones|PointList\n";
		String line = "";

		for(String tag: myZoneTags)
		{
			EpicZone z = myZones.get(tag);
			line = z.getTag() + "|";
			line = line + z.getWorld() + "|";
			line = line + z.getName() + "|";
			line = line + BuildFlags(z) + "|";
			line = line + z.getEnterText() + "|";
			line = line + z.getExitText() + "|";
			line = line + z.getFloor() + "|";
			line = line + z.getCeiling() + "|";
			line = line + BuildChildren(z) + "|";
			line = line + BuildPointList(z) + "\n";
			result = result + line;
		}

		return result;
	}

	private static String BuildFlags(EpicZone z)
	{
		String result = "";

		if(z.hasPVP())
			{result = result + "pvp:true ";}
		else
			{result = result + "pvp:false ";}
		
		//if(z.getFlags().get("nomobs") != null){result = result + "nomobs:" + z.getFlags().get("nomobs").toString() + " ";}
		
		if(z.hasRegen())
		{
			if(z.getRegenDelay() > 0)
			{
				result = result + "regen:" + z.getRegenInterval() + ":" + z.getRegenAmount() + ":" + z.getRegenDelay() + " ";
			}
			else
			{
				result = result + "regen:" + z.getRegenInterval() + ":" + z.getRegenAmount() + " ";	
			}
		}
		
		//if(z.getFlags().get("noanimals") != null){result = result + "noanimals:" + z.getFlags().get("noanimals").toString() + " ";}

		return result;
	}

	private static String BuildChildren(EpicZone z)
	{
		String result = "";

		for(String tag: z.getChildrenTags())
		{
			result = result + tag + " ";
		}

		return result;
	}

	private static String BuildPointList(EpicZone z)
	{

		String result = "";
		Polygon poly = z.getPolygon();

		if(poly.npoints <= 1)
		{
			result = z.getCenter().x + ":" + z.getCenter().y + " " + z.getRadius();
		}
		else
		{
			for(int i = 0; i < poly.npoints; i++)
			{
				result = result + poly.xpoints[i] + ":" + poly.ypoints[i] + " ";
			}
		}
		
		return result;
	}

	public static EpicZone getZoneForPoint(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint, String worldName)
	{

		EpicZone result = null;
		String resultTag = "";
		for(String zoneTag: General.myZoneTags)
		{
			EpicZone zone = General.myZones.get(zoneTag);
			resultTag = General.isPointInZone(zone, playerHeight, playerPoint, worldName);
			if(resultTag.length() > 0)
			{
				result = General.myZones.get(resultTag);
				break;
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

			if(distanceFromCenter <= General.config.mapRadius)
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
				if(EpicZones.permissions.has(player, "epiczones.ignoremapradius"))
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
	
	public static void Regen()
	{
		
	}
	
}
