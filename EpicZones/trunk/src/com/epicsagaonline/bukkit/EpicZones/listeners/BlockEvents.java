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


import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.ZonePermissionsHandler;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

/**
 * EpicZones block listener
 * @author jblaske
 */
public class BlockEvents extends BlockListener {
	//private final EpicZones plugin;
	
	public BlockEvents(final EpicZones plugin) {
		//this.plugin = plugin;
	}

	public @Override void onBlockIgnite(BlockIgniteEvent event)
	{
		if(!event.isCancelled())
		{
			EpicZone zone = General.GetZoneForPlayer(null, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockY(),new Point(event.getBlock().getLocation().getBlockX(),event.getBlock().getLocation().getBlockZ()));
			if (zone != null)
			{
				if(!zone.getFire())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onBlockBurn(BlockBurnEvent event)
	{
		if(!event.isCancelled())
		{
			EpicZone zone = General.GetZoneForPlayer(null, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockY() ,new Point(event.getBlock().getLocation().getBlockX(),event.getBlock().getLocation().getBlockZ()));
			if (zone != null)
			{
				if(!zone.getFire())
				{
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onBlockBreak(BlockBreakEvent event)
	{
		if(!event.isCancelled())
		{
			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
			String worldName = player.getWorld().getName();
			int blockHeight = event.getBlock().getLocation().getBlockY();
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
						Message.Send(player, 32);
						ezp.Warn();
					}
					event.setCancelled(true);
				}
			}
			else
			{
				if (ezp.getLastWarned().before(new Date()))
				{
					Message.Send(player, 33);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}
	}

	public @Override void onBlockPlace(BlockPlaceEvent event)
	{
		if(!event.isCancelled())
		{
			Player player = event.getPlayer();
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
			String worldName = player.getWorld().getName();
			int blockHeight = event.getBlock().getLocation().getBlockY();
			boolean hasPerms = false;

			EpicZone currentZone = null;

			if(General.BorderLogic(blockPoint, player))
			{
				currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
				hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "build");

				if(!hasPerms)
				{
					if (ezp.getLastWarned().before(new Date())){
						Message.Send(player, 32);
						ezp.Warn();
					}
					event.setCancelled(true);
				}
			}
			else
			{
				if (ezp.getLastWarned().before(new Date())){
					Message.Send(player, 33);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}
	}
}
