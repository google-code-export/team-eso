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
import com.epicsagaonline.bukkit.EpicZones.Message.Message_ID;
import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;

public class EZZoneSave
{
	public EZZoneSave(String[] data, CommandSender sender)
	{
		EpicZonePlayer ezp;
		if (sender instanceof Player)
		{
			ezp = General.getPlayer(((Player) sender).getName());
		}
		else
		{
			ezp = General.getPlayer("console");
		}
		if (ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			if (ezp.getEditZone().getPolygon().npoints > 2)
			{
				ezp.setMode(EpicZoneMode.ZoneEdit);
				ezp.getEditZone().rebuildBoundingBox();
				Message.Send(sender, Message_ID.Info_00029_DrawingComplete);
			}
			else if (ezp.getEditZone().getPolygon().npoints == 1 && ezp.getEditZone().getRadius() > 0)
			{
				ezp.setMode(EpicZoneMode.ZoneEdit);
				ezp.getEditZone().rebuildBoundingBox();
				Message.Send(sender, Message_ID.Info_00029_DrawingComplete);
			}
			else
			{
				Message.Send(sender, Message_ID.Warning_00030_Draw_Need3Points);
			}
		}
		else if (ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if (!ezp.getEditZone().hasParent())
			{ // If a zone does not have a parent, set it's parent to the global
				// zone the zone is within.
				ezp.getEditZone().setParent(General.myGlobalZones.get(ezp.getEditZone().getWorld().toLowerCase()));
			}
			if (General.myZones.get(ezp.getEditZone().getTag()) == null)
			{
				General.myZones.put(ezp.getEditZone().getTag(), ezp.getEditZone());
			}
			else
			{
				General.myZones.remove(ezp.getEditZone().getTag());
				General.myZones.put(ezp.getEditZone().getTag(), ezp.getEditZone());
			}
			ezp.getEditZone().HidePillars();
			General.SaveZones();
			ezp.setMode(EpicZoneMode.None);
			Message.Send(sender, Message_ID.Info_00031_ZoneSaved);
		}
		else
		{
			new EZZoneHelp(ZoneCommand.SAVE, sender, ezp);
		}
	}
}
