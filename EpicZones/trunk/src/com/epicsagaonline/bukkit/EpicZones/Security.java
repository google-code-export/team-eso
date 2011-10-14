package com.epicsagaonline.bukkit.EpicZones;

import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class Security 
{
	public static void UpdatePlayerSecurity(Player player)
	{
		EpicZonePlayer ezp = General.myPlayers.get(player.getName().toLowerCase());
		if(EpicZones.permissions != null)
		{
			if(EpicZones.permissions.hasPermission(player, "epiczones.admin"))
			{
				ezp.setAdmin(true);
			}
			else if(player.isOp())
			{
				ezp.setAdmin(true);
			}
			else
			{
				ezp.setAdmin(false);
			}
		}
	}
}
