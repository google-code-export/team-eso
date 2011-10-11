/*

        This file is part of SolarRedstoneTorch

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

package com.epicsagaonline.bukkit.ChallengeMaps.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.epicsagaonline.bukkit.ChallengeMaps.ChallengeMaps;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.MapEntranceData;
import com.epicsagaonline.bukkit.ChallengeMaps.integration.PermissionsManager;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.MapEntrance;

public class BlockEvents extends BlockListener
{
	public BlockEvents(ChallengeMaps instance)
	{}

	public @Override void onSignChange(SignChangeEvent event)
	{
		if (!event.isCancelled())
		{
			String line1 = event.getLines()[0];
			String line2 = event.getLines()[1];
			if (line1.equalsIgnoreCase("[challenge]"))
			{
				Map map = Current.Maps.get(line2.toLowerCase());
				if (map != null)
				{
					Location chestLoc = event.getBlock().getLocation();
					chestLoc.setY(chestLoc.getBlockY() - 1);
					Block chest = event.getBlock().getWorld().getBlockAt(chestLoc);
					if (chest.getTypeId() == 54)
					{
						if (PermissionsManager.hasPermission(event.getPlayer(), "challengemaps.admin"))
						{
							MapEntrance me = new MapEntrance();
							me.setMap(map);
							me.setSignLocation(event.getBlock().getLocation());
							me.setChestLocation(chestLoc);
							Current.MapEntrances.put(me.getSignLocation(), me);
							Current.MapEntrances.put(me.getChestLocation(), me);
							event.setLine(0, "[Challenge]");
							event.setLine(1, map.getMapName());
							event.setLine(2, ""); // high score
							event.setLine(3, ""); // high score player
							event.getPlayer().sendMessage("Map Entrance Successfully Created.");
							MapEntranceData.SaveMapEntrances();

						}
						else
						{
							event.getPlayer().sendMessage("You do not have permission to create map entrances.");
						}
					}
					else
					{
						event.getPlayer().sendMessage("The Block Below the Sign, Must be a Chest.");
					}
				}
				else
				{
					event.getPlayer().sendMessage("Invalid Challenge Map Name.");
				}
			}
		}
	}

	public @Override void onBlockBreak(BlockBreakEvent event)
	{
		if (!event.isCancelled())
		{
			MapEntrance me = Current.MapEntrances.get(Util.GetStringFromLocation(event.getBlock().getLocation()));
			if (me != null)
			{
				// Do not allow the breaking of entrances or their chests.
				event.setCancelled(true);
				if (event.getPlayer() != null)
				{
					event.getPlayer().sendMessage("You cannot remove a map entrance.");
				}
			}
			else
			{
				GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
				if (gs != null)
				{
					if (gs.getMap().getAllowBreaking())
					{
						Block blk = event.getBlock();
						gs.addBlockBreak(blk.getTypeId());
						gs.removeMapContents(blk.getTypeId());
					}
					else
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public @Override void onBlockPlace(BlockPlaceEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				if (gs.getMap().getAllowBuilding())
				{
					Block blk = event.getBlock();
					gs.addBlockPlace(blk.getTypeId());
					gs.addBlockPlaceDistance(blk.getTypeId(), blk.getLocation());
					gs.addMapContents(blk.getTypeId());
				}
				else
				{
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onBlockBurn(BlockBurnEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				gs.removeMapContents(event.getBlock().getTypeId());
			}
		}
	}

	public @Override void onBlockFade(BlockFadeEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				gs.removeMapContents(event.getBlock().getTypeId());
			}
		}
	}

	public @Override void onBlockForm(BlockFormEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				gs.removeMapContents(event.getBlock().getTypeId());
				gs.addMapContents(event.getNewState().getTypeId());
			}
		}
	}

	public @Override void onBlockFromTo(BlockFromToEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				if (event.getBlock().getTypeId() == event.getToBlock().getTypeId())
				{
					gs.addMapContents(event.getToBlock().getTypeId());
				}
				// Log.Write("From: " + event.getBlock().getTypeId());
				// Log.Write("To: " + event.getToBlock().getTypeId());
				// gs.addMapContents(event.getToBlock().getTypeId());
			}
		}
	}

	public @Override void onBlockSpread(BlockSpreadEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				gs.removeMapContents(event.getBlock().getTypeId());
				gs.addMapContents(event.getNewState().getTypeId());
			}
		}
	}

	public @Override void onLeavesDecay(LeavesDecayEvent event)
	{
		if (!event.isCancelled())
		{
			GameState gs = Current.getGameStateByWorld(event.getBlock().getWorld());
			if (gs != null)
			{
				gs.removeMapContents(event.getBlock().getTypeId());
			}
		}
	}
}