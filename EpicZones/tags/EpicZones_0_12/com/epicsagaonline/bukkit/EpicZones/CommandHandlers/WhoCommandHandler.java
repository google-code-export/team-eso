package com.epicsagaonline.bukkit.EpicZones.CommandHandlers;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.epicsagaonline.bukkit.EpicZones.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;

public class WhoCommandHandler {

	public static void Process(String[] data, PlayerChatEvent event)
	{

		int pageNumber = 1;

		if(EpicZones.permissions.has(event.getPlayer(), "epiczones.who"))
		{
			if (data.length > 1)
			{
				if (data[1].equalsIgnoreCase("all"))
				{
					if (data.length > 2)
					{
						try
						{
							pageNumber = Integer.parseInt(data[2]);
						}
						catch(NumberFormatException nfe)
						{
							pageNumber = 1;
						}
					}
					buildWho(General.getPlayer(event.getPlayer().getName()), event.getPlayer(), pageNumber, true);
					return;
				}
				else
				{
					try
					{
						pageNumber = Integer.parseInt(data[1]);
					}
					catch(NumberFormatException nfe)
					{
						pageNumber = 1;
					}
				}
			}
			buildWho(General.getPlayer(event.getPlayer().getName()), event.getPlayer(), pageNumber, false);
			event.setCancelled(true);
		}
	}

	private static void buildWho(EpicZonePlayer ezp, Player player, int pageNumber, boolean allZones)
	{

		EpicZone currentZone = General.getPlayer(player.getName()).getCurrentZone();
		if(currentZone == null){allZones = true;}
		ArrayList<EpicZonePlayer> players = getPlayers(currentZone, allZones);
		int playersPerPage = 8;
		int playerCount = players.size();

		if (allZones)
		{
			player.sendMessage(playerCount + " Players Online [Page " + pageNumber + " of " + ((int)Math.ceil((double)playerCount / (double)playersPerPage)) + "]");
			for(int i = (pageNumber - 1) * playersPerPage; i < (pageNumber * playersPerPage); i++)
			{
				if (players.size() > i)
				{
					player.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
				}
			}
		}
		else
		{
			player.sendMessage(playerCount + " Players Online in " + currentZone.getName() + " [Page " + pageNumber + " of " + ((int)Math.ceil((double)playerCount / playersPerPage) + 1) + "]");
			for(int i = (pageNumber - 1) * playersPerPage; i < pageNumber * playersPerPage; i++)
			{
				if (players.size() > i)
				{
					player.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
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
