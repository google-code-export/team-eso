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

package com.epicsagaonline.bukkit.EpicZones.CommandHandlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.Zone;
import com.epicsagaonline.bukkit.EpicZones.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.EpicZonePlayer.EpicZoneMode;

public class ZoneCommandHandler implements CommandHandler {

	public boolean onCommand(String command, CommandSender sender, String[] args) {

		if((sender instanceof Player && EpicZones.permissions.hasPermission((Player)sender, "epiczones.admin")))
		{
			Player player = (Player)sender;
			EpicZonePlayer ezp = General.getPlayer(player.getEntityId());
			int playerID = ezp.getEntityID();

			if(args.length > 0)
			{
				if(args[0].equalsIgnoreCase("create")){Create(args, sender, player, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("save")){Save(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("flag")){Flag(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("radius")){Radius(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("floor")){Floor(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("ceiling")){Ceiling(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("addchildren")){AddChildren(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("removechildren")){RemoveChildren(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("name")){Name(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("enter")){EnterMessage(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("exit")){LeaveMessage(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("draw")){Draw(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("confirm")){Confirm(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("edit")){Edit(args, sender, player, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("world")){World(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("cancel")){Cancel(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("delete")){Delete(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("list")){List(args, sender, ezp, playerID);}
				else if(args[0].equalsIgnoreCase("info")){Info(args, sender, ezp, playerID);}
				else {Help(sender, ezp, playerID);}
			}
			else 
			{
				Help(sender, ezp, playerID);
			}
			return true;
		}
		return false;
	}

	private static void Set(int playerID, String propertyName, Object value)
	{
		if(propertyName.equals("editzone")){General.getPlayer(playerID).setEditZone((Zone)value);}
		else if(propertyName.equals("mode")){General.getPlayer(playerID).setMode((EpicZoneMode)value);}
		else if(propertyName.equals("flag:pvp")){General.getPlayer(playerID).getEditZone().setPVP(Boolean.valueOf(((String)value).trim()));}
		else if(propertyName.equals("flag:mobs")){General.getPlayer(playerID).getEditZone().SetMobs((String)value);}
		else if(propertyName.equals("flag:regen")){General.getPlayer(playerID).getEditZone().setRegen((String)value);}
		else if(propertyName.equals("flag:fire")){System.out.println((String)value); General.getPlayer(playerID).getEditZone().setAllowFire(Boolean.valueOf(((String)value).trim()));}
		else if(propertyName.equals("flag:explode")){General.getPlayer(playerID).getEditZone().setAllowExplode(Boolean.valueOf(((String)value).trim()));}
		else if(propertyName.equals("floor")){General.getPlayer(playerID).getEditZone().setFloor((Integer)value);}
		else if(propertyName.equals("radius")){General.getPlayer(playerID).getEditZone().setRadius((Integer)value);}
		else if(propertyName.equals("ceiling")){General.getPlayer(playerID).getEditZone().setCeiling((Integer)value);}
		else if(propertyName.equals("entermessage")){General.getPlayer(playerID).getEditZone().setEnterText((String)value);}
		else if(propertyName.equals("exitmessage")){General.getPlayer(playerID).getEditZone().setExitText((String)value);}
		else if(propertyName.equals("name")){General.getPlayer(playerID).getEditZone().setName((String)value);}
		else if(propertyName.equals("addchild")){General.getPlayer(playerID).getEditZone().addChild((Zone)value);}
		else if(propertyName.equals("addchildtag")){General.getPlayer(playerID).getEditZone().getChildrenTags().add((String)value);}
		else if(propertyName.equals("removechild")){General.getPlayer(playerID).getEditZone().removeChild((String)value);}
		else if(propertyName.equals("clearpoints")){General.getPlayer(playerID).getEditZone().clearPolyPoints();}
		else if(propertyName.equals("boundingbox")){General.getPlayer(playerID).getEditZone().rebuildBoundingBox();}
		else if(propertyName.equals("world")){General.getPlayer(playerID).getEditZone().setWorld((String)value);}
	}

	private static void Create(String[] data, CommandSender sender, Player player, EpicZonePlayer ezp, int playerID)
	{

		if(ezp.getMode() == EpicZoneMode.None)
		{
			if(data.length > 2 && data[2].length() > 0)
			{
				String tag = data[2].replaceAll("[^a-zA-Z0-9]", "");
				if(General.myZones.get(tag) == null)
				{
					Zone zone = new Zone();
					zone.setTag(tag);
					zone.setName(tag);
					Set(playerID, "editzone", zone);
					Set(playerID, "mode", EpicZoneMode.ZoneDraw);
					Set(playerID, "world", player.getWorld().getName());
					sender.sendMessage("Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
				else
				{
					sender.sendMessage("A zone already exists with the tag [" + tag + "]");
				}
			}
			else
			{
				Help(sender, ezp, playerID);
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Save(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			if(ezp.getEditZone().getPolygon().npoints > 2)
			{
				Set(playerID, "mode", EpicZoneMode.ZoneEdit);
				Set(playerID, "boundingbox", "");
				sender.sendMessage("Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
			}
			else if(ezp.getEditZone().getPolygon().npoints == 1 && ezp.getEditZone().getRadius() > 0)
			{
				Set(playerID, "mode", EpicZoneMode.ZoneEdit);
				Set(playerID, "boundingbox", "");
				sender.sendMessage("Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
			}
			else
			{
				sender.sendMessage("You must draw at least 3 points or 1 point and set a radius, before you can move on.");
			}
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{

			if(General.myZones.get(ezp.getEditZone().getTag()) == null)
			{
				General.myZones.put(ezp.getEditZone().getTag(), ezp.getEditZone());
				General.myZoneTags.add(ezp.getEditZone().getTag());
			}
			else
			{
				General.myZones.remove(ezp.getEditZone().getTag());
				General.myZones.put(ezp.getEditZone().getTag(), ezp.getEditZone());
			}
			General.saveZones();
			Set(playerID, "mode", EpicZoneMode.None);
			sender.sendMessage("Zone Saved.");
		}
		else
		{
			Help(sender, ezp, playerID);
		}

	}

	private static void Flag(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 3 && data[2].length() > 0 && data[3].length() > 0)
			{
				String flag = data[2];
				String value = "";
				for(int i = 3; i < data.length; i++)
				{
					value = value + data[i] + " ";
				}			
				if(ValidFlag(flag))
				{
					Set(playerID, "flag:" + flag.toLowerCase(), value);
					sender.sendMessage("Zone Updated. Flag:" + flag + " set to: " + value);
				}
				else
				{
					sender.sendMessage("The flag [" + flag + "] is not a valid flag.");
					sender.sendMessage("Valid flags are: pvp, mobs, regen, fire, explode");
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Radius(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDraw || ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if (ezp.getEditZone().getPolygon().npoints == 1)
			{
				if(data.length > 2 && General.IsNumeric(data[2]))
				{
					Integer value = Integer.parseInt(data[2]);
					Set(playerID, "radius", value);
					sender.sendMessage("Zone Updated. Radius set to: " + value);
				}
				else
				{
					sender.sendMessage("[" + data[2] + "] is not a valid value for radius.");
				}
			}
			else
			{
				sender.sendMessage("You must specify a single center point, before setting the radius.");
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Floor(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2 && General.IsNumeric(data[2]))
			{
				Integer value = Integer.parseInt(data[2]);
				Set(playerID, "floor", value);
				sender.sendMessage("Zone Updated. Floor to: " + value);
			}
			else
			{
				sender.sendMessage("[" + data[2] + "] is not a valid value for floor.");
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Ceiling(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2 && General.IsNumeric(data[2]))
			{
				Integer value = Integer.parseInt(data[2]);
				Set(playerID, "ceiling", value);
				sender.sendMessage("Zone Updated. Ceiling to: " + value);
			}
			else
			{
				sender.sendMessage("[" + data[2] + "] is not a valid value for ceiling.");
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void AddChildren(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				for(int i = 2; i < data.length; i++)
				{
					String tag = data[i].replaceAll("[^a-zA-Z0-9]", "");
					if(tag.length() > 0 && General.myZones.get(tag) != null)
					{
						Set(playerID, "addchildtag", tag);
						Set(playerID, "addchild", General.myZones.get(tag));
					}
				}
				sender.sendMessage("Zone Children Updated.");
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void RemoveChildren(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				for(int i = 2; i < data.length; i++)
				{
					String tag = data[i].replaceAll("[^a-zA-Z0-9]", "");
					if(tag.length() > 0)
					{
						Set(playerID, "removechild", tag);
					}
				}
				sender.sendMessage("Zone Children Updated.");
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Name(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				String message = "";
				for(int i = 2; i < data.length; i++)
				{
					message = message + data[i] + " ";
				}
				if(message.length() > 0)
				{
					Set(playerID, "name", message.trim());
					sender.sendMessage("Zone Updated. Name set to: " + message);
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void EnterMessage(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				String message = "";
				for(int i = 2; i < data.length; i++)
				{
					message = message + data[i] + " ";
				}
				if(message.length() > 0)
				{
					Set(playerID, "entermessage", message.trim());
					sender.sendMessage("Zone Updated. Enter message set to: " + message);
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void LeaveMessage(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				String message = "";
				for(int i = 2; i < data.length; i++)
				{
					message = message + data[i] + " ";
				}
				if(message.length() > 0)
				{
					Set(playerID, "exitmessage", message.trim());
					sender.sendMessage("Zone Updated. Exit message set to: " + message);
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Draw(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneDrawConfirm);
			sender.sendMessage("WARNING! Entering draw mode will erase all points for the zone! type /zone draw confirm or /zone draw deny.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			if(data.length > 2)
			{
				if(data[2].equalsIgnoreCase("confirm"))
				{
					Set(playerID, "mode", EpicZoneMode.ZoneDraw);
					sender.sendMessage("Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
				else if(data[2].equalsIgnoreCase("deny"))
				{
					Set(playerID, "mode", EpicZoneMode.ZoneEdit);
					sender.sendMessage("Draw Mode canceled, back in Edit Mode. type /zone for more options.");
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void World(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				if(data[2].length() > 0)
				{
					Set(playerID, "world", data[2]);
					sender.sendMessage("Zone Updated. World set to: " + data[2]);
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Confirm(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
		{
			if(ezp.getEditZone().hasParent())
			{
				General.myZones.get(ezp.getEditZone().getParent().getTag()).removeChild(ezp.getEditZone().getTag());
			}
			General.myZoneTags.remove(ezp.getEditZone().getTag());
			General.saveZones();
			General.plugin.setupEpicZones();
			sender.sendMessage("Zone [" + ezp.getEditZone().getTag() + "] has been deleted.");
			Set(playerID, "mode", EpicZoneMode.None);
			Set(playerID, "editzone", null);
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneDraw);
			Set(playerID, "clearpoints", "");
			sender.sendMessage("Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Edit(String[] data, CommandSender sender, Player player, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.None)
		{
			if(data.length > 2)
			{
				if(data[2].length() > 0)
				{
					if(General.myZones.get(data[2]) != null)
					{
						String tag = data[2].replaceAll("[^a-zA-Z0-9]", "");
						Set(playerID, "editzone", new Zone(General.myZones.get(tag)));
						Set(playerID, "mode", EpicZoneMode.ZoneEdit);
						sender.sendMessage("Editing Zone: " + tag);
					}
					else
					{
						Create(data, sender, player, ezp, playerID);
					}
				}
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Cancel(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit || ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			Set(playerID, "mode", EpicZoneMode.None);
			Set(playerID, "editzone", null);
			sender.sendMessage("Zone modification cancelled, no changes were saved.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm || ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneEdit);
			sender.sendMessage("Draw Mode canceled, back in Edit Mode. type /zone for more options.");
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Delete(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneDeleteConfirm);
			sender.sendMessage("To continue deleting the zone [" + ezp.getEditZone().getTag() + "] type /zone confirm.");
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void List(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.None)
		{
			for(String zoneTag: General.myZoneTags)
			{
				String messageText;
				Zone zone = General.myZones.get(zoneTag);
				messageText = zone.getName() + " [" + zone.getTag() + "]";
				if(zone.hasChildren())
				{
					messageText = messageText + " | Children (" + zone.getChildren().size() + ")";
				}
				if(zone.hasParent())
				{
					messageText = messageText + " | Parent Zone: " + zone.getParent().getTag();
				}
				sender.sendMessage(messageText);
			}
		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Info(String[] data, CommandSender sender, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.None)
		{
			if(data.length > 2)
			{
				Zone zone = General.myZones.get(data[2].trim());
				if (zone != null)
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
						for(String childTag: zone.getChildrenTags())
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
					sender.sendMessage(ChatColor.GOLD + "Zone Flags: ");
					messageText = "";
					if(zone.hasPVP())
					{
						messageText = messageText + ChatColor.AQUA + "PVP: " + ChatColor.GREEN + "ON ";
					}
					else
					{
						messageText = messageText + ChatColor.AQUA + "PVP: " + ChatColor.RED + "OFF ";
					}
					if(zone.getAllowFire())
					{
						messageText = messageText + ChatColor.AQUA + "FIRE: " + ChatColor.GREEN + "ON  ";
					}
					else
					{
						messageText = messageText + ChatColor.AQUA + "FIRE: " + ChatColor.RED + "OFF ";
					}
					if(zone.getAllowExplode())
					{
						messageText = messageText + ChatColor.AQUA + "EXPLODE: " + ChatColor.GREEN + "ON  ";
					}
					else
					{
						messageText = messageText + ChatColor.AQUA + "EXPLODE: " + ChatColor.RED + "OFF ";
					}
					if(zone.hasRegen())
					{
						sender.sendMessage(messageText);
						sender.sendMessage(ChatColor.AQUA + "REGEN: " + ChatColor.GREEN + "Delay [" + zone.getRegenDelay() + "] Amount[" + zone.getRegenAmount() + "] Interval[" + zone.getRegenInterval() + "]");
					}
					else
					{
						messageText = messageText + ChatColor.AQUA + "REGEN: " + ChatColor.RED + "OFF ";
						sender.sendMessage(messageText);
					}
					messageText = ChatColor.AQUA + "MOBS:" + ChatColor.GREEN + "";
					for(String mobType: zone.getAllowedMobs())
					{
						messageText = messageText + " " + mobType.replace("org.bukkit.craftbukkit.entity.Craft", "");
					}
					sender.sendMessage(messageText);					
				}
				else
				{
					sender.sendMessage("No zone with the tag [" + data[2] + "] exists.");
				}
			}

		}
		else
		{
			Help(sender, ezp, playerID);
		}
	}

	private static void Help(CommandSender sender, EpicZonePlayer ezp, int playerID)
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

	private static boolean ValidFlag(String flag)
	{
		if(flag.equals("pvp")){return true;}
		else if(flag.equals("mobs")){return true;}
		else if(flag.equals("regen")){return true;}
		else if(flag.equals("fire")){return true;}
		else if(flag.equals("explode")){return true;}
		else {return false;}
	}
}
