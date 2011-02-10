package com.bukkit.epicsaga.EpicZones.CommandHandlers;

import java.io.FileNotFoundException;

import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.epicsaga.EpicZones.EpicZones;
import com.bukkit.epicsaga.EpicZones.General;

public class ReloadCommandHandler {

	public static void Process(String[] data, PlayerChatEvent event)
	{
		if(EpicZones.permissions.has(event.getPlayer(), "epiczones.admin"))
		{
			General.config.load();
			General.loadZones(null);
			event.getPlayer().sendMessage("EpicZones Reloaded.");
			event.setCancelled(true);
		}
	}

}
