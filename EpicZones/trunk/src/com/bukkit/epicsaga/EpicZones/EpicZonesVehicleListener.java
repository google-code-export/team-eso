package com.bukkit.epicsaga.EpicZones;

import java.awt.Point;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class EpicZonesVehicleListener extends VehicleListener 
{

	//private final EpicZones plugin;
	private final Vector zero = new Vector(0,0,0);

	public EpicZonesVehicleListener(EpicZones instance)
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

	private static EpicZone FindZone(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint, String worldName)
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
}