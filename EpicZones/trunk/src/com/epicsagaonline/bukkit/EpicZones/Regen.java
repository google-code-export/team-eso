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

import java.util.ArrayList;


import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class Regen implements Runnable {

	private final EpicZones plugin;
	private final int MAX_HEALTH = 20;
	private final int MIN_HEALTH = 0;

	Regen(final EpicZones instance)
	{
		this.plugin = instance;
	}

	public void run() {

		ArrayList<String> regenZoneTags = new ArrayList<String>();

		for(Player player: plugin.getServer().getOnlinePlayers())
		{
			if(player.getHealth() <= MAX_HEALTH && player.getHealth() > MIN_HEALTH)
			{
				EpicZonePlayer ezp = General.getPlayer(player.getName());
				if(ezp != null)
				{
					EpicZone zone = ezp.getCurrentZone();
					if(zone != null)
					{
						if(zone.timeToRegen())
						{
							if(ezp.getEnteredZone().before(zone.getAdjustedRegenDelay()))
							{
								if(zone.getRegen().getRestDelay() == 0 || (zone.getRegen().getRestDelay() > 0 && ezp.getLastMoved().before(zone.getAdjustedRestDelay())))
								{
									if(zone.getRegen().getAmount() >= 0)
									{
										int bonus = 0;
										if(player.isSleeping())
										{
											bonus = zone.getRegen().getBedBonus();
										}
										
										if(zone.getRegen().getMaxRegen() > 0)
										{
											if(player.getHealth() + zone.getRegen().getAmount() + bonus > zone.getRegen().getMaxRegen())
											{
												player.setHealth(zone.getRegen().getMaxRegen());
											}	
											else
											{
												player.setHealth(((player.getHealth() + zone.getRegen().getAmount() + bonus)));
											}	
										}
										else
										{
											if(player.getHealth() + zone.getRegen().getAmount() + bonus > MAX_HEALTH)
											{
												player.setHealth(MAX_HEALTH);
											}	
											else
											{
												player.setHealth(((player.getHealth() + zone.getRegen().getAmount() + bonus)));
											}
										}
									}
									else
									{
										if(zone.getRegen().getMinDegen() > 0)
										{
											if(player.getHealth() + zone.getRegen().getAmount() < zone.getRegen().getMinDegen())
											{
												player.setHealth(zone.getRegen().getMinDegen());
											}	
											else
											{
												player.setHealth(((player.getHealth() + zone.getRegen().getAmount())));
											}
										}
										else
										{
											if(player.getHealth() + zone.getRegen().getAmount() < MIN_HEALTH)
											{
												player.setHealth(MIN_HEALTH);
											}	
											else
											{
												player.setHealth(((player.getHealth() + zone.getRegen().getAmount())));
											}
										}
									}
								}
							}
							if(!regenZoneTags.contains(zone.getTag()))
							{
								regenZoneTags.add(zone.getTag());
							}
						}
					}
				}
			}
		}

		for(int i = 0; i < regenZoneTags.size(); i++)
		{
			General.myZones.get(regenZoneTags.get(i)).Regen();
		}
	}
}
