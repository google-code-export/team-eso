package com.epicsagaonline.bukkit.ChallengeMaps;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.GameStateData;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.google.common.io.Files;

public class Util
{
	public static boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static String GetStringFromLocation(Location loc)
	{
		String result = "";
		if (loc != null)
		{
			result = loc.getWorld().getName() + ":";
			result = result + loc.getBlockX() + ":";
			result = result + loc.getBlockY() + ":";
			result = result + loc.getBlockZ() + ":";
		}
		return result;
	}

	public static Location GetLocationFromString(String loc)
	{
		Location result = null;

		String[] split = loc.split(":");
		if (split.length == 4)
		{
			World world = Current.Plugin.getServer().getWorld(split[0]);
			result = new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
		}

		return result;
	}

	public static String getStringValueFromHashSet(String key, HashMap<String, Object> data)
	{
		String result = "";
		Object temp = getObjectValueFromHashSet(key, data);
		if (temp != null)
		{
			result = temp.toString();
		}
		return result;
	}

	public static Integer getIntegerValueFromHashSet(String key, HashMap<String, Object> data)
	{
		return getIntegerValueFromHashSet(key, data, 0);
	}

	public static Integer getIntegerValueFromHashSet(String key, HashMap<String, Object> data, Integer defaultValue)
	{
		Integer result = defaultValue;
		Object temp = getObjectValueFromHashSet(key, data);
		if (temp != null)
		{
			if (IsNumeric(temp.toString()))
			{
				result = Integer.valueOf(temp.toString());
			}
		}
		return result;
	}

	public static Boolean getBooleanValueFromHashSet(String key, HashMap<String, Object> data)
	{
		return getBooleanValueFromHashSet(key, data, false);
	}

	public static Boolean getBooleanValueFromHashSet(String key, HashMap<String, Object> data, Boolean defaultValue)
	{
		Boolean result = defaultValue;
		Object temp = getObjectValueFromHashSet(key, data);
		if (temp != null)
		{
			result = Boolean.valueOf(temp.toString());
		}
		return result;
	}

	public static Object getObjectValueFromHashSet(String key, HashMap<String, Object> data)
	{
		Object result = null;
		if (data != null)
		{
			result = data.get(key);
			if (result == null)
			{
				result = data.get(key.toLowerCase());
				if (result == null)
				{
					result = data.get(key.toUpperCase());
				}
			}
		}
		// Log.Write(result.toString());
		return result;
	}

	public static void CopyFiles(File sourceFile, File destFile)
	{
		try
		{
			if (sourceFile.isDirectory())
			{
				destFile.mkdir();
				String[] files = sourceFile.list();
				for (String fileName : files)
				{
					File theSourceFile = new File(sourceFile + File.separator + fileName);
					File theDestFile = new File(destFile + File.separator + fileName);
					CopyFiles(theSourceFile, theDestFile);
				}
			}
			else
			{
				Files.copy(sourceFile, destFile);
			}
		}
		catch (IOException e)
		{
			Log.Write(e.getMessage());
		}
	}

	public static void DeleteFiles(File destFile)
	{
		if (destFile.isDirectory())
		{
			String[] files = destFile.list();
			for (String fileName : files)
			{
				File theDestFile = new File(destFile + File.separator + fileName);
				DeleteFiles(theDestFile);
			}
			destFile.delete();
		}
		else
		{
			destFile.delete();
		}
	}

	public static String getStringFromItemStack(ItemStack data)
	{
		String result = "";
		if (data != null)
		{
			if (data.getData() != null)
			{
				result = data.getTypeId() + ";" + data.getData().getData() + ";:" + data.getAmount() + ":" + data.getDurability();
			}
			else
			{
				result = data.getTypeId() + ";0;:" + data.getAmount() + ":" + data.getDurability();
			}
		}
		return result;
	}

	public static ItemStack getItemStackFromString(String data)
	{
		String[] split = data.split(":");
		String[] item = split[0].split(";");
		if (!item[0].equalsIgnoreCase("0"))
		{
			return new ItemStack(Integer.parseInt(item[0]), Integer.parseInt(split[1]), Short.parseShort(split[2]), Byte.parseByte(item[1]));
		}
		else
		{
			return null;
		}
	}

	public static void CopyWorld(String mapName, String worldName, boolean force)
	{

		String AbsPath = Current.Plugin.getDataFolder().getAbsolutePath();

		AbsPath = AbsPath.substring(0, AbsPath.indexOf("\\plugins\\ChallengeMaps"));
		AbsPath = AbsPath + File.separator + worldName;

		File destFile = new File(AbsPath);

		if (!destFile.exists() || force)
		{
			File srcFile = new File(Current.Plugin.getDataFolder() + File.separator + "maps" + File.separator + mapName);
			if (srcFile.exists())
			{
				Util.CopyFiles(srcFile, destFile);
			}
		}

	}

	public static void DeleteWorld(String mapName, String worldName)
	{

		String AbsPath = Current.Plugin.getDataFolder().getAbsolutePath();

		AbsPath = AbsPath.substring(0, AbsPath.indexOf("\\plugins\\ChallengeMaps"));
		AbsPath = AbsPath + File.separator + worldName;

		File destFile = new File(AbsPath);

		if (destFile.exists())
		{
			Util.DeleteFiles(destFile);
		}
	}

	public static void ResetWorld(String mapName, Player player)
	{

		String worldName = mapName + "_" + player.getName();
		DeleteWorld(mapName, worldName);
		CopyWorld(mapName, worldName, true);
		
		GameState gs = GameStateData.Load(Current.Maps.get(mapName), player, true);
		gs.PendingRemoval = true;

	}
}
