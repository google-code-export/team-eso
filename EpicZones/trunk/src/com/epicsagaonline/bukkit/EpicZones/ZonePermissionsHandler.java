package com.epicsagaonline.bukkit.EpicZones;

import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone.ZoneType;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePermission.PermType;

public class ZonePermissionsHandler
{

	private static final String PERMS_IGNORE = "epiczones.ignorepermissions";

	public static boolean hasPermissions(Player player, EpicZone zone, String flag)
	{
		try
		{
			if (!EpicZones.permissions.hasPermission(player, PERMS_IGNORE))
			{
				if (!zone.isOwner(player.getName()))
				{
					if (zone.hasPermission(player, flag, PermType.DENY)) // EpicZones.permissions.hasPermission(player,
																			// getPermNode(zone,
																			// flag,
																			// true)))
					{
						return false;
					}
					if (zone.hasPermission(player, flag, PermType.ALLOW)) // EpicZones.permissions.hasPermission(player,
																			// getPermNode(zone,
																			// flag,
																			// true)))
					{
						return true;
					}
					if (zone.hasPermissionFromGroup(player, flag, PermType.DENY)) // EpicZones.permissions.hasPermission(player,
																					// getPermNode(zone,
																					// flag,
																					// true)))
					{
						return false;
					}
					if (zone.hasPermissionFromGroup(player, flag, PermType.ALLOW)) // EpicZones.permissions.hasPermission(player,
																					// getPermNode(zone,
																					// flag,
																					// true)))
					{
						return true;
					}
					if (zone.hasParent())
					{
						return hasPermissions(player, zone.getParent(), flag);
					}
					if (zone.getType() != ZoneType.GLOBAL)
					{
						return hasPermissions(player, General.myGlobalZones.get(player.getWorld().getName().toLowerCase()), flag);
					}
					else
					{
						return Config.globalZoneDefaultAllow;
					}
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		catch (Exception e)
		{
			Log.Write(e.getMessage());
			return false;
		}
	}
}
