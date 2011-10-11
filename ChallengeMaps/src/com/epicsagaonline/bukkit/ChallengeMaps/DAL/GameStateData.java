package com.epicsagaonline.bukkit.ChallengeMaps.DAL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import com.epicsagaonline.bukkit.ChallengeMaps.Log;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;

public class GameStateData
{

	private static final String DATA_PATH = "data";
	private static boolean savingData = false;

	public static void saveData()
	{
		if (!savingData)
		{
			savingData = true;
			// Log.Write("Saving Gamestates.");
			for (String key : Current.GameStates.keySet())
			{
				GameState gs = Current.GameStates.get(key);
				Init();
				File file = new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH + File.separator + gs.getKey() + ".yml");
				if (!file.exists())
				{
					try
					{
						file.createNewFile();
					}
					catch (IOException e)
					{
						Log.Write(e.getMessage());
					}
				}
				Save(file, gs);
			}
			// Log.Write("Gamestates Saved.");
			savingData = false;
		}
	}

	private static void Save(File file, GameState gs)
	{
		Yaml yaml = new Yaml();
		HashMap<String, Object> root = new HashMap<String, Object>();
		FileOutputStream stream;
		BufferedWriter writer;
		HashSet<String> completedObjectives = new HashSet<String>();

		root.put("MapName", gs.getMap().getMapName());
		root.put("WorldName", gs.getWorld().getName());
		root.put("PlayerName", gs.getPlayer().getName());
		root.put("Score", gs.getScore());
		root.put("DeathCount", gs.getDeathCount());
		root.put("EntryPoint", Util.GetStringFromLocation(gs.getEntryPoint()));

		for (String obj : gs.getCompletedObjectives())
		{
			completedObjectives.add(obj);
		}
		root.put("CompletedObjectives", completedObjectives);

		root.put("BlocksBroken", gs.getBlocksBroken());
		root.put("BlocksPlaced", gs.getBlocksPlaced());
		root.put("BlocksPlacedDistance", gs.getBlocksPlacedDistance());
		root.put("ItemsCrafted", gs.getItemsCrafted());
		root.put("Rewards", gs.getRewards());
		root.put("MapContents", gs.getMapContents());
		root.put("InventoryBuffer", gs.getInventoryBuffer());
		root.put("InChallenge", gs.getInChallenge());

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

	@SuppressWarnings("unchecked") public static GameState Load(Map map, Player player, boolean forceNew)
	{
		Init();
		String fileName = map.getMapName() + "_" + player.getName() + ".yml";
		File file = new File(Current.Plugin.getDataFolder() + File.separator + DATA_PATH + File.separator + fileName);
		GameState gs = null;
		if (file.exists() && !forceNew)
		{
			Yaml yaml = new Yaml();
			HashMap<String, Object> root = new HashMap<String, Object>();
			FileInputStream stream;
			try
			{
				stream = new FileInputStream(file);
				root = (HashMap<String, Object>) yaml.load(stream);
				gs = new GameState(root, player);
				Current.GameStates.put(gs.getPlayer().getName(), gs);
			}
			catch (FileNotFoundException e)
			{
				Log.Write(e.getMessage());
			}
		}
		else
		{
			gs = New(map, player);
		}

		return gs;
	}

	private static GameState New(Map map, Player player)
	{
		GameState gs = new GameState(map, player);
		if (Current.GameStates.get(gs.getPlayer().getName()) == null)
		{
			Current.GameStates.remove(gs.getPlayer().getName());
		}
		Current.GameStates.put(gs.getPlayer().getName(), gs);
		GameStateData.saveData();
		return gs;
	}

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
}
