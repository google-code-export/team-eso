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

import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;

public class EZZoneHelp 
{

	public enum ZoneCommand {CANCEL, CEILING, CHILD, CONFIRM, CREATE, DELETE, DRAW, EDIT, FLAG, FLOOR, HELP, INFO, LIST, MESSAGE, NAME, NONE, OWNER, PARENT, PERM, RADIUS, SAVE, WORLD}

	public EZZoneHelp(ZoneCommand command, CommandSender sender, EpicZonePlayer ezp)
	{
		if(ezp != null)
		{
			if(ezp.getMode() == EpicZoneMode.ZoneEdit)
			{
				sender.sendMessage(ChatColor.GOLD + "You are currently in Edit mode.");
				sender.sendMessage(ChatColor.GOLD + "/zone name " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Name.");
				sender.sendMessage(ChatColor.GOLD + "/zone flag " + ChatColor.AQUA + "[1] [2] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Flag, " + ChatColor.AQUA + "[2]" + ChatColor.GREEN + " = Value");
				sender.sendMessage(ChatColor.GOLD + "/zone floor " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Floor.");
				sender.sendMessage(ChatColor.GOLD + "/zone ceiling " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Ceiling.");
				sender.sendMessage(ChatColor.GOLD + "/zone addchildren " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Zone Tag, multiples allowed.");
				sender.sendMessage(ChatColor.GOLD + "/zone removechildren " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Zone Tag, multiples allowed.");
				sender.sendMessage(ChatColor.GOLD + "/zone enter " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Enter Message");
				sender.sendMessage(ChatColor.GOLD + "/zone exit " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Exit Message");
				sender.sendMessage(ChatColor.GOLD + "/zone world " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = World Name");
				sender.sendMessage(ChatColor.GOLD + "/zone draw " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Prompts you to go into Draw mode.");
				sender.sendMessage(ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Discards all current changes.");
				sender.sendMessage(ChatColor.GOLD + "/zone delete " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Deletes the zone you are currently editing.");
				sender.sendMessage(ChatColor.GOLD + "/zone save " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Saves all current changes.");
			}
			else if(ezp.getMode() == EpicZoneMode.ZoneDraw)
			{
				sender.sendMessage(ChatColor.GOLD + "You are currently in Draw mode.");
				sender.sendMessage(ChatColor.GOLD + "/zone save " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Saves the point data you have drawn.");
				sender.sendMessage(ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Discards all current changes.");
			}
			else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
			{
				sender.sendMessage(ChatColor.GOLD + "You are currently in Draw Confirm mode.");
				sender.sendMessage(ChatColor.GOLD + "/zone confirm " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Clears point data and puts you into Draw mode.");
				sender.sendMessage(ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Puts you back into EditMode.");
			}
			else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
			{
				sender.sendMessage(ChatColor.GOLD + "You are currently in Delete Confirm mode.");
				sender.sendMessage(ChatColor.GOLD + "/zone confirm " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Deletes the zone you are currently editing.");
				sender.sendMessage(ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Puts you back into EditMode.");
			}
			else
			{
				sender.sendMessage(ChatColor.GOLD + "Help for /zone command.");
				sender.sendMessage(ChatColor.GOLD + "/zone edit " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Begin editing specified zone.");
				sender.sendMessage(ChatColor.GOLD + "/zone create " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Create new zone.");
				sender.sendMessage(ChatColor.GOLD + "/zone list " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Lists existing zones.");
				sender.sendMessage(ChatColor.GOLD + "/zone info " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Detailed info for specified zone.");
			}
		}
		else
		{
			sender.sendMessage(ChatColor.GOLD + "Help for zone command.");
			sender.sendMessage(ChatColor.GOLD + "zone list " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Lists existing zones.");
			sender.sendMessage(ChatColor.GOLD + "zone info " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Detailed info for specified zone.");
		}
	}
}