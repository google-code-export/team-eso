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
import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZoneDAL;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;

public class EZZoneConfirm 
{
	public EZZoneConfirm(String[] data, CommandSender sender)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			boolean admin = EpicZones.permissions.hasPermission(player, "epiczones.admin") || player.isOp();
			if(admin) //Only admin will ever be to this point.
			{
				if(ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
				{

					if(ezp.getEditZone().hasParent())
					{
						General.myZones.get(ezp.getEditZone().getParent().getTag()).removeChild(ezp.getEditZone().getTag());
					}
					EpicZoneDAL.DeleteZone(ezp.getEditZone().getTag());
					sender.sendMessage("Zone [" + ezp.getEditZone().getTag() + "] has been deleted.");

					ezp.setMode(EpicZoneMode.None);
					ezp.setEditZone(null);

				}
				else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
				{
					ezp.setMode(EpicZoneMode.ZoneDraw);
					ezp.getEditZone().clearPolyPoints();
					sender.sendMessage("Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
			}
			else
			{
				new EZZoneHelp(ZoneCommand.CONFIRM, sender, ezp);
			}
		}
	}
}
