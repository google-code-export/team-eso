package com.bukkit.epicsaga.EpicZones;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

//import sun.security.mscapi.KeyStore.MY;

/**
 * Handle events for all Player related events
 * @author jblaske
 */
public class EpicZonesPlayerListener extends PlayerListener
{
	private final EpicZones plugin;
	private static final String NO_PERM_ENTER = "You do not have permission to enter ";
	private static final String NO_PERM_BORDER = "You have reached the border of the map.";
	private static final String NO_PERM_BUCKET = "You do not have permissions to do that in this zone.";
	private static final int EMPTY_BUCKET = 325;
	private Set<Integer> bucketTypes = new HashSet<Integer>();

	public EpicZonesPlayerListener(EpicZones instance)
	{
		plugin = instance;
		bucketTypes.add(326);
		bucketTypes.add(327);
	}

	public @Override void onPlayerMove(PlayerMoveEvent event)
	{

		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		int playerHeight = event.getTo().getBlockY();
		Point playerPoint = new Point(event.getTo().getBlockX(), event.getTo().getBlockZ());

		if(ezp.getCurrentLocation() == null){ezp.setCurrentLocation(event.getFrom());}
		if(!PlayerWithinZoneLogic(player, ezp, playerHeight, playerPoint))
		{
			event.setTo(ezp.getCurrentLocation());
			event.setCancelled(true);
		}
		else
		{
			ezp.setCurrentLocation(event.getTo());
		}

	}

	public @Override void onPlayerTeleport(PlayerMoveEvent event)
	{

		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		int playerHeight = event.getTo().getBlockY();
		Point playerPoint = new Point(event.getTo().getBlockX(), event.getTo().getBlockZ());

		if(ezp.getCurrentLocation() == null){ezp.setCurrentLocation(event.getFrom());}
		if(!PlayerWithinZoneLogic(player, ezp, playerHeight, playerPoint))
		{
			event.setTo(ezp.getCurrentLocation());
			event.setCancelled(true);
		}
		{
			ezp.setCurrentLocation(event.getTo());
		}

	}

	private boolean PlayerWithinZoneLogic(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint)
	{

		EpicZone foundZone = null;

		if(playerWithinBorder(playerPoint, player))
		{

			if(ezp.getCurrentZone() != null)
			{

				String resultTag;
				foundZone = ezp.getCurrentZone();
				resultTag = General.isPointInZone(foundZone, playerHeight, playerPoint);
				if(resultTag.length() > 0)
				{
					if(!resultTag.equals(ezp.getCurrentZone().getTag()))
					{
						foundZone = General.myZones.get(resultTag);
					}
				}
				else
				{
					foundZone = null;
				}

			}
			else
			{
				foundZone = General.getZoneForPoint(player, ezp, playerHeight, playerPoint);
			}


			if(foundZone != null)
			{

				if (ezp.getCurrentZone() == null || foundZone != ezp.getCurrentZone())
				{
					if(General.hasPermissions(player, foundZone, "entry"))
					{
						ezp.setCurrentZone(foundZone);
						player.sendMessage(foundZone.getEnterText());
					}
					else
					{
						WarnPlayer(player, ezp, NO_PERM_ENTER + foundZone.getName());
						player.teleportTo(ezp.getCurrentLocation());
						return false;
					}
				}

			}
			else
			{
				if (ezp.getCurrentZone() != null)
				{
					player.sendMessage(ezp.getCurrentZone().getExitText());
					ezp.setCurrentZone(null);
				}
			}
		}
		else
		{
			WarnPlayer(player, ezp, NO_PERM_BORDER);
			player.teleportTo(ezp.getCurrentLocation());
			return false;
		}

		return true;

	}

	private void WarnPlayer(Player player, EpicZonePlayer ezp, String message)
	{
		if (ezp.getLastWarned().before(new Date()))
		{
			player.sendMessage(message);
			ezp.Warn();
		}
	}

	public @Override void onPlayerLogin(PlayerLoginEvent event)
	{
		if(event.getResult() != Result.ALLOWED)
			return;

		General.addPlayer(event.getPlayer().getEntityId(), event.getPlayer().getName());
	}

	public @Override void onPlayerQuit(PlayerEvent event)
	{
		General.removePlayer(event.getPlayer().getEntityId());
	}

