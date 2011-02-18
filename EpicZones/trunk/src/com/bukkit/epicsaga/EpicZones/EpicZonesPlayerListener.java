package com.bukkit.epicsaga.EpicZones;

import java.awt.Point;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.bukkit.epicsaga.EpicZones.EpicZonePlayer.EpicZoneMode;
import com.bukkit.epicsaga.EpicZones.CommandHandlers.ReloadCommandHandler;
import com.bukkit.epicsaga.EpicZones.CommandHandlers.WhoCommandHandler;
import com.bukkit.epicsaga.EpicZones.CommandHandlers.ZoneCommandHandler;

//import sun.security.mscapi.KeyStore.MY;

/**
 * Handle events for all Player related events
 * @author jblaske
 */
public class EpicZonesPlayerListener extends PlayerListener
{
	private final EpicZones plugin;
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

		if(General.ShouldCheckPlayer(ezp))
		{
			if(!ezp.isTeleporting())
			{
				if(ezp.getCurrentLocation() == null){ezp.setCurrentLocation(event.getFrom());}
				if(!PlayerWithinZoneLogic(player, ezp, playerHeight, playerPoint))
				{
					ezp.setIsTeleporting(true);
					player.teleportTo(ezp.getCurrentLocation());
					ezp.setIsTeleporting(false);
					event.setTo(ezp.getCurrentLocation());
					event.setCancelled(true);
				}
				else
				{
					ezp.setCurrentLocation(event.getFrom());
				}
			}
			ezp.Check();
		}

	}

	public @Override void onPlayerTeleport(PlayerMoveEvent event)
	{

		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		int playerHeight = event.getTo().getBlockY();
		Point playerPoint = new Point(event.getTo().getBlockX(), event.getTo().getBlockZ());

		if(General.ShouldCheckPlayer(ezp))
		{
			if(!ezp.isTeleporting())
			{
				if(ezp.getEntityID() != player.getEntityId()){ezp.setEntityID(player.getEntityId());}
				if(ezp.getCurrentLocation() == null){ezp.setCurrentLocation(event.getFrom());}
				if(!PlayerWithinZoneLogic(player, ezp, playerHeight, playerPoint))
				{
					ezp.setIsTeleporting(true);
					player.teleportTo(ezp.getCurrentLocation());
					ezp.setIsTeleporting(false);
					event.setTo(ezp.getCurrentLocation());
					event.setCancelled(true);
				}
				{
					ezp.setCurrentLocation(event.getTo());
				}
			}
			ezp.Check();
		}

	}

	private boolean PlayerWithinZoneLogic(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint)
	{

		EpicZone foundZone = null;
		String worldName = player.getWorld().getName();

		if(General.pointWithinBorder(playerPoint, player))
		{

			foundZone = FindZone(player, ezp, playerHeight, playerPoint, worldName);

			if(foundZone != null)
			{

				if (ezp.getCurrentZone() == null || foundZone != ezp.getCurrentZone())
				{
					if(General.hasPermissions(player, foundZone, "entry"))
					{
						if(ezp.getCurrentZone() != null){ezp.setPreviousZoneTag(ezp.getCurrentZone().getTag());}
						ezp.setCurrentZone(foundZone);
						EpicZonesHeroChat.joinChat(foundZone.getTag(), ezp, player);
						if(foundZone.getEnterText().length() > 0){player.sendMessage(foundZone.getEnterText());}
					}
					else
					{
						General.WarnPlayer(player, ezp, General.NO_PERM_ENTER + foundZone.getName());
						return false;
					}
				}

			}
			else
			{
				if (ezp.getCurrentZone() != null)
				{
					if(ezp.getCurrentZone().getExitText().length() > 0){player.sendMessage(ezp.getCurrentZone().getExitText());}
					EpicZonesHeroChat.leaveChat(ezp.getCurrentZone().getTag(), player);
					ezp.setCurrentZone(null);
				}
			}
		}
		else
		{
			General.WarnPlayer(player, ezp, General.NO_PERM_BORDER);
			return false;
		}

		return true;

	}

	private EpicZone FindZone(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint, String worldName)
	{

		EpicZone result = null;

		if(ezp.getCurrentZone() != null)
		{

			String resultTag;
			result = ezp.getCurrentZone();
			resultTag = General.isPointInZone(result, playerHeight, playerPoint, worldName);
			if(resultTag.length() > 0)
			{
				if(!resultTag.equalsIgnoreCase(ezp.getCurrentZone().getTag()))
				{
					result = General.myZones.get(resultTag);
				}
			}
			else
			{
				result = null;
			}

		}
		else
		{
			result = General.getZoneForPoint(player, ezp, playerHeight, playerPoint, worldName);
		}

		return result;

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
			if (split[0].equalsIgnoreCase("/who")){WhoCommandHandler.Process(split, event);}
			else if (split[0].equalsIgnoreCase("/reloadez")){ReloadCommandHandler.Process(split, event, plugin);}
			else if (split[0].equalsIgnoreCase("/zone")){ZoneCommandHandler.Process(split, event);}
		}
	}

	public @Override void onPlayerItem(PlayerItemEvent event)
	{

		if (bucketTypes.contains((event.getPlayer().getItemInHand().getTypeId())))
		{

			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
			String worldName = player.getWorld().getName();
			int blockHeight = event.getBlockClicked().getLocation().getBlockY();
			boolean hasPerms = false;

			EpicZone currentZone = null;
			if(General.pointWithinBorder(blockPoint, player))
			{
				currentZone = General.getZoneForPoint(player, ezp, blockHeight, blockPoint, worldName);
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
		}
		else if(event.getPlayer().getItemInHand().getTypeId() == EMPTY_BUCKET)
		{
			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
			String worldName = player.getWorld().getName();
			int blockHeight = event.getBlockClicked().getLocation().getBlockY();
			boolean hasPerms = false;

			EpicZone currentZone = null;
			if(General.pointWithinBorder(blockPoint, player))
			{
				currentZone = General.getZoneForPoint(player, ezp, blockHeight, blockPoint, worldName);
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
		else if(event.getPlayer().getItemInHand().getTypeId() == General.config.zoneTool)
		{
			if(General.getPlayer(event.getPlayer().getEntityId()).getMode() == EpicZoneMode.ZoneDraw)
			{
				Point point = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
				General.getPlayer(event.getPlayer().getEntityId()).getEditZone().addPoint(point);
				event.getPlayer().sendMessage("Point " + point.x + ":" + point.y + " added to zone.");
			}
		}

	}
}