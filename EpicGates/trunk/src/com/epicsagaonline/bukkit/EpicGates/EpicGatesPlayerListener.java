package com.epicsagaonline.bukkit.EpicGates;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author jblaske
 */
public class EpicGatesPlayerListener extends PlayerListener
{
	//private final EpicGates plugin;
	private int loopCount = 0;

	public EpicGatesPlayerListener(EpicGates instance)
	{
		//plugin = instance;
	}

	public @Override void onPlayerMove(PlayerMoveEvent event)
	{
		if(EpicGates.permissions.has(event.getPlayer(), "epicgates.use"))
		{
			EpicGate gate = GetGateForPlayerLocation(event.getTo());
			if(gate != null)
			{
				Warp(event, gate);
			}
		}
	}

	public @Override void onPlayerCommand(PlayerChatEvent event)
	{
		if(!event.isCancelled())
		{
			String[] split = event.getMessage().split("\\s");
			if (split[0].equalsIgnoreCase("/epicgates")){}
		}
	}

	private void Warp(PlayerMoveEvent event, EpicGate gate)
	{	
		if(gate != null && gate.getTarget() != null)
		{
			if(gate.getTarget().getTargetTag().length() > 0 && loopCount < 5)
			{
				loopCount++;
				Warp(event, gate.getTarget());
			}
			else
			{
				event.getPlayer().teleportTo(gate.getTarget().getLocation());
				event.setTo(gate.getTarget().getLocation());
				loopCount = 0;
			}
		}
	}

	private EpicGate GetGateForPlayerLocation(Location loc)
	{
		for(String gateTag: General.myGateTags)
		{
			EpicGate gate = General.myGates.get(gateTag);
			if(gate.getLocation().getWorld().getName().equals(loc.getWorld().getName()))
			{
				if(gate.getTargetTag().length() > 0)
				{
					if(gate.getLocation().getBlockX() == loc.getBlockX()
							&& gate.getLocation().getBlockY() == loc.getBlockY()
							&& gate.getLocation().getBlockZ() == loc.getBlockZ())
					{
						return gate;
					}
				}
			}
		}
		return null;
	}
}