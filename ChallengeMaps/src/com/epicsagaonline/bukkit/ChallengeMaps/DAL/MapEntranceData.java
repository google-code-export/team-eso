package com.epicsagaonline.bukkit.ChallengeMaps.DAL;

import java.util.HashMap;
import java.util.HashSet;

import com.epicsagaonline.bukkit.ChallengeMaps.Log;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.MapEntrance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.yaml.snakeyaml.Yaml;

public class MapEntranceData
{

	private static final String DATA_PATH = "data" + File.separator + "entrances.yml";

	private static void Init()
	{
		try
		{
			if (!Current.Plugin.getDataFolder().exists())
			{
				Current.Plugin.getDataFolder().mkdir();
			}
			File file = new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH);
			if (!file.exists())
			{
				file.createNewFile();
			}
		}
		catch (IOException e)
		{
			Log.Write(e.getMessage());
		}
	}

	public static void SaveMapEntrances()
	{
		Init();
		Save(new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH));
		//Log.Write("Map Entrances Saved.");
	}

	private static void Save(File file)
	{
		Yaml yaml = new Yaml();
		HashMap<String, Object> root = new HashMap<String, Object>();
		FileOutputStream stream;
		BufferedWriter writer;
		HashSet<String> savedEntrances = new HashSet<String>();

		int count = 1;
		for (String key : Current.MapEntrances.keySet())
		{
			MapEntrance me = Current.MapEntrances.get(key);
			if (me != null)
			{
				if (me.getMap() != null)
				{
					if (!savedEntrances.contains(me.getSignLocation()))
					{
						HashMap<String, Object> newEnt = new HashMap<String, Object>();
						newEnt.put("MapName", me.getMap().getMapName());
						newEnt.put("SignLocation", me.getSignLocation());
						newEnt.put("ChestLocation", me.getChestLocation());
						newEnt.put("HighScores", me.getHighScores());
						root.put("Entrance" + count, newEnt);
						savedEntrances.add(me.getSignLocation());
						count++;
					}
				}
			}
		}

		try
		{
			stream = new FileOutputStream(file);
			stream.getChannel().truncate(0);
			writer = new BufferedWriter(new OutputStreamWriter(stream));
			try
			{
				writer.write(yaml.dump(root));
			}
			finally
			{
				writer.close();
			}
		}
		catch (IOException e)
		{
			Log.Write(e.getMessage());
		}
	}

	public static void LoadMapEntrances()
	{
		Init();
		Load(new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH));
		if (Current.MapEntrances.size() == 0)
		{
			Log.Write("No Map Entrances Detected!");
		}
		else
		{
			Log.Write((Current.MapEntrances.size() / 2) + " Map Entrance(s) Loaded.");
		}
	}

	@SuppressWarnings("unchecked") private static void Load(File file)
	{

		if (file.exists())
		{
			Yaml yaml = new Yaml();
			HashMap<String, Object> root = new HashMap<String, Object>();
			FileInputStream stream;
			try
			{
				stream = new FileInputStream(file);
				root = (HashMap<String, Object>) yaml.load(stream);
				if (root != null)
				{
					for (String key : root.keySet())
					{
						HashMap<String, Object> data = (HashMap<String, Object>) Util.getObjectValueFromHashSet(key, root);
						if (data != null)
						{
							MapEntrance me = new MapEntrance(data);
							if (me != null)
							{
								Current.MapEntrances.put(me.getSignLocation(), me);
								Current.MapEntrances.put(me.getChestLocation(), me);
							}
						}
					}
				}
			}
			catch (FileNotFoundException e)
			{
				Log.Write(e.getMessage());
			}
		}
	}
}
