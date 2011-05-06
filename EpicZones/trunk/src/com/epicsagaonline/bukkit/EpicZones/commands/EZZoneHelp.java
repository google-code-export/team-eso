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

import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;

public class EZZoneHelp 
{

	public enum ZoneCommand {CANCEL, CEILING, CHILD, CONFIRM, CREATE, DELETE, DRAW, EDIT, FLAG, FLOOR, HELP, INFO, LIST, MESSAGE, NAME, NONE, OWNER, PARENT, PERM, RADIUS, SAVE, WORLD}

	public EZZoneHelp(ZoneCommand command, CommandSender sender, EpicZonePlayer ezp)
	{
		if(ezp != null)
		{
			Message.Send(sender, 1000);
			Message.Send(sender, 10);
			if(ezp.getMode() == EpicZoneMode.ZoneEdit)
			{
				Message.Send(sender, 11);
				Message.Send(sender, 1001);
				Message.Send(sender, 1002);
				Message.Send(sender, 1003);
				Message.Send(sender, 1004);
				Message.Send(sender, 1005);
				Message.Send(sender, 1006);
				Message.Send(sender, 1007);
				Message.Send(sender, 1008);
				Message.Send(sender, 1009);
				Message.Send(sender, 1010);
				Message.Send(sender, 1011);
				Message.Send(sender, 1012);
				Message.Send(sender, 1013);
				Message.Send(sender, 1014);
				Message.Send(sender, 1015);
				Message.Send(sender, 1016);
				Message.Send(sender, 1017);
				Message.Send(sender, 1018);
				Message.Send(sender, 1019);
			}
			else if(ezp.getMode() == EpicZoneMode.ZoneDraw)
			{
				Message.Send(sender, 12);
				Message.Send(sender, 1020);
				Message.Send(sender, 1022);
			}
			else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
			{
				Message.Send(sender, 13);
				Message.Send(sender, 1021);
				Message.Send(sender, 1022);
			}
			else if(ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
			{
				Message.Send(sender, 14);
				Message.Send(sender, 1021);
				Message.Send(sender, 1022);
			}
			else
			{
				Message.Send(sender, 1024);
				Message.Send(sender, 1025);
				Message.Send(sender, 1026);
				Message.Send(sender, 1027);
			}
		}
		else
		{
			Message.Send(sender, 1026);
			Message.Send(sender, 1027);
		}
	}
}