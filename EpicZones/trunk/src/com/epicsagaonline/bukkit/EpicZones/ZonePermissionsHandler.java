package com.epicsagaonline.bukkit.EpicZones;


import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePermission.PermType;

public class ZonePermissionsHandler 
{

	private static final String PERMS_IGNORE = "epiczones.ignorepermissions";

	public static boolean hasPermissions(Player player, EpicZone zone, String flag)
	{
		if(!EpicZones.permissions.hasPermission(player, PERMS_IGNORE))
		{
			if(zone == null)
			{
				return getDefaultPerm(flag);
			}

			if(!zone.isOwner(player.getName()))
			{
				if(zone.hasPermission(player, flag, PermType.DENY)) //  EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
				{
					return false;
				}
				if(zone.hasPermission(player, flag, PermType.ALLOW)) //  EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
				{
					return true;
				}
				else if(zone.hasParent())
				{
					return hasPermissions(player, zone.getParent(), flag);
				}
				else
				{
					return getDefaultPerm(flag);	
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

	private static boolean getDefaultPerm(String flag)
	{
		if(flag.equals("entry"))
			return General.config.defaultEnter;
		if(flag.equals("destroy"))
			return General.config.defaultDestroy;
		if(flag.equals("build"))
			return General.config.defaultBuild;

		return false;
	}

}
