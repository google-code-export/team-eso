/*

        This file is part of EpicGates

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

package com.epicsagaonline.bukkit.EpicGates.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.epicsagaonline.bukkit.EpicGates.EpicGates;
import com.epicsagaonline.bukkit.EpicGates.General;
import com.epicsagaonline.bukkit.EpicGates.objects.EpicGate;
import com.epicsagaonline.bukkit.EpicGates.objects.EpicGatesPlayer;

/**
 * Handle events for all Player related events
 * 
 * @author jblaske
 */
public class PlayerEvents extends PlayerListener
{
	// private final EpicGates plugin;

	public PlayerEvents(EpicGates instance)
	{
		// plugin = instance;
	}

	public @Override void onPlayerMove(PlayerMoveEvent event)
	{

		Player player = event.getPlayer();
		EpicGatesPlayer egp = General.myPlayers.get(player.getName());
		if (egp != null)
		{
			if (egp.shouldCheck())
			{
				egp.Check();
				EpicGate gate = GetGateForPlayerLocation(event.getTo());
				if (gate != null)
				{
					if (gate.isAllowed(player))
					{
						Warp(event, gate, egp);
						egp.Teleported();
					}
				}
			}
		}
	}

	public @Override void onPlayerLogin(PlayerLoginEvent event)
	{
		if (event.getResult() == Result.ALLOWED)
		{
			General.addPlayer(event.getPlayer().getName());
		}
	}

	public @Override void onPlayerQuit(PlayerQuitEvent event)
	{
		General.removePlayer(event.getPlayer().getName());
	}

	private void Warp(PlayerMoveEvent event, EpicGate gate, EpicGatesPlayer egp)
	{
		if (gate != null && gate.getTarget() != null)
		{
			event.getPlayer().teleport(gate.getTarget().getLanding());
			event.setTo(gate.getTarget().getLanding());
			egp.setLoopCount(0);
		}
	}

	private EpicGate GetGateForPlayerLocation(Location loc)
	{
		for (String gateTag : General.myGateTags)
		{
			EpicGate gate = General.myGates.get(gateTag);
			if (gate != null)
			{
				if (gate.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
				{
					if (gate.getTargetTag().length() > 0)
					{
						if (PlayerWithinGate(gate, loc))
						{
							return gate;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean PlayerWithinGate(EpicGate gate, Location playerLoc)
	{

		boolean result = false;

		if ((int) gate.getLocation().getBlockY() == (int) playerLoc.getBlockY())
		{
			if (playerLoc.getX() <= Math.ceil(gate.getLocation().getX()) && playerLoc.getX() >= Math.floor(gate.getLocation().getX()))
			{
				if (playerLoc.getZ() <= Math.ceil(gate.getLocation().getZ()) && playerLoc.getZ() >= Math.floor(gate.getLocation().getZ()))
				{
					result = true;
				}
			}
		}

		return result;
	}
}