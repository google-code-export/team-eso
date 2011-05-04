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

package com.epicsagaonline.bukkit.EpicZones.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.Log;

public class EpicZoneDAL{
	private static final Yaml yaml;
	private static final String PATH = "Zones"; 
	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	public static Map<String, EpicZone> Load() 
	{

		File file = new File(General.plugin.getDataFolder() + File.separator + PATH);
		Map<String, EpicZone> result = new HashMap<String, EpicZone>();

		if(!file.exists())
		{
			file.mkdir();
		}


		String fileNames[] = file.list();

		for(int i = 0; i < fileNames.length; i++)
		{
			EpicZone zone = Load(new File(file.getAbsolutePath() + File.separator + fileNames[i]));
			result.put(zone.getTag(), zone);
		}

		return result;

	}

	public static void ReloadZone(String zoneTag)
	{
		File file = new File(General.plugin.getDataFolder() + File.separator + PATH  + File.separator + zoneTag);
		if(file.exists())
		{
			General.myZones.put(zoneTag, Load(file));
		}
	}
	
	private static EpicZone Load(File file)
	{

		Map<String, ConfigurationNode> nodes;
		List<Object> list;
		String tag = file.getName().substring(0, file.getName().indexOf("."));		
		EpicZone result = new EpicZone();
		Configuration config = new Configuration(file);

		config.load();
		result.setTag(tag);
		result.setName(config.getString("name"));
		result.setType(config.getString("type"));
		result.setRadius(config.getInt("radius", 0));
		result.setWorld(config.getString("world"));
		result.setEnterText(config.getString("entertext"));
		result.setExitText(config.getString("exittext"));
		result.setFloor(config.getInt("floor", 0));
		result.setCeiling(config.getInt("ceiling", 128));
		result.setPVP(config.getBoolean("pvp", false));
		result.setFire(config.getBoolean("fire", false));
		result.setExplode(config.getBoolean("explode", false));
		result.setSanctuary(config.getBoolean("sanctuary", false));
		result.setFireBurnsMobs(config.getBoolean("fireburnsmobs", true));
		result.setPolygon(config.getString("points"));
		result.setRegen(getRegen(config));

		
		list = config.getList("mobs");
		if(list != null)
		{
			for(Object mob: list)
			{
				if(mob instanceof String)
				{
					result.addMob((String)mob);
				}
			}
		}
		
		list = config.getList("owners");
		if(list != null)
		{
			for(Object playerName: list)
			{
				if(playerName instanceof String)
				{
					result.addOwner((String)playerName);
				}
			}
		}

		list = config.getList("childzones");
		if(list != null)
		{
			for(Object zoneName: list)
			{
				if(zoneName instanceof String)
				{
					result.addChildTag((String)zoneName);
				}
			}
		}

		nodes = config.getNodes("permissions");
		if(nodes != null)
		{
			ConfigurationNode innerNodes;
			for(String nodeName: nodes.keySet())
			{
				innerNodes = nodes.get(nodeName);
				if(innerNodes.getString("build") != null);
				{
					result.addPermission(
							nodeName, 
							"BUILD", 
							innerNodes.getString("build"));
				}

				if(innerNodes.getString("destroy") != null);
				{
					result.addPermission(
							nodeName, 
							"DESTROY", 
							innerNodes.getString("destroy"));
				}

				if(innerNodes.getString("entry") != null);
				{
					result.addPermission(
							nodeName, 
							"ENTRY", 
							innerNodes.getString("entry"));
				}
			}
		}

		result.rebuildBoundingBox();

		Log.Write("Loaded " + result.getType().toString() + " Zone [" + result.getName() + "]");

		return result;

	}


	private static String getRegen(Configuration config)
	{
		String result = "";

		result += config.getString("regen.amount") + ":";
		result += config.getString("regen.delay") + ":";
		result += config.getString("regen.interval") + ":";
		result += config.getString("regen.maxregen") + ":";
		result += config.getString("regen.mindegen") + ":";
		result += config.getString("regen.restdelay") + ":";
		result += config.getString("regen.bedbonus");

		return result;
	}

	public static boolean Save(EpicZone zone)
	{

		if(zone != null && zone.getTag().length() > 0)
		{
			FileOutputStream stream;
			BufferedWriter writer;
			File file = new File(General.plugin.getDataFolder() + File.separator + PATH + File.separator + zone.getTag() + ".yml");
			Map<String, Object> config = new TreeMap<String, Object>();

			config.put("name", zone.getName());
			config.put("type", zone.getType().toString());
			config.put("radius", zone.getRadius());
			config.put("world", zone.getWorld());
			config.put("entertext", zone.getEnterText());
			config.put("exittext", zone.getExitText());
			config.put("floor", zone.getFloor());
			config.put("ceiling", zone.getCeiling());
			config.put("pvp", zone.getPVP());
			config.put("mobs", zone.getMobs().toArray());
			config.put("fire", zone.getFire());
			config.put("explode", zone.getExplode());
			config.put("sanctuary", zone.getSanctuary());
			config.put("fireburnsmobs", zone.getFireBurnsMobs());

			Map<String, Object> regen = new TreeMap<String, Object>();
			regen.put("amount", zone.getRegen().getAmount()); 
			regen.put("delay", zone.getRegen().getDelay()); 
			regen.put("interval", zone.getRegen().getInterval()); 
			regen.put("maxregen", zone.getRegen().getMaxRegen()); 
			regen.put("mindegen", zone.getRegen().getMinDegen()); 
			regen.put("restdelay", zone.getRegen().getRestDelay());
			regen.put("bedbonus", zone.getRegen().getBedBonus());
			config.put("regen", regen);

			config.put("owners", zone.getOwners());
			config.put("childzones", zone.getChildrenTags().toArray());
			config.put("points", zone.getPoints());
			config.put("permissions", BuildPerms(zone.getPermissions()));

			try 
			{
				if(!file.exists())
				{
					File dir = new File(General.plugin.getDataFolder() + File.separator + PATH);
					if(!dir.exists())
					{
						dir.mkdir();
					}
					file.createNewFile();
				}

				stream = new FileOutputStream(file);
				stream.getChannel().truncate(0);
				writer = new BufferedWriter(new OutputStreamWriter(stream));

				try
				{
					writer.write(yaml.dump(config));
				}
				finally 
				{
					writer.close();
				}

			}
			catch(IOException e)
			{
				Log.Write(e.getMessage());
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Map<String, Object> BuildPerms(ArrayList<EpicZonePermission> perms)
	{
		Map<String, Object> mainMap = new HashMap<String, Object>();
		ArrayList<String> members = new ArrayList<String>();

		for(EpicZonePermission perm : perms)
		{
			if(!members.contains(perm.getMember().toLowerCase()))
			{
				members.add(perm.getMember().toLowerCase());
			}
		}

		for(String memberName : members)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			for(EpicZonePermission perm : perms)
			{
				if(memberName.equals(perm.getMember().toLowerCase()))
				{
					map.put(perm.getNode().toString().toLowerCase(), perm.getPermission().toString().toLowerCase());
				}
			}
			if(map.size() > 0)
			{
				mainMap.put(memberName, map);
			}
		}

		return mainMap;
	}

	public static void DeleteZone(String zoneTag)
	{
		EpicZone zone = General.myZones.get(zoneTag);
		if(zone != null)
		{
			File file = new File(General.plugin.getDataFolder() + File.separator + PATH + File.separator + zone.getTag() + ".yml");
			if (file.exists())
			{
				if(file.delete())
				{
					General.LoadZones();
				}
			}
		}
	}
}
