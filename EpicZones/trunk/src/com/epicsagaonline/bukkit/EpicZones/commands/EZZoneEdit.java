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

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone.ZoneType;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;

public class EZZoneEdit 
{
	public EZZoneEdit(String[] data, CommandSender sender)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			boolean admin = EpicZones.permissions.hasPermission(player, "epiczones.admin") || player.isOp();
			if(ezp.getMode() == EpicZoneMode.None)
			{
				if(data.length > 1)
				{
					if(data[1].length() > 0)
					{
						if(General.myZones.get(data[1]) != null)
						{
							String tag = data[1].replaceAll("[^a-zA-Z0-9_]", "");
							EpicZone zone = General.myZones.get(tag);
							if(zone.getType() != ZoneType.GLOBAL)
							{
								if(admin || zone.isOwner(ezp.getName()))
								{
									ezp.setEditZone(new EpicZone(zone));
									ezp.setMode(EpicZoneMode.ZoneEdit);
									ezp.getEditZone().ShowPillars();
									Message.Send(sender, 105, new String[]{tag});
								}
								else
								{
									Message.Send(sender, 106, new String[]{tag});	
								}
							}
							else
							{
								Message.Send(sender, 22);
							}
						}
						else
						{
							new EZZoneCreate(data, sender);
						}
					}
				}
			}
			else
			{
				new EZZoneHelp(ZoneCommand.EDIT, sender, ezp);
			}
		}
	}
}
