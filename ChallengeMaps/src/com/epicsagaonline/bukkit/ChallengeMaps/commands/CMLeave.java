/*

This file is part of EpicZones

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

/**
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.epicsagaonline.bukkit.ChallengeMaps.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;

public class CMLeave
{
	public CMLeave(String[] data, CommandSender sender)
	{
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			GameState gs = Current.getGameStateByWorld(player.getWorld());

			if (gs != null)
			{

				player.sendMessage("Sending you back home.");

				String worldName = player.getWorld().getName();

				if (gs.getEntryPoint() != null)
				{
					player.teleport(gs.getEntryPoint());
				}
				else
				{
					player.teleport(Util.GetLocationFromString(Current.getMapEntranceByMap(gs.getMap()).getChestLocation()));
				}
				gs.setInChallenge(false);

				if (gs.getMap().getResetInventory())
				{
					gs.toggleInventory();
				}

				Current.Plugin.getServer().unloadWorld(worldName, true);
				Current.GameWorlds.remove(worldName);
				gs.PendingRemoval = true;

			}
			else
			{
				player.sendMessage("Error trying to leave challenge.");
			}
		}
	}
}
