package com.epicsagaonline.bukkit.EpicZones;

import com.epicsagaonline.bukkit.EpicZones.integration.PermissionsManager;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import org.bukkit.entity.Player;

public class Security
{
    public static void UpdatePlayerSecurity(Player player)
    {
        EpicZonePlayer ezp = General.myPlayers.get(player.getName().toLowerCase());
        if (PermissionsManager.hasPermission(player, "epiczones.admin"))
        {
            ezp.setAdmin(true);
        }
        else if (player.isOp())
        {
            ezp.setAdmin(true);
        }
        else
        {
            ezp.setAdmin(false);
        }
    }
}
