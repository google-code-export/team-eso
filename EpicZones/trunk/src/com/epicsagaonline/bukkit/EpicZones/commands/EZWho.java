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

package com.epicsagaonline.bukkit.EpicZones.commands;

import java.util.ArrayList;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class EZWho implements CommandHandler {

	public boolean onCommand(String command, CommandSender sender, String[] args) {

		if((sender instanceof Player && EpicZones.permissions.hasPermission((Player)sender, "epiczones.who")))
		{
			Player player = (Player)sender;
			int pageNumber = 1;
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("all"))
				{
					if (args.length > 1)
					{
						try
						{
							pageNumber = Integer.parseInt(args[1]);
						}
						catch(NumberFormatException nfe)
						{
							pageNumber = 1;
						}
					}
					buildWho(General.getPlayer(player.getName()), player, sender, pageNumber, true);
					return true;
				}
				else
				{
					try
					{
						pageNumber = Integer.parseInt(args[0]);
					}
					catch(NumberFormatException nfe)
					{
						pageNumber = 1;
					}
				}
			}
			buildWho(General.getPlayer(player.getName()), player, sender, pageNumber, false);
			return true;
		}
		return false;
	}

	private static void buildWho(EpicZonePlayer ezp, Player player, CommandSender sender, int pageNumber, boolean allZones)
	{

		EpicZone currentZone = General.getPlayer(player.getName()).getCurrentZone();
		if(currentZone == null){allZones = true;}
		ArrayList<EpicZonePlayer> players = getPlayers(currentZone, allZones);
		int playersPerPage = 8;
		int playerCount = players.size();

		if (allZones)
		{
			sender.sendMessage(playerCount + " Players Online [Page " + pageNumber + " of " + ((int)Math.ceil((double)playerCount / (double)playersPerPage)) + "]");
			for(int i = (pageNumber - 1) * playersPerPage; i < (pageNumber * playersPerPage); i++)
			{
				if (players.size() > i)
				{
					sender.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
				}
			}
		}
		else
		{
			sender.sendMessage(playerCount + " Players Online in " + currentZone.getName() + " [Page " + pageNumber + " of " + ((int)Math.ceil((double)playerCount / playersPerPage) + 1) + "]");
			for(int i = (pageNumber - 1) * playersPerPage; i < pageNumber * playersPerPage; i++)
			{
				if (players.size() > i)
				{
					sender.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
				}
			}
		}
	}

	private static String buildWhoPlayerName(EpicZonePlayer ezp, ArrayList<EpicZonePlayer> players, int index, boolean allZones )
	{

		if (allZones)
		{
			if(players.get(index).getCurrentZone() != null)
			{
				return players.get(index).getName() + " - " + players.get(index).getCurrentZone().getName() + " - Distance: " + CalcDist(ezp, players.get(index));
			}
			else
			{
				return players.get(index).getName() + " - Distance: " + CalcDist(ezp, players.get(index));
			}
		}
		else
		{
			return players.get(index).getName() + " - Distance: " + CalcDist(ezp, players.get(index));
		}
	}

	private static int	CalcDist(EpicZonePlayer player1, EpicZonePlayer player2)
	{
		int result = 0;

		if(!player1.getName().equals(player2.getName()))
		{
			int a = Math.abs(player1.getCurrentLocation().getBlockX() - player2.getCurrentLocation().getBlockX()); 
			int b = Math.abs(player1.getCurrentLocation().getBlockZ() - player2.getCurrentLocation().getBlockZ());
			int aSquared = (a * a);
			int bSquared = (b * b);
			int cSquared = aSquared + bSquared;			
			
			result = (int)Math.ceil(Math.sqrt(cSquared));
		}

		return result;
	}

	private static ArrayList<EpicZonePlayer> getPlayers(EpicZone currentZone, boolean allZones)
	{
		if (allZones)
		{
			return General.myPlayers;
		}
		else
		{
			ArrayList<EpicZonePlayer> result = new ArrayList<EpicZonePlayer>();
			for (EpicZonePlayer ezp: General.myPlayers)
			{
				if (!result.contains(ezp) && ezp.getCurrentZone().equals(currentZone))
				{
					result.add(ezp);
				}
			}
			return result;
		}
	}
}
