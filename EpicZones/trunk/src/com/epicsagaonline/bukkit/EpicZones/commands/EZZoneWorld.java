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
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;

public class EZZoneWorld 
{
	public EZZoneWorld(String[] data, CommandSender sender)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			if(ezp.getMode() == EpicZoneMode.ZoneEdit)
			{
				if(data.length > 1)
				{
					if(ezp.getAdmin()) //Owners are not allowed to change the world of their zone.
					{
						String worldName = data[1].trim();
						if(worldName.length() > 0)
						{
							ezp.getEditZone().setWorld(worldName);
							Message.Send(sender, 100, new String[]{"world", worldName});
						}
					}
				}
			}
			else
			{
				new EZZoneHelp(ZoneCommand.WORLD, sender, ezp);
			}
		}
	}
}
