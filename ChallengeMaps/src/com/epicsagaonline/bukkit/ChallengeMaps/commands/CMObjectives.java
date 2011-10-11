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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Objective;

public class CMObjectives
{
	public CMObjectives(String[] data, CommandSender sender)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			GameState gs = Current.GameStates.get(player.getName());
			if (gs != null)
			{
				int pageNumber = 1;
				int maxPages = 1;
				int firstIndex = 0;
				int objPerPage = 5;

				if (data.length > 1 && Util.IsNumeric(data[1]))
				{
					pageNumber = Integer.parseInt(data[1]);
				}

				if (pageNumber <= 0 && pageNumber > maxPages)
				{
					pageNumber = 1;
				}

				firstIndex = (pageNumber - 1) * objPerPage;
				maxPages = (int) Math.ceil(gs.getMap().getObjectives().size() / objPerPage);

				player.sendMessage("Objectives for " + gs.getMap().getMapName() + ". Page " + pageNumber + " of " + maxPages);
				for (int count = 0; count < objPerPage; count++)
				{
					String message = BuildMessage(gs, firstIndex + count);
					if (message != null && message.length() > 0)
					{
						player.sendMessage(message);
					}
					else
					{
						break;
					}
				}
			}
			else
			{
				player.sendMessage("You must be within a challenge to view your objectives status.");
			}
		}

	}

	private String BuildMessage(GameState gs, int index)
	{
		String result = "";
		String key = "";

		int count = 0;

		for (String innerKey : gs.getMap().getObjectives().keySet())
		{
			if (count == index)
			{
				key = innerKey;
				break;
			}
			count++;
		}

		Objective obj = gs.getMap().getObjectives().get(key);

		if (obj != null)
		{
			if (gs.getCompletedObjectives().contains(key))
			{
				result = ChatColor.GREEN + "[Done] " + ChatColor.WHITE;
			}
			else
			{
				result = ChatColor.RED + "[      ] " + ChatColor.WHITE;
			}
			result = result + obj.getText() + ChatColor.BLUE + " (" + obj.getScoreValue() + ")";
		}

		return result;
	}
}
