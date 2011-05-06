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

public class EZZonePerm 
{
	public EZZonePerm(String[] data, CommandSender sender)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			
			if(ezp.getMode() == EpicZoneMode.ZoneEdit)
			{
				if(data.length > 3)
				{

					String member = data[1];
					String node = data[2];
					String perm = data[3];

					if(ValidNode(node))
					{
						if(ValidPerm(perm))
						{
							ezp.getEditZone().addPermission(member, node, perm);
							Message.Send(sender, 109, new String[]{member, node, perm});
						}
						else
						{
							Message.Send(sender, 110, new String[]{perm});	
						}
					}
					else
					{
						Message.Send(sender, 111, new String[]{node});
					}
				}
				else
				{
					new EZZoneHelp(ZoneCommand.PERM, sender, ezp);
				}
			}
			else
			{
				new EZZoneHelp(ZoneCommand.PERM, sender, ezp);
			}
		}
	}
	
	private static boolean ValidNode(String node)
	{
		if(node.equals("build")){return true;}
		else if(node.equals("destroy")){return true;}
		else if(node.equals("entry")){return true;}
		else {return false;}
	}

	private static boolean ValidPerm(String perm)
	{
		if(perm.equals("allow")){return true;}
		else if(perm.equals("deny")){return true;}
		else {return false;}
	}
}
