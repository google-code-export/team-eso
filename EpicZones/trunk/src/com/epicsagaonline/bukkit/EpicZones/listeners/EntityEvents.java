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

package com.epicsagaonline.bukkit.EpicZones.listeners;

import java.awt.Point;


import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

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
			if(!zone.getExplode())
			{
				event.setCancelled(true);
			}
		}
	}

	public @Override void onEntityCombust(EntityCombustEvent event) 
	{
		if(!event.isCancelled())
		{
			Entity e = event.getEntity();
			EpicZone zone = General.getZoneForPoint(e.getLocation().getBlockY(),new Point(e.getLocation().getBlockX(),e.getLocation().getBlockZ()), e.getLocation().getWorld().getName());
			if(zone != null)
			{
				if(!zone.getFire())
				{
					if(isPlayer(e))
					{
						e.setFireTicks(0);
						event.setCancelled(true);
					}
					else if (!zone.getFireBurnsMobs())
					{
						e.setFireTicks(0);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public @Override void onEntityDamage(EntityDamageEvent event)
	{
		if(!event.isCancelled())
		{
			Entity e = event.getEntity();
			EpicZone sancZone = General.getZoneForPoint(e.getLocation().getBlockY(),new Point(e.getLocation().getBlockX(),e.getLocation().getBlockZ()), e.getLocation().getWorld().getName());
			if((sancZone != null && !sancZone.getSanctuary()) || sancZone == null)
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
								if(!zone.getPVP())
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
								if(!zone.getPVP())
								{
									event.setCancelled(true);
								}
							}
						}
					}
				}
				else if(event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION)
				{
					if(sancZone != null)
					{
						if(!sancZone.getExplode())
						{
							event.setCancelled(true);
						}
					}
				}
				else if(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK)
				{
					if(sancZone != null)
					{
						if(!sancZone.getFire())
						{
							if(isPlayer(e))
							{
								e.setFireTicks(0);
								event.setCancelled(true);
							}
							else if (!sancZone.getFireBurnsMobs())
							{
								e.setFireTicks(0);
								event.setCancelled(true);
							}
						}
					}
				}
			}
			else //This is a sanctuary zone, no damage allowed to players.
			{
				if(isPlayer(e))
				{
					e.setFireTicks(0);
					event.setCancelled(true);
				}
			}
		}
	}

	public @Override void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if(!event.isCancelled() && isCreature(event.getCreatureType()))
		{

			EpicZone zone = General.getZoneForPoint(event.getLocation().getBlockY(),new Point(event.getLocation().getBlockX(),event.getLocation().getBlockZ()), event.getLocation().getWorld().getName());

			if(zone != null)
			{
				if(!zone.getMobs().contains("all"))
				{
					if (zone.getMobs().contains("none") || !zone.getMobs().contains(event.getCreatureType().toString()))
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
