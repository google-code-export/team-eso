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

package listeners;

import java.awt.Point;

import objects.EpicZonePlayer;
import objects.EpicZone;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;

public class EntityEvents extends EntityListener 
{

	//private final EpicZones plugin;

	public EntityEvents(EpicZones instance)
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
					else
					{
						if(!General.config.defaultPVP)
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
		if(!event.isCancelled() && isCreature(event.getCreatureType()))
		{

			Entity mob = event.getEntity();
			EpicZone zone = General.getZoneForPoint(event.getLocation().getBlockY(),new Point(event.getLocation().getBlockX(),event.getLocation().getBlockZ()), event.getLocation().getWorld().getName());

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
	}

	private boolean isCreature(CreatureType ct)
	{
		boolean result = false;

		if(ct != null)
		{
			switch(ct)
			{
			case CHICKEN:
				result = true;
				break;
			case COW:
				result = true;
				break;
			case CREEPER:
				result = true;
				break;
			case GHAST:
				result = true;
				break;
			case GIANT:
				result = true;
				break;
			case PIG:
				result = true;
				break;
			case PIG_ZOMBIE:
				result = true;
				break;
			case SHEEP:
				result = true;
				break;
			case SKELETON:
				result = true;
				break;
			case SLIME:
				result = true;
				break;
			case SPIDER:
				result = true;
				break;
			case SQUID:
				result = true;
				break;
			case ZOMBIE:
				result = true;
				break;
			}
		}

		return result;
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
