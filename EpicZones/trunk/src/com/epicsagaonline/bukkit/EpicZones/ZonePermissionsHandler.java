package com.epicsagaonline.bukkit.EpicZones;

import com.epicsagaonline.bukkit.EpicZones.integration.PermissionsManager;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone.ZoneType;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePermission.PermType;
import org.bukkit.entity.Player;

public class ZonePermissionsHandler
{

    private static final String PERMS_IGNORE = "epiczones.ignorepermissions";

    public static boolean hasPermissions(Player player, EpicZone zone, String flag)
    {
        try
        {
            if (!PermissionsManager.hasPermission(player, PERMS_IGNORE))
            {
                if (!zone.isOwner(player.getName()))
                {
                    if (zone.hasPermission(player, flag, PermType.DENY)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return false;
                    }
                    if (zone.hasPermission(player, flag, PermType.ALLOW)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return true;
                    }
                    if (zone.hasPermissionFromGroup(player, flag, PermType.DENY)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return false;
                    }
                    if (zone.hasPermissionFromGroup(player, flag, PermType.ALLOW)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
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
                        if (flag.equalsIgnoreCase("build"))
                        {
                            return Config.globalZoneDefaultBuild;
                        }
                        else if (flag.equalsIgnoreCase("destroy"))
                        {
                            return Config.globalZoneDefaultDestroy;
                        }
                        else //If for some reason anything other than build, deny or enter come thru, its going to default to the enter permission.
                        {
                            return Config.globalZoneDefaultEnter;
                        }
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
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
            return false;
        }
    }
}
