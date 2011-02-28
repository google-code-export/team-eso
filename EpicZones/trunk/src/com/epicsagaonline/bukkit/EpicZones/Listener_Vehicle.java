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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class Listener_Vehicle extends VehicleListener 
{

	//private final EpicZones plugin;
	private final Vector zero = new Vector(0,0,0);

	public Listener_Vehicle(EpicZones instance)
	{
		//plugin = instance;
	}

	public @Override void onVehicleMove(VehicleMoveEvent event)
	{

		Vehicle vehicle = event.getVehicle();
		Entity passenger = vehicle.getPassenger();

		if(passenger != null)
		{
			EpicZonePlayer ezp = General.getPlayer(passenger.getEntityId());

			if(ezp != null)
			{
				int playerHeight = event.getTo().getBlockY();
				Point playerPoint = new Point(event.getTo().getBlockX(), event.getTo().getBlockZ());

				if(General.ShouldCheckPlayer(ezp))
				{
					if(!ezp.isTeleporting())
					{
						if(ezp.getCurrentLocation() == null){ezp.setCurrentLocation(event.getFrom());}
						if(!VehicleWithinZoneLogic((Player)passenger, ezp, playerHeight, playerPoint))
						{
							ezp.setIsTeleporting(true);
							vehicle.teleportTo(ezp.getCurrentLocation());
							vehicle.setVelocity(zero);
							ezp.setIsTeleporting(false);
							//event.setTo(ezp.getCurrentLocation());
							//event.setCancelled(true);
						}
						else
						{
							ezp.setCurrentLocation(event.getFrom());
						}
					}
					ezp.Check();
				}
			}
		}
	}

	public static boolean VehicleWithinZoneLogic(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint)
	{

		Zone foundZone = null;
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
						ezp.setCurrentZone(foundZone);
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

	private static Zone FindZone(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint, String worldName)
	{

		Zone result = null;

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
			result = General.getZoneForPoint(playerHeight, playerPoint, worldName);
		}

		return result;

	}
}