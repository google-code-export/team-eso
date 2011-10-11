package com.epicsagaonline.bukkit.ChallengeMaps.DAL;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.ChallengeMaps.ChallengeMaps;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.MapEntrance;

public class Current
{
	public static ChallengeMaps Plugin;
	// <mapname, Map>
	public static HashMap<String, Map> Maps = new HashMap<String, Map>();
	public static HashSet<String> GameWorlds = new HashSet<String>();
	// <LocationString, MapEntrance>
	public static HashMap<String, MapEntrance> MapEntrances = new HashMap<String, MapEntrance>();
	// <PlayerName, GameState>
	public static HashMap<String, GameState> GameStates = new HashMap<String, GameState>();

	public static GameState getGameStateByWorld(World world)
	{
		if (GameWorlds.contains(world.getName()))
		{
			for (Player player : world.getPlayers())
			{
				if (GameStates.containsKey(player.getName()))
				{
					return GameStates.get(player.getName());
				}
			}
		}
		return null;
	}

	public static MapEntrance getMapEntranceByMap(Map map)
	{
		MapEntrance result = null;

		for (String key : MapEntrances.keySet())
		{
			result = MapEntrances.get(key);
			if (result.getMap().getMapName().equals(map.getMapName()))
			{
				break;
			}
		}

		return result;
	}

	public static World LoadWorld(GameState gs, Player player)
	{

		String mapName = gs.getMap().getMapName();
		String worldName = mapName + "_" + player.getName();

		if (!GameWorlds.contains(worldName))
		{
			if (Current.Plugin.getServer().getWorld(worldName) == null)
			{
				Util.CopyWorld(mapName, worldName, false);
			}

			WorldCreator wc = new WorldCreator(worldName);
			Current.Plugin.getServer().createWorld(wc);

			GameWorlds.add(worldName);
		}

		return Plugin.getServer().getWorld(worldName);
	}
}
