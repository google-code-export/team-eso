package com.epicsagaonline.bukkit.EpicZones;

import java.awt.Point;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * EpicZones block listener
 * @author jblaske
 */
public class EpicZonesBlockListener extends BlockListener {
	//private final EpicZones plugin;
	private static final String NO_PERM_DESTROY = "You do not have permissions to destroy in this zone.";
	private static final String NO_PERM_DESTROY_BORDER = "You do not have permissions to destroy outside the border of the map.";
	private static final String NO_PERM_BUILD = "You do not have permissions to build in this zone.";
	private static final String NO_PERM_BUILD_BORDER = "You do not have permissions to build outside the border of the map.";

	public EpicZonesBlockListener(final EpicZones plugin) {
		//this.plugin = plugin;
	}

	public @Override void onBlockIgnite(BlockIgniteEvent event)
	{
		EpicZone zone = General.getZoneForPoint(event.getBlock().getLocation().getBlockY(),new Point(event.getBlock().getLocation().getBlockX(),event.getBlock().getLocation().getBlockZ()), event.getBlock().getLocation().getWorld().getName());
		if (zone != null)
		{
			if(!zone.getAllowFire())
			{
				//System.out.println("Cancel Ignite [" + zone.getTag() + "]");
				event.setCancelled(true);
			}
		}
	}
	
	public @Override void onBlockBurn(BlockBurnEvent event)
	{
		EpicZone zone = General.getZoneForPoint(event.getBlock().getLocation().getBlockY(),new Point(event.getBlock().getLocation().getBlockX(),event.getBlock().getLocation().getBlockZ()), event.getBlock().getLocation().getWorld().getName());
		if (zone != null)
		{
			if(!zone.getAllowFire())
			{
				//System.out.println("Cancel Burn [" + zone.getTag() + "]");
				event.setCancelled(true);
			}
		}
	}
	
	public @Override void onBlockDamage(BlockDamageEvent event)
	{
		
		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
		String worldName = player.getWorld().getName();
		int blockHeight = event.getBlock().getLocation().getBlockY();
		boolean hasPerms = false;
		EpicZone currentZone = null;

		if(General.pointWithinBorder(blockPoint, player))
		{
			currentZone = General.getZoneForPoint(blockHeight, blockPoint, worldName);
			hasPerms = General.hasPermissions(player, currentZone, "destroy");

			if(!hasPerms)
			{
				if (ezp.getLastWarned().before(new Date())){
					player.sendMessage(NO_PERM_DESTROY);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}
		else
		{
			if (ezp.getLastWarned().before(new Date())){
				player.sendMessage(NO_PERM_DESTROY_BORDER);
				ezp.Warn();
			}
			event.setCancelled(true);
		}
	}

	public @Override void onBlockPlace(BlockPlaceEvent event)
	{

		Player player = event.getPlayer();
		EpicZonePlayer ezp = General.getPlayer(player.getName());
		Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
		String worldName = player.getWorld().getName();
		int blockHeight = event.getBlock().getLocation().getBlockY();
		boolean hasPerms = false;

		EpicZone currentZone = null;

		if(General.pointWithinBorder(blockPoint, player))
		{
			currentZone = General.getZoneForPoint(blockHeight, blockPoint, worldName);
			hasPerms = General.hasPermissions(player, currentZone, "build");

			if(!hasPerms)
			{
				if (ezp.getLastWarned().before(new Date())){
					player.sendMessage(NO_PERM_BUILD);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		}
		else
		{
			if (ezp.getLastWarned().before(new Date())){
				player.sendMessage(NO_PERM_BUILD_BORDER);
				ezp.Warn();
			}
			event.setCancelled(true);
		}
	}

}
