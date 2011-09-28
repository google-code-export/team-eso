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

package com.epicsagaonline.bukkit.EpicZones.listeners;


import java.awt.Point;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.Message.Message_ID;
import com.epicsagaonline.bukkit.EpicZones.ZonePermissionsHandler;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;


/**
 * Handle events for all Player related events
 * @author jblaske
 */
public class PlayerEvents extends PlayerListener
{
	private Set<Integer> interactiveItems = new HashSet<Integer>();

	public PlayerEvents(EpicZones instance)
	{
		interactiveItems.add(324); //Wood Door
		interactiveItems.add(330); //Iron Door
		interactiveItems.add(323); //Painting
		interactiveItems.add(321); //Sign
		interactiveItems.add(354); //Bed
		interactiveItems.add(355); //Cake
		interactiveItems.add(356); //Redstone Repeater
	}

	public @Override void onPlayerMove(PlayerMoveEvent event)
	{
		if(!event.isCancelled())
		{
			if(!General.PlayerMovementLogic(event.getPlayer(), event.getFrom(), event.getTo()))
			{
				event.setTo(General.getPlayer(event.getPlayer().getName()).getCurrentLocation());
				event.setCancelled(true);
			}
		}
	}

	public @Override void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if(!event.isCancelled())
		{
			if(!General.PlayerMovementLogic(event.getPlayer(), event.getFrom(), event.getTo()))
			{
				event.setTo(General.getPlayer(event.getPlayer().getName()).getCurrentLocation());
				event.setCancelled(true);
			}
		}
	}

	public @Override void onPlayerLogin(PlayerLoginEvent event)
	{
		if(event.getResult() == Result.ALLOWED)
		{
			General.addPlayer(event.getPlayer());
		}
	}

	public @Override void onPlayerQuit(PlayerQuitEvent event)
	{
		General.removePlayer(event.getPlayer().getName());
	}

	public @Override void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if(!event.isCancelled())
		{
			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
			String worldName = player.getWorld().getName();
			int blockHeight = event.getBlockClicked().getLocation().getBlockY();
			boolean hasPerms = false;
			EpicZone currentZone = null;
			if(General.BorderLogic(blockPoint, player))
			{
				currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
				hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "build");
				if(!hasPerms)
				{
					if (ezp.getLastWarned().before(new Date()))
					{
						Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
						ezp.Warn();
					}
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		if(!event.isCancelled())
		{
			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
			String worldName = player.getWorld().getName();
			int blockHeight = event.getBlockClicked().getLocation().getBlockY();
			boolean hasPerms = false;
			EpicZone currentZone = null;
			if(General.BorderLogic(blockPoint, player))
			{
				currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
				hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "destroy");
				if(!hasPerms)
				{
					if (ezp.getLastWarned().before(new Date()))
					{
						Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
						ezp.Warn();
					}
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onPlayerInteract(PlayerInteractEvent event) 
	{
		if(!event.isCancelled())
		{
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(event.getPlayer() != null)
				{
					if (interactiveItems.contains((event.getPlayer().getItemInHand().getTypeId())))
					{

						Player player = event.getPlayer();
						EpicZonePlayer ezp = General.getPlayer(player.getName());
						Point blockPoint = new Point(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ());
						String worldName = player.getWorld().getName();
						int blockHeight = event.getClickedBlock().getLocation().getBlockY();
						boolean hasPerms = false;

						EpicZone currentZone = null;
						if(General.BorderLogic(blockPoint, player))
						{
							currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
							hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "build");

							if(!hasPerms)
							{
								if (ezp.getLastWarned().before(new Date()))
								{
									Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
									ezp.Warn();
								}
								event.setCancelled(true);
							}
						}
					}
					else if(event.getPlayer().getItemInHand().getTypeId() == General.config.zoneTool)
					{
						if(General.getPlayer(event.getPlayer().getName()).getMode() == EpicZoneMode.ZoneDraw)
						{
							Point point = new Point(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ());
							EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getName());
							ezp.getEditZone().addPoint(point);
							ezp.getEditZone().addPillar(event.getClickedBlock());
							ezp.getEditZone().ShowPillar(point);
							Message.Send(event.getPlayer(), Message_ID.Info_00112_Point_XZ_Added, new String[]{Integer.toString(point.x), Integer.toString(point.y)});
						}
					}
				}
			}
		}
	}
}