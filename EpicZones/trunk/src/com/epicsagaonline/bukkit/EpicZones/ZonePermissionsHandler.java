package com.epicsagaonline.bukkit.EpicZones;


import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;

public class ZonePermissionsHandler 
{

	private static final String PERMS_IGNORE = "epiczones.ignorepermissions";
	private static final String PERMS_ROOT = "epiczones.";

	public static boolean hasPermissions(Player player, EpicZone zone, String flag)
	{
		if(!EpicZones.permissions.hasPermission(player, PERMS_IGNORE))
		{
			if(zone == null)
			{
				if(EpicZones.permissions.hasPermission(player, getPermNode(player.getWorld().getName(), flag, true)))
				{
					return false;
				}
				else if(EpicZones.permissions.hasPermission(player, getPermNode(player.getWorld().getName(), flag, false)))
				{
					return true;
				}
				else
				{
					return getDefaultPerm(flag);
				}
			}

			if(!zone.isOwner(player.getName()))
			{
				if(EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
				{
					//Has Deny Permission, Now Check For Specific Grant, and Global Deny (*)
					if(EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, false)) && EpicZones.permissions.hasPermission(player, getPermNode("*", flag, true)))
					{
						return true; //Has Deny Global, But Child Grant, So Override.
					}
					else
					{
						return false; //Does Not Have Child Grant.
					}
				}
				else if(EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, false)))
				{
					return true;
				}
				else if(zone.hasParent())
				{
					return hasPermissions(player, zone.getParent(), flag);
				}
				else if(EpicZones.permissions.hasPermission(player, getPermNode(player.getWorld().getName(), flag, true)))
				{
					return false;
				}
				else if(EpicZones.permissions.hasPermission(player, getPermNode(player.getWorld().getName(), flag, false)))
				{
					return true;
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

	private static String getPermNode(EpicZone zone, String flag, Boolean deny)
	{
		String result = "";
		String perm = "";

		if(deny)
		{
			perm = ".deny";
		}
		else
		{
			perm = ".allow";
		}

		result = PERMS_ROOT + zone.getTag() + "." + flag + perm;

		return result;
	}

	private static String getPermNode(String worldName, String flag, Boolean deny)
	{
		String result = "";
		String perm = "";

		if(deny)
		{
			perm = ".deny.";
		}
		else
		{
			perm = ".allow.";
		}

		result = PERMS_ROOT + worldName + "." + flag + perm;

		return result;
	}

//	private static String getZoneNode(EpicZone zone)
//	{
//		if(zone.hasParent())
//		{
//			return getZoneNode(zone.getParent()) + "." + zone.getTag();
//		}
//		else
//		{
//			return zone.getTag();
//		}
//
//	}

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
