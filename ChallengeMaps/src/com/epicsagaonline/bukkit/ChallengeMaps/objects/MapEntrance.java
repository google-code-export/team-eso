package com.epicsagaonline.bukkit.ChallengeMaps.objects;

import java.util.HashMap;

import org.bukkit.Location;

import com.epicsagaonline.bukkit.ChallengeMaps.Log;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;

public class MapEntrance
{
	private String signLocation;
	private String chestLocation;
	private Map map;
	private HashMap<String, Integer> highScores = new HashMap<String, Integer>();

	public MapEntrance()
	{}

	@SuppressWarnings("unchecked") public MapEntrance(HashMap<String, Object> data)
	{

		if (data != null)
		{
			Map map = Current.Maps.get(Util.getStringValueFromHashSet("MapName", data).toLowerCase());
			if (map != null)
			{
				this.map = map;
				this.signLocation = Util.getStringValueFromHashSet("SignLocation", data);
				this.chestLocation = Util.getStringValueFromHashSet("ChestLocation", data);
				this.highScores = (HashMap<String, Integer>) Util.getObjectValueFromHashSet("HighScores", data);
				if (this.highScores == null)
				{
					highScores = new HashMap<String, Integer>();
				}
				Log.Write("Loaded Entrance To: " + map.getMapName() + " at " + this.signLocation);
			}

		}
	}

	public String getSignLocation()
	{
		return this.signLocation;
	}

	public String getChestLocation()
	{
		return this.chestLocation;
	}

	public Map getMap()
	{
		return this.map;
	}

	public HashMap<String, Integer> getHighScores()
	{
		return this.highScores;
	}

	public void setSignLocation(Location value)
	{
		this.signLocation = Util.GetStringFromLocation(value);
	}

	public void setChestLocation(Location value)
	{
		this.chestLocation = Util.GetStringFromLocation(value);
	}

	public void setMap(Map value)
	{
		this.map = value;
	}
}
