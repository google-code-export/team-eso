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

package com.epicsagaonline.bukkit.EpicZones;

import java.awt.Point;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * EpicZones block listener
 * @author jblaske
 */
public class LBlock extends BlockListener {
	//private final EpicZones plugin;
	private static final String NO_PERM_DESTROY = "You do not have permissions to destroy in this zone.";
	private static final String NO_PERM_DESTROY_BORDER = "You do not have permissions to destroy outside the border of the map.";
	private static final String NO_PERM_BUILD = "You do not have permissions to build in this zone.";
	private static final String NO_PERM_BUILD_BORDER = "You do not have permissions to build outside the border of the map.";

	public LBlock(final EpicZones plugin) {
		//this.plugin = plugin;
	}

	public @Override void onBlockIgnite(BlockIgniteEvent event)
	{
		Zone zone = General.getZoneForPoint(event.getBlock().getLocation().getBlockY(),new Point(event.getBlock().getLocation().getBlockX(),event.getBlock().getLocation().getBlockZ()), event.getBlock().getLocation().getWorld().getName());
		if (zone != null)
		{
			if(!zone.getAllowFire())
			{
				event.setCancelled(true);
			}
		}
	}
	
	public @Override void onBlockBurn(BlockBurnEvent event)
	{
		Zone zone = General.getZoneForPoint(event.getBlock().getLocation().getBlockY(),new Point(event.getBlock().getLocation().getBlockX(),event.getBlock().getLocation().getBlockZ()), event.getBlock().getLocation().getWorld().getName());
		if (zone != null)
		{
			if(!zone.getAllowFire())
			{
				event.setCancelled(true);
			}
		}
	}
	
	public @Override void onBlockDamage(BlockDamageEvent event)
	{
		
		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
		String worldName = player.getWorld().getName();
		int blockHeight = event.getBlock().getLocation().getBlockY();
		boolean hasPerms = false;
		Zone currentZone = null;

		if(General.pointWithinBorder(blockPoint, player))
		{
			currentZone = General.getZoneForPoint(blockHeight, blockPoint, worldName);
			hasPerms = General.hasPermissions(player, currentZone, "destroy");

			if(!hasPerms)
			{
				if (ezp.getLastWarned().before(new Date())){
					player.sendMessage(NO_PERM_DESTROY);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}
		else
		{
			if (ezp.getLastWarned().before(new Date())){
				player.sendMessage(NO_PERM_DESTROY_BORDER);
				ezp.Warn();
			}
			event.setCancelled(true);
		}
	}

	public @Override void onBlockPlace(BlockPlaceEvent event)
	{

		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
		String worldName = player.getWorld().getName();
		int blockHeight = event.getBlock().getLocation().getBlockY();
		boolean hasPerms = false;

		Zone currentZone = null;

		if(General.pointWithinBorder(blockPoint, player))
		{
			currentZone = General.getZoneForPoint(blockHeight, blockPoint, worldName);
			hasPerms = General.hasPermissions(player, currentZone, "build");

			if(!hasPerms)
			{
				if (ezp.getLastWarned().before(new Date())){
					player.sendMessage(NO_PERM_BUILD);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}
		else
		{
			if (ezp.getLastWarned().before(new Date())){
				player.sendMessage(NO_PERM_BUILD_BORDER);
				ezp.Warn();
			}
			event.setCancelled(true);
		}
	}

}
