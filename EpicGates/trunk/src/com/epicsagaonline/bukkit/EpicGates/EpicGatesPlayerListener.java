package com.epicsagaonline.bukkit.EpicGates;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author jblaske
 */
public class EpicGatesPlayerListener extends PlayerListener
{
	//private final EpicGates plugin;

	public EpicGatesPlayerListener(EpicGates instance)
	{
		//plugin = instance;
	}

	public @Override void onPlayerMove(PlayerMoveEvent event)
	{

		Player player = event.getPlayer();
		EpicGatesPlayer egp = General.myPlayers.get(player.getName()); 
		if(egp != null)
		{
			if(egp.shouldCheck())
			{
				egp.Check();
				EpicGate gate = GetGateForPlayerLocation(event.getTo());
				if(gate != null)
				{
					Warp(event, gate, egp);
					egp.Teleported();
				}
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

	public @Override void onPlayerLogin(PlayerLoginEvent event)
	{
		if(event.getResult() == Result.ALLOWED)
		{
			General.addPlayer( event.getPlayer().getName());
		}
	}

	public @Override void onPlayerQuit(PlayerEvent event)
	{
		General.removePlayer(event.getPlayer().getName());
	}


	private void Warp(PlayerMoveEvent event, EpicGate gate, EpicGatesPlayer egp)
	{	
		if(gate != null && gate.getTarget() != null)
		{
			if(gate.getTarget().getTargetTag().length() > 0 && !gate.getTag().equals(gate.getTarget().getTargetTag())  && egp.getLoopCount() < 8)
			{
				egp.Looped();
				Warp(event, gate.getTarget(), egp);
			}
			else
			{
				event.getPlayer().teleportTo(gate.getTarget().getLanding());
				event.setTo(gate.getTarget().getLanding());
				egp.setLoopCount(0);
			}
		}
	}

	private EpicGate GetGateForPlayerLocation(Location loc)
	{
		for(String gateTag: General.myGateTags)
		{
			EpicGate gate = General.myGates.get(gateTag);
			if(gate.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
			{
				if(gate.getTargetTag().length() > 0)
				{
					if(PlayerWithinGate(gate, loc))
					{
						return gate;
					}
				}
			}
		}
		return null;
	}

	private boolean PlayerWithinGate(EpicGate gate, Location playerLoc)
	{

		boolean result = false;
		
		if ((int)gate.getLocation().getBlockY() == (int)playerLoc.getBlockY())
		{
			if(playerLoc.getX() <= Math.ceil(gate.getLocation().getX()) && playerLoc.getX() >= Math.floor(gate.getLocation().getX()))
			{
				if(playerLoc.getZ() <= Math.ceil(gate.getLocation().getZ()) && playerLoc.getZ() >= Math.floor(gate.getLocation().getZ()))					{
					result = true;
				}
			}
		}

		return result;
	}
}