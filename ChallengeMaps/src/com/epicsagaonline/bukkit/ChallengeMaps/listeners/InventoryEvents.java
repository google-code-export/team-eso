/*

        This file is part of ChallengeMaps

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

package com.epicsagaonline.bukkit.ChallengeMaps.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;

import com.epicsagaonline.bukkit.ChallengeMaps.ChallengeMaps;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.GameStateData;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.MapEntrance;

public class InventoryEvents extends InventoryListener
{
	public InventoryEvents(ChallengeMaps instance)
	{}

	public @Override void onInventoryCraft(InventoryCraftEvent event)
	{
		if (!event.isCancelled())
		{
			Player player = event.getPlayer();
			GameState gs = Current.GameStates.get(player.getName());
			if (gs != null)
			{
				ItemStack result = event.getResult();
				if (result != null)
				{
					gs.addItemCrafted(result.getTypeId());
				}
			}
		}
	}

	public @Override void onInventoryOpen(InventoryOpenEvent event)
	{
		if (!event.isCancelled())
		{
			Location loc = event.getLocation();
			if (loc != null)
			{
				MapEntrance me = Current.MapEntrances.get(Util.GetStringFromLocation(loc));
				if (me != null)
				{
					GameState gs = GameStateData.Load(me.getMap(), event.getPlayer(), false);
					event.getInventory().clear();
					for (Integer key : gs.getRewards().keySet())
					{
						event.getInventory().addItem(new ItemStack(key, gs.getRewards().get(key)));
					}
				}
			}
		}
	}
}
