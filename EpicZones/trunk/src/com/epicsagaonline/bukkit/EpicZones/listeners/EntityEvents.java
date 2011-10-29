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

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.awt.*;

public class EntityEvents extends EntityListener
{

    public
    @Override
    void onEntityExplode(EntityExplodeEvent event)
    {
        EpicZone zone = General.GetZoneForPlayer(null, event.getLocation().getWorld().getName(), event.getLocation().getBlockY(), new Point(event.getLocation().getBlockX(), event.getLocation().getBlockZ()));
        if (zone != null)
        {
            if (event.getEntity().toString().equalsIgnoreCase("CraftTNTPrimed"))
            {
                if (!zone.getExplode().getTNT())
                {
                    event.setYield(0);
                    event.setCancelled(true);
                }
            }
            else if (event.getEntity().toString().equalsIgnoreCase("CraftCreeper"))
            {
                if (!zone.getExplode().getCreeper())
                {
                    event.setYield(0);
                    event.setCancelled(true);
                }
            }
            else if (event.getEntity().toString().equalsIgnoreCase("CraftFireball"))
            {
                if (!zone.getExplode().getGhast())
                {
                    event.setYield(0);
                    event.setCancelled(true);
                }
            }
        }
    }

    public
    @Override
    void onEntityCombust(EntityCombustEvent event)
    {
        if (!event.isCancelled())
        {
            Entity e = event.getEntity();
            EpicZone zone = General.GetZoneForPlayer(null, e.getLocation().getWorld().getName(), e.getLocation().getBlockY(), new Point(e.getLocation().getBlockX(), e.getLocation().getBlockZ()));
            if (zone != null)
            {
                if (!zone.getFire().getIgnite())
                {
                    if (isPlayer(e))
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

    public
    @Override
    void onEntityDamage(EntityDamageEvent event)
    {
        if (!event.isCancelled())
        {
            Entity e = event.getEntity();
            EpicZone sancZone = General.GetZoneForPlayer(null, e.getLocation().getWorld().getName(), e.getLocation().getBlockY(), new Point(e.getLocation().getBlockX(), e.getLocation().getBlockZ()));
            if ((sancZone != null && !sancZone.getSanctuary()) || sancZone == null)
            {
                if (event.getCause() == DamageCause.ENTITY_ATTACK)
                {
                    if (event instanceof EntityDamageByEntityEvent)
                    {
                        EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
                        if (isPlayer(sub.getEntity()) && isPlayer(sub.getDamager()))
                        {
                            Player player = (Player) sub.getEntity();
                            EpicZonePlayer ezp = General.getPlayer(player.getName());
                            EpicZone zone = ezp.getCurrentZone();
                            if (zone != null)
                            {
                                if (!zone.getPVP())
                                {
                                    event.setCancelled(true);
                                }
                            }
                            else
                            {
                                if (!General.myGlobalZones.get(e.getWorld().getName().toLowerCase()).getPVP())
                                {
                                    event.setCancelled(true);
                                }
                            }
                        }
                        else if (sub.getDamager().toString().equalsIgnoreCase("CraftGhast"))
                        {
                            if (sancZone != null)
                            {
                                if (!sancZone.getExplode().getGhast())
                                {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
                else if (event.getCause() == DamageCause.BLOCK_EXPLOSION)
                {
                    if (sancZone != null)
                    {
                        if (!sancZone.getExplode().getTNT())
                        {
                            event.setCancelled(true);
                        }
                    }
                }
                else if (event.getCause() == DamageCause.ENTITY_EXPLOSION)
                {
                    if (sancZone != null)
                    {
                        if (!sancZone.getExplode().getCreeper())
                        {
                            event.setCancelled(true);
                        }
                    }
                }
                else if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK)
                {
                    if (sancZone != null)
                    {
                        if (!sancZone.getFire().getIgnite())
                        {
                            if (isPlayer(e))
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
                if (isPlayer(e))
                {
                    e.setFireTicks(0);
                    event.setCancelled(true);
                }
            }
        }
    }

    public
    @Override
    void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!event.isCancelled() && isCreature(event.getCreatureType()) && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
        {
            EpicZone zone = General.GetZoneForPlayer(null, event.getLocation().getWorld().getName(), event.getLocation().getBlockY(), new Point(event.getLocation().getBlockX(), event.getLocation().getBlockZ()));
            if (zone != null)
            {
                if (zone.getMobs() != null) //If null assume all
                {
                    if (zone.getMobs().size() > 0) //If size = 0 assume all
                    {
                        if (!zone.getMobs().contains("ALL"))
                        {
                            if (zone.getMobs().contains("NONE") || !zone.getMobs().contains(event.getCreatureType().toString()))
                            {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isCreature(CreatureType ct)
    {
        boolean result = false;

        if (ct != null)
        {
            switch (ct)
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

        if (entity instanceof Player)
        {
            Player player = (Player) entity;
            if (General.getPlayer(player.getName()) != null)
            {
                result = true;
            }
        }

        return result;

    }
}
