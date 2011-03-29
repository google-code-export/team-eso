/*

        This file is part of EpicGates

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

package com.epicsagaonline.bukkit.EpicGates.commands;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicGates.EpicGates;
import com.epicsagaonline.bukkit.EpicGates.General;
import com.epicsagaonline.bukkit.EpicGates.objects.EpicGatesWorld;

public class EGWorld implements CommandHandler {

	@Override
	public boolean onCommand(String command, CommandSender sender, String[] args)
	{
		if((sender instanceof Player && EpicGates.permissions.hasPermission((Player)sender, "epicgates.admin")))
		{
			Player player = (Player)sender;

			if(args.length > 0)
			{
				if(args[0].equalsIgnoreCase("create")){Create(args, sender, player);}
				else if(args[0].equalsIgnoreCase("delete")){Delete(args, sender, player);}
				else if(args[0].equalsIgnoreCase("visit")){Visit(args, sender, player);}
				else if(args[0].equalsIgnoreCase("list")){List(args, sender, player);}
				else {Help(sender);}
			}
			else 
			{
				Help(sender);
			}
			return true;
		}
		return false;
	}

	private void Create(String[] args, CommandSender sender, Player player)
	{				
		if(args.length > 1)
		{
			EpicGatesWorld egw = new EpicGatesWorld(args[1]);
			if(args.length > 2 && args[2].equalsIgnoreCase("nether"))
			{
				General.plugin.getServer().createWorld(args[1], Environment.NETHER);
				egw.environment = Environment.NETHER;
			}
			else
			{
				General.plugin.getServer().createWorld(args[1], Environment.NORMAL);
				egw.environment = Environment.NORMAL;
			}
			sender.sendMessage("New World Created: " + args[1]);
			General.config.additionalWorlds.add(egw);
			General.config.save();
		}
		else
		{
			Help(sender);
		}
	}

	private void Visit(String[] args, CommandSender sender, Player player)
	{				
		if(args.length > 1)
		{
			World world = General.plugin.getServer().getWorld(args[1]);
			if(world != null)
			{
				Location newLoc = player.getLocation().clone();
				newLoc.setWorld(world);
				player.teleport(newLoc);
			}
		}
		else
		{
			Help(sender);
		}
	}

	private void List(String[] args, CommandSender sender, Player player)
	{		
		for(EpicGatesWorld egw : General.config.additionalWorlds)
		{			
			sender.sendMessage(ChatColor.GREEN + "World " + ChatColor.GOLD + "[" + egw.name + "]" + ChatColor.GREEN + " Environment " + ChatColor.GOLD + "[" + egw.environment.toString() + "]" + ChatColor.GREEN + ".");
		}
	}

	private void Delete(String[] args, CommandSender sender, Player player)
	{				
		if(args.length > 1)
		{

			int index = 0;
			int delIndex = -1;

			for(EpicGatesWorld egw: General.config.additionalWorlds)
			{
				if(egw.name.equalsIgnoreCase(args[1]))
				{
					delIndex = index;
					break;
				}
				index ++;
			}

			if(delIndex > -1)
			{
				General.config.additionalWorlds.remove(delIndex);
			}

			General.config.save();
			sender.sendMessage(args[1] + " will no longer load on server start. World files have NOT been deleted.");
			sender.sendMessage("You must restart your server for [" + args[1] + "] to unload.");
		}
		else
		{
			Help(sender);
		}
	}

	private void Help(CommandSender sender)
	{
		sender.sendMessage(ChatColor.GOLD + "EpicGates world help");
		sender.sendMessage(ChatColor.GOLD + "/world create " + ChatColor.AQUA + "[1] [2]" + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New World Name, " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Environment Type.");
		sender.sendMessage(ChatColor.GOLD + "/world delete " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = World Name.");
		sender.sendMessage(ChatColor.GOLD + "/world visit " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = World Name.");
		sender.sendMessage(ChatColor.GOLD + "/world list " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Lists all worlds.");
	}

}
