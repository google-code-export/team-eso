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

import java.util.HashMap;
import java.util.HashSet;

import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.GameStateData;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.MapEntranceData;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.MapEntrance;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Objective;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Trigger;

public class ProcessObjectives implements Runnable
{

	private static HashSet<String> KeysToRemove = new HashSet<String>();

	public void run()
	{
		if (Current.GameStates.size() > 0)
		{
			for (String key : Current.GameStates.keySet())
			{
				GameState gs = Current.GameStates.get(key);
				for (String objKey : gs.getMap().getObjectives().keySet())
				{
					Objective obj = gs.getMap().getObjectives().get(objKey);
					Trigger trg = obj.getTrigger();
					switch (trg.getType())
					{
						case BLOCK_BREAK:
							ProcessBlockBreak(gs, obj);
							break;
						case BLOCK_PLACE:
							ProcessBlockPlace(gs, obj);
							break;
						case BLOCK_PLACE_DISTANCE:
							ProcessBlockPlaceDistance(gs, obj);
							break;
						case CRAFT:
							ProcessItemCraft(gs, obj);
							break;
						case MAP_COUNT:
							ProcessMapCount(gs, obj);
							break;
					}
				}
				for (String loc : Current.MapEntrances.keySet())
				{
					MapEntrance me = Current.MapEntrances.get(loc);
					if (me.getMap().getMapName().equals(gs.getMap().getMapName()))
					{
						me.getHighScores().put(gs.getPlayer().getName(), gs.getScore());
					}
				}

				if (gs.PendingRemoval)
				{
					Current.GameWorlds.remove(gs.getWorld().getName());
					Current.Plugin.getServer().unloadWorld(gs.getWorld().getName(), true);
					KeysToRemove.add(key);
				}
			}
			GameStateData.saveData();
			MapEntranceData.SaveMapEntrances();
			for (String key : KeysToRemove)
			{
				Current.GameStates.remove(key);
				Log.Write("Unloaded Gamestate [" + key + "]");
			}
			KeysToRemove.clear();
		}
	}

	private void ProcessBlockPlace(GameState gs, Objective obj)
	{
		Integer count = 0;
		for (Integer itemID : obj.getTrigger().getItemIDs())
		{
			if (gs.getBlocksPlaced().containsKey(itemID))
			{
				count += gs.getBlocksPlaced().get(itemID);
			}
		}
		if (count >= obj.getTrigger().getQuantity())
		{
			gs.addCompleteObjective(obj);
		}
	}

	private void ProcessBlockBreak(GameState gs, Objective obj)
	{
		Integer count = 0;
		for (Integer itemID : obj.getTrigger().getItemIDs())
		{
			if (gs.getBlocksBroken().containsKey(itemID))
			{
				count += gs.getBlocksBroken().get(itemID);
			}
		}
		if (count >= obj.getTrigger().getQuantity())
		{
			gs.addCompleteObjective(obj);
		}
	}

	private void ProcessBlockPlaceDistance(GameState gs, Objective obj)
	{
		Integer count = 0;
		if (gs.getBlocksPlacedDistance().containsKey(obj.getTrigger().getParameter()))
		{
			HashMap<Integer, Integer> items = gs.getBlocksPlacedDistance().get(obj.getTrigger().getParameter());
			for (Integer itemID : obj.getTrigger().getItemIDs())
			{
				if (items.containsKey(itemID))
				{
					count += gs.getBlocksBroken().get(itemID);
				}
			}
			if (count >= obj.getTrigger().getQuantity())
			{
				gs.addCompleteObjective(obj);
			}
		}
	}

	private void ProcessItemCraft(GameState gs, Objective obj)
	{
		Integer count = 0;
		for (Integer itemID : obj.getTrigger().getItemIDs())
		{
			if (gs.getItemsCrafted().containsKey(itemID))
			{
				count += gs.getItemsCrafted().get(itemID);
			}
		}
		if (count >= obj.getTrigger().getQuantity())
		{
			gs.addCompleteObjective(obj);
		}
	}

	private void ProcessMapCount(GameState gs, Objective obj)
	{
		Integer count = 0;
		for (Integer itemID : obj.getTrigger().getItemIDs())
		{
			if (gs.getMapContents().containsKey(itemID))
			{
				count += gs.getMapContents().get(itemID);
			}
		}
		if (count >= obj.getTrigger().getQuantity())
		{
			gs.addCompleteObjective(obj);
		}
	}
}
