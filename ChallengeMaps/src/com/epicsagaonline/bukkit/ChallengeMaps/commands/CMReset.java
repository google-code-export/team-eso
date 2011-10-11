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
import com.epicsagaonline.bukkit.ChallengeMaps.integration.PermissionsManager;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;

public class CMReset
{
	public CMReset(String[] data, CommandSender sender)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (PermissionsManager.hasPermission(player, "challengemaps.enter"))
			{
				if (data.length > 1)
				{
					String mapName = data[1].toLowerCase();
					GameState gs = Current.getGameStateByWorld(player.getWorld());
					Map map = Current.Maps.get(mapName);
					if (map != null)
					{
						if (gs == null)
						{
							if (!map.getHardcore())
							{
								Util.ResetWorld(mapName, player);
								player.sendMessage("Your instance of " + data[1] + " has been reset.");
							}
							else
							{
								player.sendMessage("You cannot reset a hardcore map.");
							}
						}
						else
						{
							player.sendMessage("You must leave the challenge before you can reset it.");
						}
					}
					else
					{
						player.sendMessage("Invalid challenge name.");
					}
				}
				else
				{
					player.sendMessage("Specify the name of the map you want to reset.");
				}
			}
			else
			{
				player.sendMessage("You are not allowed to reset your map instance.");
			}
		}
	}
}
