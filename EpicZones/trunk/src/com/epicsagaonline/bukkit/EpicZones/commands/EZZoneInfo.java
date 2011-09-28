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
						Message.Send(sender, Message_ID.Format_KeyValue, new String[]{zone.getName(), zone.getTag()});
						if(zone.getCenter() != null)
						{
							Message.Send(sender, Message_ID.Info_00121_Zone_Shape_Cirdle, new String[]{zone.getRadius() + ""});
						}
						else
						{
							Message.Send(sender, Message_ID.Info_00122_Zone_Shape_Poly, new String[]{zone.getPolygon().npoints + ""});
						}
						if(zone.hasChildren())
						{
							messageText = "";
							for(String childTag: zone.getChildren().keySet())
							{
								messageText = messageText + " " + childTag;
							}
							Message.Send(sender, Message_ID.Info_00123_Zone_Children, new String[]{messageText});
						}
						Message.Send(sender, Message_ID.Info_00124_Zone_EnterText, new String[]{zone.getEnterText()});
						Message.Send(sender, Message_ID.Info_00125_Zone_ExitText, new String[]{zone.getExitText()});
						if(zone.hasParent())
						{
							Message.Send(sender, Message_ID.Info_00126_Zone_Parent, new String[]{zone.getParent().getName(), zone.getParent().getTag()});
						}
						if(zone.getOwners().size() > 0)
						{
							Message.Send(sender, Message_ID.Info_00127_Zone_Owners, new String[]{zone.getOwners().toString()});
						}
						Message.Send(sender, Message_ID.Info_00038_ZoneFlags);
						messageText = "";
						if(zone.getPVP())
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_On, new String[]{"PVP"}) + " ";
						}
						else
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_Off, new String[]{"PVP"}) + " ";
						}
						if(zone.getFire().getIgnite() || zone.getFire().getSpread())
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_On, new String[]{"FIRE"}) + " (";
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
							messageText = messageText + Message.get(Message_ID.Format_Flag_Off, new String[]{"FIRE"}) + " ";
						}

						if(zone.getExplode().getTNT() || zone.getExplode().getCreeper() || zone.getExplode().getGhast())
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_On, new String[]{"EXPLODE"}) + " (";
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
							messageText = messageText + Message.get(Message_ID.Format_Flag_Off, new String[]{"EXPLODE"}) + " ";
						}

						if(zone.getSanctuary())
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_On, new String[]{"SANCTUARY"}) + " ";
						}
						else
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_Off, new String[]{"SANCTUARY"}) + " ";
						}
						Message.Send(sender, messageText);
						messageText = "";
						if(zone.getFireBurnsMobs())
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_On, new String[]{"FIREBURNSMOBS"}) + " ";
						}
						else
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_Off, new String[]{"FIREBURNSMOBS"}) + " ";
						}
						if(zone.hasRegen())
						{
							messageText = messageText + Message.get(Message_ID.Info_00118_Zone_Regen, new String[]{zone.getRegen().getDelay() + "", zone.getRegen().getAmount() + "", zone.getRegen().getInterval() + ""});
						}
						else
						{
							messageText = messageText + Message.get(Message_ID.Format_Flag_Off, new String[]{"REGEN"});
						}
						Message.Send(sender, messageText);
						messageText = "";
						for(String mobType: zone.getMobs())
						{
							messageText = messageText + " " + mobType;
						}
						Message.Send(sender, Message_ID.Info_00119_Zone_Mobs, new String[]{messageText});
						Message.Send(sender, Message_ID.Info_00039_Permissions);
						for(String permKey : zone.getPermissions().keySet())
						{
							EpicZonePermission perm = zone.getPermissions().get(permKey);
							Message.Send(sender, Message_ID.Info_00120_Zone_PermissionTemplate, new String[]{perm.getMember(), perm.getNode().toString(), perm.getPermission().toString()});
						}
					}
					else
					{
						Message.Send(sender, Message_ID.Warning_00037_Perm_Command);
					}
				}
				else
				{
					Message.Send(sender, Message_ID.Warning_00117_Zone_X_DoesNotExist, new String[]{data[1]});
				}
			}
		}
		else
		{
			new EZZoneHelp(ZoneCommand.INFO, sender, null);
		}
	}
}
