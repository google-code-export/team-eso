/*

This file is part of EpicZones

Copyright (C) 2011 by Team ESO

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

/**
* @author jblaske@gmail.com
* @license MIT License
*/

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

public class LEntity extends EntityListener 
{

	//private final EpicZones plugin;

	public LEntity(EpicZones instance)
	{
		//plugin = instance;
	}

	public @Override void onEntityExplode(EntityExplodeEvent event)
	{
		Zone zone = General.getZoneForPoint(event.getLocation().getBlockY(),new Point(event.getLocation().getBlockX(),event.getLocation().getBlockZ()), event.getLocation().getWorld().getName());
		if (zone != null)
		{
			if(!zone.getAllowExplode())
			{
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
					Zone zone = ezp.getCurrentZone();
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
					Zone zone = ezp.getCurrentZone();
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
		Zone zone = General.getZoneForPoint(event.getLocation().getBlockY(),new Point(event.getLocation().getBlockX(),event.getLocation().getBlockZ()), event.getLocation().getWorld().getName());

		if(zone != null)
		{
			if(!zone.getAllowedMobs().contains("all"))
			{
				if (zone.getAllowedMobs().contains("none") || !zone.getAllowedMobs().contains(mob.getClass().getName()))
				{
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
