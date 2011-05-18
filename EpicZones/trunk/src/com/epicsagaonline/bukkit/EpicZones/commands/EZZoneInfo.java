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

package com.epicsagaonline.bukkit.EpicZones.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePermission;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class EZZoneInfo 
{
	public EZZoneInfo(String[] data, CommandSender sender)
	{
		if(sender instanceof Player)
		{

			Player player = (Player)sender;
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			if(data.length > 1)
			{
				EpicZone zone = General.myZones.get(data[1].trim());
				if (zone != null)
				{
					if(ezp.getAdmin() || zone.isOwner(ezp.getName()))
					{
						String messageText;
						Message.Send(sender, 5, new String[]{zone.getName(), zone.getTag()});
						if(zone.getCenter() != null)
						{
							Message.Send(sender, 121, new String[]{zone.getRadius() + ""});
						}
						else
						{
							Message.Send(sender, 122, new String[]{zone.getPolygon().npoints + ""});
						}
						if(zone.hasChildren())
						{
							messageText = "";
							for(String childTag: zone.getChildren().keySet())
							{
								messageText = messageText + " " + childTag;
							}
							Message.Send(sender, 123, new String[]{messageText});
						}
						Message.Send(sender, 124, new String[]{zone.getEnterText()});
						Message.Send(sender, 125, new String[]{zone.getExitText()});
						if(zone.hasParent())
						{
							Message.Send(sender, 126, new String[]{zone.getParent().getName(), zone.getParent().getTag()});
						}
						if(zone.getOwners().size() > 0)
						{
							Message.Send(sender, 127, new String[]{zone.getOwners().toString()});
						}
						Message.Send(sender, 38);
						messageText = "";
						if(zone.getPVP())
						{
							messageText = messageText + Message.get(2, new String[]{"PVP"}) + " ";
						}
						else
						{
							messageText = messageText + Message.get(3, new String[]{"PVP"}) + " ";
						}
						if(zone.getFire().getIgnite() || zone.getFire().getSpread())
						{
							messageText = messageText + Message.get(2, new String[]{"FIRE"}) + " (";
							if(zone.getFire().getIgnite())
							{
								messageText = messageText + "Ignite ";
							}
							if(zone.getFire().getSpread())
							{
								messageText = messageText + "Spread ";
							}
							messageText = messageText.trim() + ") ";
						}
						else
						{
							messageText = messageText + Message.get(3, new String[]{"FIRE"}) + " ";
						}

						if(zone.getExplode().getTNT() || zone.getExplode().getCreeper() || zone.getExplode().getGhast())
						{
							messageText = messageText + Message.get(2, new String[]{"EXPLODE"}) + " (";
							if(zone.getExplode().getTNT())
							{
								messageText = messageText + "TNT ";
							}
							if(zone.getExplode().getCreeper())
							{
								messageText = messageText + "Creeper ";
							}
							if(zone.getExplode().getGhast())
							{
								messageText = messageText + "Ghast ";
							}
							messageText = messageText.trim() + ") ";
						}
						else
						{
							messageText = messageText + Message.get(3, new String[]{"EXPLODE"}) + " ";
						}

						if(zone.getSanctuary())
						{
							messageText = messageText + Message.get(2, new String[]{"SANCTUARY"}) + " ";
						}
						else
						{
							messageText = messageText + Message.get(3, new String[]{"SANCTUARY"}) + " ";
						}
						Message.Send(sender, messageText);
						messageText = "";
						if(zone.getFireBurnsMobs())
						{
							messageText = messageText + Message.get(2, new String[]{"FIREBURNSMOBS"}) + " ";
						}
						else
						{
							messageText = messageText + Message.get(3, new String[]{"FIREBURNSMOBS"}) + " ";
						}
						if(zone.hasRegen())
						{
							messageText = messageText + Message.get(118, new String[]{zone.getRegen().getDelay() + "", zone.getRegen().getAmount() + "", zone.getRegen().getInterval() + ""});
						}
						else
						{
							messageText = messageText + Message.get(3, new String[]{"REGEN"});
						}
						Message.Send(sender, messageText);
						messageText = "";
						for(String mobType: zone.getMobs())
						{
							messageText = messageText + " " + mobType;
						}
						Message.Send(sender, 119, new String[]{messageText});
						Message.Send(sender, 39);
						for(String permKey : zone.getPermissions().keySet())
						{
							EpicZonePermission perm = zone.getPermissions().get(permKey);
							Message.Send(sender, 120, new String[]{perm.getMember(), perm.getNode().toString(), perm.getPermission().toString()});
						}
					}
					else
					{
						Message.Send(sender, 37);
					}
				}
				else
				{
					Message.Send(sender, 117, new String[]{data[1]});
				}
			}
		}
		else
		{
			new EZZoneHelp(ZoneCommand.INFO, sender, null);
		}
	}
}