	public @Override void onPlayerCommand(PlayerChatEvent event)
	{
		if(!event.isCancelled())
		{
			String[] split = event.getMessage().split("\\s");

			if (split[0].equalsIgnoreCase("/who"))
			{
				int pageNumber = 1;

				if (split.length > 1)
				{
					if (split[1].equalsIgnoreCase("all"))
					{
						if (split.length > 2)
						{
							try
							{
								pageNumber = Integer.parseInt(split[2]);
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
							pageNumber = Integer.parseInt(split[1]);
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
			else if(split[0].equalsIgnoreCase("/reloadez"))
			{
				General.config.load();
				//General.config.save();
				try {
					General.loadZones(null);
					event.getPlayer().sendMessage("EpicZones Reloaded.");
					event.setCancelled(true);
				}
				catch (FileNotFoundException e) {
					event.getPlayer().sendMessage("Error, zone file not found.");
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onPlayerItem(PlayerItemEvent event)
	{

		if (bucketTypes.contains((event.getPlayer().getItemInHand().getTypeId())))
		{

			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
			int blockHeight = event.getBlockClicked().getLocation().getBlockY();
			boolean hasPerms = false;

			EpicZone currentZone = null;

			currentZone = General.getZoneForPoint(player, ezp, blockHeight, blockPoint);
			hasPerms = General.hasPermissions(player, currentZone, "build");

			if(!hasPerms)
			{
				if (ezp.getLastWarned().before(new Date()))
				{
					player.sendMessage(NO_PERM_BUCKET);
					ezp.Warn();
				}
				event.setCancelled(true);
			}

		}
		else if(event.getPlayer().getItemInHand().getTypeId() == EMPTY_BUCKET)
		{
			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
			int blockHeight = event.getBlockClicked().getLocation().getBlockY();
			boolean hasPerms = false;

			EpicZone currentZone = null;

			currentZone = General.getZoneForPoint(player, ezp, blockHeight, blockPoint);
			hasPerms = General.hasPermissions(player, currentZone, "destroy");

			if(!hasPerms)
			{
				if (ezp.getLastWarned().before(new Date()))
				{
					player.sendMessage(NO_PERM_BUCKET);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}

	}

	private void buildWho(EpicZonePlayer ezp, Player player, int pageNumber, boolean allZones)
	{

		EpicZone currentZone = General.getPlayer(player.getName()).getCurrentZone();
		if(currentZone == null){allZones = true;}
		ArrayList<EpicZonePlayer> players = getPlayers(currentZone, allZones);
		int playersPerPage = 8;
		int playerCount = players.size();

		if (allZones)
		{
			player.sendMessage(playerCount + " Players Online [Page " + pageNumber + " of " + ((int)Math.ceil((double)playerCount / (double)playersPerPage) + 1) + "]");
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

	private String buildWhoPlayerName(EpicZonePlayer ezp, ArrayList<EpicZonePlayer> players, int index, boolean allZones )
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

	private int	CalcDist(EpicZonePlayer player1, EpicZonePlayer player2)
	{
		int result = 0;

		if(!player1.getName().equals(player2.getName()))
		{
			int aSquared = (int)(player1.getDistanceFromCenter() * player1.getDistanceFromCenter());
			int bSquared = (int)(player2.getDistanceFromCenter() * player2.getDistanceFromCenter());
			int cSquared = aSquared + bSquared;

			result = (int)Math.sqrt(cSquared);
		}

		return result;
	}

	private ArrayList<EpicZonePlayer> getPlayers(EpicZone currentZone, boolean allZones)
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

	private boolean playerWithinBorder(Point point, Player player)
	{

		EpicZonePlayer ezp = General.getPlayer(player.getName());
		double xsquared = point.x * point.x;
		double ysquared = point.y * point.y;
		double distanceFromCenter = Math.sqrt(xsquared + ysquared);

		ezp.setDistanceFromCenter((int)distanceFromCenter);

		if(General.config.enableRadius && !EpicZones.permissions.has(player, "epiczones.ignoremapradius"))
		{

			//WarnPlayer(player, ezp, "Distance From Center: " + distanceFromCenter);
			if(distanceFromCenter <= General.config.mapRadius)
			{
				return true;
			}
			else
			{
				return false;
			}

		}
		else
		{
			return true;
		}
	}
}