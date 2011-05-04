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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
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
			boolean admin = EpicZones.permissions.hasPermission(player, "epiczones.admin") || player.isOp();
			if(data.length > 1)
			{
				EpicZone zone = General.myZones.get(data[1].trim());
				if (zone != null)
				{
					if(admin || zone.isOwner(ezp.getName()))
					{
						String messageText;

						sender.sendMessage(ChatColor.GOLD + "Zone: " + ChatColor.GREEN + zone.getName() + ChatColor.GOLD + " Tag: " + ChatColor.GREEN + "" + zone.getTag());
						if(zone.getCenter() != null)
						{
							sender.sendMessage(ChatColor.GOLD + "Shape: " + ChatColor.GREEN + "Circle " + ChatColor.WHITE + "| " + ChatColor.GOLD + "Radius: " + ChatColor.GREEN + "" + zone.getRadius());	
						}
						else
						{
							sender.sendMessage(ChatColor.GOLD + "Shape: " + ChatColor.GREEN + "Polygon " + ChatColor.WHITE + "| " + ChatColor.GOLD + "Points " + ChatColor.GREEN + "(" + zone.getPolygon().npoints + ")");
						}
						if(zone.hasChildren())
						{
							messageText = ChatColor.GOLD + "Child Zone Tags:" + ChatColor.GREEN + "";
							for(String childTag: zone.getChildren().keySet())
							{
								messageText = messageText + " " + childTag;
							}
							sender.sendMessage(messageText);
						}
						sender.sendMessage(ChatColor.GOLD + "Enter Text: " + ChatColor.GREEN + "" + zone.getEnterText());
						sender.sendMessage(ChatColor.GOLD + "Exit Text: " + ChatColor.GREEN + "" + zone.getExitText());
						if(zone.hasParent())
						{
							sender.sendMessage(ChatColor.GOLD + "Parent Zone: " + ChatColor.GREEN + zone.getParent().getName() + ChatColor.GOLD + " Tag: " + ChatColor.GREEN + zone.getParent().getTag());
						}
						if(zone.getOwners().size() > 0)
						{
							messageText = ChatColor.GOLD + "Owners:" + ChatColor.GREEN + "";
							messageText = messageText + " " + zone.getOwners().toString();
							sender.sendMessage(messageText);
						}
						sender.sendMessage(ChatColor.GOLD + "Zone Flags: ");
						messageText = "";
						if(zone.getPVP())
						{
							messageText = messageText + ChatColor.AQUA + "PVP: " + ChatColor.GREEN + "ON ";
						}
						else
						{
							messageText = messageText + ChatColor.AQUA + "PVP: " + ChatColor.RED + "OFF ";
						}
						if(zone.getFire())
						{
							messageText = messageText + ChatColor.AQUA + "FIRE: " + ChatColor.GREEN + "ON  ";
						}
						else
						{
							messageText = messageText + ChatColor.AQUA + "FIRE: " + ChatColor.RED + "OFF ";
						}
						if(zone.getExplode())
						{
							messageText = messageText + ChatColor.AQUA + "EXPLODE: " + ChatColor.GREEN + "ON  ";
						}
						else
						{
							messageText = messageText + ChatColor.AQUA + "EXPLODE: " + ChatColor.RED + "OFF ";
						}
						if(zone.getSanctuary())
						{
							messageText = messageText + ChatColor.AQUA + "SANCTUARY: " + ChatColor.GREEN + "ON  ";
						}
						else
						{
							messageText = messageText + ChatColor.AQUA + "SANCTUARY: " + ChatColor.RED + "OFF ";
						}
						sender.sendMessage(messageText);
						messageText = "";
						if(zone.getFireBurnsMobs())
						{
							messageText = messageText + ChatColor.AQUA + "FIREBURNSMOBS: " + ChatColor.GREEN + "ON  ";
						}
						else
						{
							messageText = messageText + ChatColor.AQUA + "FIREBURNSMOBS: " + ChatColor.RED + "OFF ";
						}
						if(zone.hasRegen())
						{
							messageText = messageText + ChatColor.AQUA + "REGEN: " + ChatColor.GREEN + "Delay [" + zone.getRegen().getDelay() + "] Amount[" + zone.getRegen().getAmount() + "] Interval[" + zone.getRegen().getInterval() + "]";
						}
						else
						{
							messageText = messageText + ChatColor.AQUA + "REGEN: " + ChatColor.RED + "OFF ";
						}
						sender.sendMessage(messageText);
						messageText = ChatColor.AQUA + "MOBS:" + ChatColor.GREEN + "";
						for(String mobType: zone.getMobs())
						{
							messageText = messageText + " " + mobType.replace("org.bukkit.craftbukkit.entity.Craft", "");
						}
						sender.sendMessage(messageText);
						sender.sendMessage("Permissions:");
						for(EpicZonePermission perm : zone.getPermissions())
						{
							sender.sendMessage(perm.getMember() + " > " + perm.getNode().toString() + ":" + perm.getPermission().toString());
						}
					}
					else
					{
						sender.sendMessage("You do not have permission to use this command.");
					}
				}
				else
				{
					sender.sendMessage("No zone with the tag [" + data[1] + "] exists.");
				}
			}
		}
		else
		{
			new EZZoneHelp(ZoneCommand.INFO, sender, null);
		}
	}
}
