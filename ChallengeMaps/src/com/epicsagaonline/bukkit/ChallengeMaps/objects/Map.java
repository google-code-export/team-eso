package com.epicsagaonline.bukkit.ChallengeMaps.objects;

import java.util.ArrayList;
import java.util.HashMap;

import com.epicsagaonline.bukkit.ChallengeMaps.Log;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;

public class Map
{
	private String mapName;
	private String entranceText;
	private TimeModes timeMode;
	private WeatherModes weatherMode;
	private boolean resetInventory;
	private Integer deathPenalty;
	private Integer numberOfLives;
	private boolean allowBuilding;
	private boolean allowBreaking;
	private boolean allowCommands;
	private boolean hardcore;
	private HashMap<String, Objective> objectives = new HashMap<String, Objective>();

	@SuppressWarnings("unchecked") public Map(HashMap<String, Object> data)
	{

		this.mapName = Util.getStringValueFromHashSet("MapName", data);
		this.entranceText = Util.getStringValueFromHashSet("EntranceText", data);
		this.timeMode = TimeModes.valueOf(Util.getStringValueFromHashSet("TimeMode", data));
		this.weatherMode = WeatherModes.valueOf(Util.getStringValueFromHashSet("WeatherMode", data));
		this.resetInventory = Util.getBooleanValueFromHashSet("ResetInventory", data);
		this.deathPenalty = Util.getIntegerValueFromHashSet("DeathPenalty", data);
		this.numberOfLives = Util.getIntegerValueFromHashSet("NumberOfLives", data);
		this.allowBuilding = Util.getBooleanValueFromHashSet("AllowBuilding", data);
		this.allowBreaking = Util.getBooleanValueFromHashSet("AllowBreaking", data);
		this.allowCommands = Util.getBooleanValueFromHashSet("AllowCommands", data);
		this.hardcore = Util.getBooleanValueFromHashSet("Hardcore", data);
		
		ArrayList<HashMap<String, Object>> objSet = (ArrayList<HashMap<String, Object>>) Util.getObjectValueFromHashSet("Objectives", data);
		int count = 1;

		for (HashMap<String, Object> obj : objSet)
		{
			Objective newObj = new Objective("Objective" + count, obj);
			this.objectives.put(newObj.getTag(), newObj);
			count++;
		}

		Log.Write("Loaded Map: " + this.getMapName() + " - " + this.objectives.size() + " Objective(s).");

	}

	public String getMapName()
	{
		return this.mapName;
	}

	public String getEntranceText()
	{
		return this.entranceText;
	}

	public TimeModes getTimeMode()
	{
		return this.timeMode;
	}

	public WeatherModes getWeatherMode()
	{
		return this.weatherMode;
	}

	public boolean getResetInventory()
	{
		return this.resetInventory;
	}

	public Integer getDeathPenalty()
	{
		return this.deathPenalty;
	}

	public Integer getNumberOfLives()
	{
		return this.numberOfLives;
	}

	public boolean getAllowBuilding()
	{
		return this.allowBuilding;
	}

	public boolean getAllowBreaking()
	{
		return this.allowBreaking;
	}

	public boolean getAllowCommands()
	{
		return this.allowCommands;
	}
	
	public boolean getHardcore()
	{
		return this.hardcore;
	}

	public HashMap<String, Objective> getObjectives()
	{
		return this.objectives;
	}
}
