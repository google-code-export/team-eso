package com.epicsagaonline.bukkit.EpicZones.CommandHandlers;

import org.bukkit.event.player.PlayerChatEvent;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;

public class ReloadCommandHandler {

	public static void Process(String[] data, PlayerChatEvent event, EpicZones plugin)
	{
		if(EpicZones.permissions.has(event.getPlayer(), "epiczones.admin"))
		{
			plugin.setupPermissions();
			plugin.setupHeroChat();
			plugin.setupEpicZones();
			event.getPlayer().sendMessage("EpicZones Reloaded.");
		}
	}

}
