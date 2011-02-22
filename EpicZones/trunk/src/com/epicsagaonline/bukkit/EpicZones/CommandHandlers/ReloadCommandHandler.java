package com.epicsagaonline.bukkit.EpicZones.CommandHandlers;

import org.bukkit.event.player.PlayerChatEvent;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;

public class ReloadCommandHandler {

	public static void Process(String[] data, PlayerChatEvent event, EpicZones plugin)
	{
		if(EpicZones.permissions.has(event.getPlayer(), "epiczones.admin"))
		{
			try 
			{
				plugin.setupPermissions();
				plugin.setupHeroChat();
				General.config.load();
				General.config.save();
				General.loadZones(null);
				event.getPlayer().sendMessage("EpicZones Reloaded.");
				event.setCancelled(true);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

}
