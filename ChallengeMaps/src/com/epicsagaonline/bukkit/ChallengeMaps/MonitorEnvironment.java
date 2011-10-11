/*

This file is part of SolarRedstoneTorch

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

package com.epicsagaonline.bukkit.ChallengeMaps;

import org.bukkit.World;

import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;

public class MonitorEnvironment implements Runnable
{
	public void run()
	{
		if (Current.GameStates.size() > 0)
		{
			for (String key : Current.GameStates.keySet())
			{
				GameState gs = Current.GameStates.get(key);
				Map map = gs.getMap();
				ProcessTime(gs, map);
				ProcessWeather(gs, map);
			}
		}
	}

	private void ProcessTime(GameState gs, Map map)
	{
		switch (map.getTimeMode())
		{
			case ALWAYS_DAY:
				ProcessAlwaysDay(gs);
				break;
			case ALWAYS_NIGHT:
				ProcessAlwaysNight(gs);
				break;
			case NORMAL:
				break;
		}
	}

	private void ProcessWeather(GameState gs, Map map)
	{
		switch (map.getWeatherMode())
		{
			case RAINY:
				ProcessAlwaysRainy(gs);
				break;
			case CLEAR:
				ProcessAlwaysClear(gs);
				break;
			case THUNDERSTORM:
				ProcessAlwaysThunderstorm(gs);
				break;
			case NORMAL:
				break;
		}
	}

	private void ProcessAlwaysDay(GameState gs)
	{
		World world = gs.getWorld();
		if (world.getTime() > 15000 || world.getTime() < 5000)
		{
			world.setTime(5000);
		}
	}

	private void ProcessAlwaysNight(GameState gs)
	{
		World world = gs.getWorld();
		if (world.getTime() > 22000 || world.getTime() < 18000)
		{
			world.setTime(18000);
		}
	}

	private void ProcessAlwaysRainy(GameState gs)
	{
		World world = gs.getWorld();
		if (!world.hasStorm())
		{
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setStorm(true);
		}
		world.setThunderDuration(0);
		world.setThundering(false);
	}

	private void ProcessAlwaysClear(GameState gs)
	{
		World world = gs.getWorld();
		world.setWeatherDuration(0);
		world.setStorm(false);
		world.setThunderDuration(0);
		world.setThundering(false);
	}

	private void ProcessAlwaysThunderstorm(GameState gs)
	{
		World world = gs.getWorld();
		if (!world.hasStorm())
		{
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setStorm(true);
			world.setThunderDuration(Integer.MAX_VALUE);
			world.setThundering(true);
		}
	}
}