package com.epicsagaonline.bukkit.ChallengeMaps.DAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

import com.epicsagaonline.bukkit.ChallengeMaps.Log;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;

public class MapData
{

	private static final String DATA_PATH = "maps";

	private static void Init()
	{
		if (!Current.Plugin.getDataFolder().exists())
		{
			Current.Plugin.getDataFolder().mkdir();
		}

		File file = new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH);
		if (!file.exists())
		{
			file.mkdir();
		}
	}

	public static void LoadMaps()
	{

		Init();
		File file = new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH);
		String folderNames[] = file.list();
		Current.Maps = new HashMap<String, Map>();

		for (int i = 0; i < folderNames.length; i++)
		{
			Load(new File(file.getAbsolutePath() + File.separator + folderNames[i] + File.separator + "map.yml"));
		}

	}

	@SuppressWarnings("unchecked") private static void Load(File file)
	{
		//Log.Write("Loading Map: " + file.getAbsolutePath());
		if (file.exists())
		{
			Yaml yaml = new Yaml();
			HashMap<String, Object> root = new HashMap<String, Object>();
			FileInputStream stream;
			try
			{
				stream = new FileInputStream(file);
				root = (HashMap<String, Object>) yaml.load(stream);
				Map map = new Map(root);
				Current.Maps.put(map.getMapName().toLowerCase(), map);
			}
			catch (FileNotFoundException e)
			{
				Log.Write(e.getMessage());
			}
		}

	}

}
