package com.epicsagaonline.bukkit.EpicZones;

import java.awt.Point;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class EpicZonesEntityListener extends EntityListener 
{

	//private final EpicZones plugin;

	public EpicZonesEntityListener(EpicZones instance)
	{
		//plugin = instance;
	}

	public @Override void onEntityExplode(EntityExplodeEvent event)
	{
		EpicZone zone = General.getZoneForPoint(event.getLocation().getBlockY(),new Point(event.getLocation().getBlockX(),event.getLocation().getBlockZ()), event.getLocation().getWorld().getName());
		if (zone != null)
		{
			if(!zone.getAllowExplode())
			{
				//System.out.println("Cancel Explosion [" + zone.getTag() + "]");
				event.setCancelled(true);
			}
		}
	}
	
	public @Override void onEntityDamage(EntityDamageEvent event)
	{
		if(event.getCause() == DamageCause.ENTITY_ATTACK)
		{
			if (event instanceof EntityDamageByEntityEvent) 
			{
				EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
				if(isPlayer(sub.getEntity()) && isPlayer(sub.getDamager()))
				{
					EpicZonePlayer ezp = General.getPlayer(sub.getEntity().getEntityId());
					EpicZone zone = ezp.getCurrentZone();
					if(zone != null)
					{
						if(!zone.hasPVP())
						{
							event.setCancelled(true);
						}
					}
				}
			}
			else if (event instanceof EntityDamageByProjectileEvent)
			{
				EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
				if(isPlayer(sub.getEntity()) && isPlayer(sub.getDamager()))
				{
					EpicZonePlayer ezp = General.getPlayer(sub.getEntity().getEntityId());
					EpicZone zone = ezp.getCurrentZone();
					if(zone != null)
					{
						if(!zone.hasPVP())
						{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	public @Override void onCreatureSpawn(CreatureSpawnEvent event)
	{

		Entity mob = event.getEntity();
		EpicZone zone = General.getZoneForPoint(event.getLocation().getBlockY(),new Point(event.getLocation().getBlockX(),event.getLocation().getBlockZ()), event.getLocation().getWorld().getName());

		if(zone != null)
		{
			if(!zone.getAllowedMobs().contains("all"))
			{
				if (zone.getAllowedMobs().contains("none") || !zone.getAllowedMobs().contains(mob.getClass().getName()))
				{
					//System.out.println("Creature Class:" + event.getEntity().getClass().getName() + " Not Allowed Within " + zone.getTag());
					event.setCancelled(true);
				}
			}
		}
	}

	private boolean isPlayer(Entity entity)
	{

		boolean result = false;

		if(General.getPlayer(entity.getEntityId()) != null)
		{
			result = true;
		}
		else
		{
			result = false;
		}

		return result;

	}
}
