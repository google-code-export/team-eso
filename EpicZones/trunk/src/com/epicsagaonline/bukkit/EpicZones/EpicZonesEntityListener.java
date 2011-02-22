package com.epicsagaonline.bukkit.EpicZones;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

public class EpicZonesEntityListener extends EntityListener 
{

	//private final EpicZones plugin;

	public EpicZonesEntityListener(EpicZones instance)
	{
		//plugin = instance;
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
