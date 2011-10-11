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

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.epicsagaonline.bukkit.ChallengeMaps.ChallengeMaps;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.GameStateData;
import com.epicsagaonline.bukkit.ChallengeMaps.integration.PermissionsManager;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.GameState;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.Map;
import com.epicsagaonline.bukkit.ChallengeMaps.objects.MapEntrance;

public class PlayerEvents extends PlayerListener
{
	public PlayerEvents(ChallengeMaps instance)
	{}

	public @Override void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!event.isCancelled())
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				MapEntrance me = Current.MapEntrances.get(Util.GetStringFromLocation(event.getClickedBlock().getLocation()));
				if (me != null)
				{
					if (PermissionsManager.hasPermission(event.getPlayer(), "challengemaps.enter"))
					{
						if (event.getClickedBlock().getTypeId() == 54)
						{
							// Chest - Populate user specific treasure.
							// event.getPlayer().sendMessage("Treasure!");
						}
						else if (event.getClickedBlock().getTypeId() == 68)
						{
							Player player = event.getPlayer();
							Location loc = Util.GetLocationFromString(Util.GetStringFromLocation(player.getLocation()));
							GameState gs = GameStateData.Load(me.getMap(), player, false);
							if (gs != null)
							{

								if (gs.canRespawn())
								{
									World world = Current.LoadWorld(gs, player);
									if (gs.getMap().getResetInventory())
									{
										gs.toggleInventory();
									}

									player.teleport(world.getSpawnLocation());
									player.sendMessage(gs.getMap().getEntranceText());

									gs.setEntryPoint(loc);
									gs.setInChallenge(true);

								}
								else
								{
									gs.PendingRemoval = true;
									event.getPlayer().sendMessage("You have died too many times, you cannot enter this challenge anymore.");
								}

							}
							else
							{
								event.getPlayer().sendMessage("You have died too many times, you cannot enter this challenge anymore.");
							}
							event.setCancelled(true);
						}
					}
					else
					{
						event.getPlayer().sendMessage("You cannot enter challenge maps.");
					}
				}
			}
		}
	}

	public @Override void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (!event.isCancelled())
		{
			String worldName = event.getPlayer().getWorld().getName();
			if (Current.GameWorlds.contains(worldName))
			{
				GameState gs = Current.GameStates.get(event.getPlayer().getName());
				if (gs != null)
				{
					if (!gs.getMap().getAllowCommands())
					{
						HashSet<String> allowedCommands = new HashSet<String>();
						allowedCommands.add("leave");
						allowedCommands.add("cm");
						allowedCommands.add("kill");
						String command = event.getMessage().split(" ")[0].toLowerCase().replace("/", "").trim();
						if (!allowedCommands.contains(command))
						{
							event.getPlayer().sendMessage("That command is not allowed while inside this challenge.");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	public @Override void onPlayerJoin(PlayerJoinEvent event)
	{
		String worldName = event.getPlayer().getWorld().getName();
		String mapName = worldName.replace("_" + event.getPlayer().getName(), "");
		if (!mapName.equals(worldName))
		{
			Map map = Current.Maps.get(mapName.toLowerCase());
			if (map != null)
			{
				GameState gs = Current.GameStates.get(event.getPlayer().getName());
				if (gs == null || !gs.getMap().getMapName().equalsIgnoreCase(mapName))
				{
					gs = GameStateData.Load(map, event.getPlayer(), false);
					if (gs != null)
					{

						if (gs.canRespawn())
						{
							World world = Current.LoadWorld(gs, event.getPlayer());
							if (gs.getMap().getResetInventory())
							{
								gs.toggleInventory();
							}

							event.getPlayer().teleport(world.getSpawnLocation());
							event.getPlayer().sendMessage(gs.getMap().getEntranceText());

							gs.setInChallenge(true);

						}
						else
						{
							gs.PendingRemoval = true;
						}

					}
					else
					{
						event.getPlayer().sendMessage("You have died too many times, you cannot enter this challenge anymore.");
					}
				}
			}
		}
		else
		{
			if (mapName.indexOf("_") > -1)
			{
				mapName = mapName.substring(0, mapName.indexOf("_"));
				Map map = Current.Maps.get(mapName.toLowerCase());
				if (map != null)
				{
					event.getPlayer().teleport(Util.GetLocationFromString(Current.getMapEntranceByMap(map).getSignLocation()));
				}
				else
				{
					event.getPlayer().teleport(Current.Plugin.getServer().getWorlds().get(0).getSpawnLocation());
				}
			}
		}
	}

	public @Override void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (!event.isCancelled())
		{
			String fromWorldName = event.getFrom().getWorld().getName();
			String toWorldName = event.getTo().getWorld().getName();
			if (!fromWorldName.equalsIgnoreCase(toWorldName))
			{
				if (Current.GameWorlds.contains(fromWorldName))
				{
					GameState gs = Current.GameStates.get(event.getPlayer().getName());
					if (gs != null)
					{
						gs.PendingRemoval = true;
					}
				}
			}
		}
	}

	public @Override void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		GameState gs = Current.GameStates.get(player.getName());
		if (gs != null)
		{
			if (gs.canRespawn())
			{
				String respawnWorld = event.getRespawnLocation().getWorld().getName();
				if (!gs.getWorld().getName().equalsIgnoreCase(respawnWorld))
				{
					event.setRespawnLocation(gs.getWorld().getSpawnLocation());
				}
				if (gs.getMap().getNumberOfLives() >= 0)
				{
					int livesLeft = gs.getMap().getNumberOfLives() - gs.getDeathCount();
					if (livesLeft > 0)
					{
						player.sendMessage("You Have " + livesLeft + " Lives Left.");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You Have No Lives Left.");
					}
				}
			}
			else
			{
				player.sendMessage("You have died too many times, sending you back home.");

				String worldName = player.getWorld().getName();

				event.setRespawnLocation(gs.getEntryPoint());
				gs.setInChallenge(false);

				if (gs.getMap().getResetInventory())
				{
					gs.toggleInventory();
				}

				Current.Plugin.getServer().unloadWorld(worldName, true);
				Current.GameWorlds.remove(worldName);
				gs.PendingRemoval = true;
			}
		}
	}
}
