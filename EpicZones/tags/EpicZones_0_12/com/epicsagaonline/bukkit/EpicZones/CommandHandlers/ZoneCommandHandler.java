package com.epicsagaonline.bukkit.EpicZones.CommandHandlers;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;

import com.epicsagaonline.bukkit.EpicZones.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.EpicZonePlayer.EpicZoneMode;

public class ZoneCommandHandler {

	private static EpicZones plugin;
	
	public static void Process(String[] data, PlayerChatEvent event, EpicZones instance)
	{

		plugin = instance;
		
		if(EpicZones.permissions.has(event.getPlayer(), "epiczones.admin"))
		{

			EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getEntityId());
			int playerID = ezp.getEntityID();

			if(data.length > 1)
			{
				if(data[1].equalsIgnoreCase("create")){Create(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("save")){Save(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("flag")){Flag(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("radius")){Radius(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("floor")){Floor(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("ceiling")){Ceiling(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("addchildren")){AddChildren(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("removechildren")){RemoveChildren(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("name")){Name(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("enter")){EnterMessage(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("exit")){LeaveMessage(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("draw")){Draw(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("confirm")){Confirm(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("edit")){Edit(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("world")){World(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("cancel")){Cancel(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("delete")){Delete(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("list")){List(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("info")){Info(data, event, ezp, playerID);}
				else {Help(event, ezp, playerID);}
			}
			else 
			{
				Help(event, ezp, playerID);
			}


		}

	}

	private static void Set(int playerID, String propertyName, Object value)
	{
		if(propertyName.equals("editzone")){General.getPlayer(playerID).setEditZone((EpicZone)value);}
		else if(propertyName.equals("mode")){General.getPlayer(playerID).setMode((EpicZoneMode)value);}
		else if(propertyName.equals("flag:pvp")){General.getPlayer(playerID).getEditZone().setPVP(Boolean.valueOf(((String)value).trim()));}
		else if(propertyName.equals("flag:mobs")){General.getPlayer(playerID).getEditZone().SetMobs((String)value);}
		else if(propertyName.equals("flag:regen")){General.getPlayer(playerID).getEditZone().setRegen((String)value);}
		else if(propertyName.equals("flag:fire")){System.out.println((String)value); General.getPlayer(playerID).getEditZone().setAllowFire(Boolean.valueOf(((String)value).trim()));}
		else if(propertyName.equals("flag:explode")){General.getPlayer(playerID).getEditZone().setAllowExplode(Boolean.valueOf(((String)value).trim()));}
		//else if(propertyName.equals("flag:noanimals")){General.getPlayer(playerID).getEditZone().getFlags().put("noanimals", Boolean.valueOf((String)value));}
		else if(propertyName.equals("floor")){General.getPlayer(playerID).getEditZone().setFloor((Integer)value);}
		else if(propertyName.equals("radius")){General.getPlayer(playerID).getEditZone().setRadius((Integer)value);}
		else if(propertyName.equals("ceiling")){General.getPlayer(playerID).getEditZone().setCeiling((Integer)value);}
		else if(propertyName.equals("entermessage")){General.getPlayer(playerID).getEditZone().setEnterText((String)value);}
		else if(propertyName.equals("exitmessage")){General.getPlayer(playerID).getEditZone().setExitText((String)value);}
		else if(propertyName.equals("name")){General.getPlayer(playerID).getEditZone().setName((String)value);}
		else if(propertyName.equals("addchild")){General.getPlayer(playerID).getEditZone().addChild((EpicZone)value);}
		else if(propertyName.equals("addchildtag")){General.getPlayer(playerID).getEditZone().getChildrenTags().add((String)value);}
		else if(propertyName.equals("removechild")){General.getPlayer(playerID).getEditZone().removeChild((String)value);}
		else if(propertyName.equals("clearpoints")){General.getPlayer(playerID).getEditZone().clearPolyPoints();}
		else if(propertyName.equals("boundingbox")){General.getPlayer(playerID).getEditZone().rebuildBoundingBox();}
		else if(propertyName.equals("world")){General.getPlayer(playerID).getEditZone().setWorld((String)value);}
	}

	private static void SendMessage(PlayerChatEvent event, String message)
	{
		event.getPlayer().sendMessage(message);
	}

	private static void Create(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{

		if(ezp.getMode() == EpicZoneMode.None)
		{
			if(data.length > 2 && data[2].length() > 0)
			{
				String tag = data[2].replaceAll("[^a-zA-Z0-9]", "");
				if(General.myZones.get(tag) == null)
				{
					EpicZone zone = new EpicZone();
					zone.setTag(tag);
					zone.setName(tag);
					Set(playerID, "editzone", zone);
					Set(playerID, "mode", EpicZoneMode.ZoneDraw);
					Set(playerID, "world", event.getPlayer().getWorld().getName());
					SendMessage(event, "Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
				else
				{
					SendMessage(event, "A zone already exists with the tag [" + tag + "]");
				}
			}
			else
			{
				Help(event, ezp, playerID);
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Save(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			if(ezp.getEditZone().getPolygon().npoints > 2)
			{
				Set(playerID, "mode", EpicZoneMode.ZoneEdit);
				Set(playerID, "boundingbox", "");
				SendMessage(event, "Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
			}
			else if(ezp.getEditZone().getPolygon().npoints == 1 && ezp.getEditZone().getRadius() > 0)
			{
				Set(playerID, "mode", EpicZoneMode.ZoneEdit);
				Set(playerID, "boundingbox", "");
				SendMessage(event, "Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
			}
			else
			{
				SendMessage(event, "You must draw at least 3 points or 1 point and set a radius, before you can move on.");
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
			SendMessage(event, "Zone Saved.");
		}
		else
		{
			Help(event, ezp, playerID);
		}

	}

	private static void Flag(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
					SendMessage(event, "Zone Updated. Flag:" + flag + " set to: " + value);
				}
				else
				{
					SendMessage(event, "The flag [" + flag + "] is not a valid flag.");
					SendMessage(event, "Valid flags are: pvp, mobs, regen, fire, explode");
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Radius(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDraw || ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if (ezp.getEditZone().getPolygon().npoints == 1)
			{
				if(data.length > 2 && IsNumeric(data[2]))
				{
					Integer value = Integer.parseInt(data[2]);
					Set(playerID, "radius", value);
					SendMessage(event, "Zone Updated. Radius set to: " + value);
				}
				else
				{
					SendMessage(event, "[" + data[2] + "] is not a valid value for radius.");
				}
			}
			else
			{
				SendMessage(event, "You must specify a single center point, before setting the radius.");
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Floor(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2 && IsNumeric(data[2]))
			{
				Integer value = Integer.parseInt(data[2]);
				Set(playerID, "floor", value);
				SendMessage(event, "Zone Updated. Floor to: " + value);
			}
			else
			{
				SendMessage(event, "[" + data[2] + "] is not a valid value for floor.");
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Ceiling(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2 && IsNumeric(data[2]))
			{
				Integer value = Integer.parseInt(data[2]);
				Set(playerID, "ceiling", value);
				SendMessage(event, "Zone Updated. Ceiling to: " + value);
			}
			else
			{
				SendMessage(event, "[" + data[2] + "] is not a valid value for ceiling.");
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void AddChildren(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
				SendMessage(event, "Zone Children Updated.");
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void RemoveChildren(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
				SendMessage(event, "Zone Children Updated.");
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Name(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
					SendMessage(event, "Zone Updated. Name set to: " + message);
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void EnterMessage(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
					SendMessage(event, "Zone Updated. Enter message set to: " + message);
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void LeaveMessage(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
					SendMessage(event, "Zone Updated. Exit message set to: " + message);
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Draw(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneDrawConfirm);
			SendMessage(event, "WARNING! Entering draw mode will erase all points for the zone! type /zone draw confirm or /zone draw deny.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			if(data.length > 2)
			{
				if(data[2].equalsIgnoreCase("confirm"))
				{
					Set(playerID, "mode", EpicZoneMode.ZoneDraw);
					SendMessage(event, "Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
				else if(data[2].equalsIgnoreCase("deny"))
				{
					Set(playerID, "mode", EpicZoneMode.ZoneEdit);
					SendMessage(event, "Draw Mode canceled, back in Edit Mode. type /zone for more options.");
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void World(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				if(data[2].length() > 0)
				{
					Set(playerID, "world", data[2]);
					SendMessage(event, "Zone Updated. World set to: " + data[2]);
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Confirm(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
		{
			if(ezp.getEditZone().hasParent())
			{
				General.myZones.get(ezp.getEditZone().getParent().getTag()).removeChild(ezp.getEditZone().getTag());
			}
			General.myZoneTags.remove(ezp.getEditZone().getTag());
			General.saveZones();
			plugin.setupEpicZones();
			SendMessage(event, "Zone [" + ezp.getEditZone().getTag() + "] has been deleted.");
			Set(playerID, "mode", EpicZoneMode.None);
			Set(playerID, "editzone", null);
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneDraw);
			Set(playerID, "clearpoints", "");
			SendMessage(event, "Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Edit(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
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
						Set(playerID, "editzone", new EpicZone(General.myZones.get(tag)));
						Set(playerID, "mode", EpicZoneMode.ZoneEdit);
						SendMessage(event, "Editing Zone: " + tag);
					}
					else
					{
						Create(data, event, ezp, playerID);
					}
				}
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Cancel(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit || ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			Set(playerID, "mode", EpicZoneMode.None);
			Set(playerID, "editzone", null);
			SendMessage(event, "Zone modification cancelled, no changes were saved.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm || ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneEdit);
			SendMessage(event, "Draw Mode canceled, back in Edit Mode. type /zone for more options.");
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Delete(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneDeleteConfirm);
			SendMessage(event, "To continue deleting the zone [" + ezp.getEditZone().getTag() + "] type /zone confirm.");
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}
	
	private static void List(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.None)
		{
			for(String zoneTag: General.myZoneTags)
			{
				String messageText;
				EpicZone zone = General.myZones.get(zoneTag);
				messageText = zone.getName() + " [" + zone.getTag() + "]";
				if(zone.hasChildren())
				{
					messageText = messageText + " | Children (" + zone.getChildren().size() + ")";
				}
				if(zone.hasParent())
				{
					messageText = messageText + " | Parent Zone: " + zone.getParent().getTag();
				}
				SendMessage(event, messageText);
			}
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}
	
	private static void Info(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.None)
		{
			if(data.length > 2)
			{
				EpicZone zone = General.myZones.get(data[2].trim());
				if (zone != null)
				{
					String messageText;
					
					SendMessage(event, ChatColor.GOLD + "Zone: " + ChatColor.GREEN + zone.getName() + ChatColor.GOLD + " Tag: " + ChatColor.GREEN + "" + zone.getTag());
					if(zone.getCenter() != null)
					{
						SendMessage(event, ChatColor.GOLD + "Shape: " + ChatColor.GREEN + "Circle " + ChatColor.WHITE + "| " + ChatColor.GOLD + "Radius: " + ChatColor.GREEN + "" + zone.getRadius());	
					}
					else
					{
						SendMessage(event, ChatColor.GOLD + "Shape: " + ChatColor.GREEN + "Polygon " + ChatColor.WHITE + "| " + ChatColor.GOLD + "Points " + ChatColor.GREEN + "(" + zone.getPolygon().npoints + ")");
					}
					if(zone.hasChildren())
					{
						messageText = ChatColor.GOLD + "Child Zone Tags:" + ChatColor.GREEN + "";
						for(String childTag: zone.getChildrenTags())
						{
							messageText = messageText + " " + childTag;
						}
						SendMessage(event, messageText);
					}
					SendMessage(event, ChatColor.GOLD + "Enter Text: " + ChatColor.GREEN + "" + zone.getEnterText());
					SendMessage(event, ChatColor.GOLD + "Exit Text: " + ChatColor.GREEN + "" + zone.getExitText());
					if(zone.hasParent())
					{
						SendMessage(event, ChatColor.GOLD + "Parent Zone: " + ChatColor.GREEN + zone.getParent().getName() + ChatColor.GOLD + " Tag: " + ChatColor.GREEN + zone.getParent().getTag());
					}
					SendMessage(event, ChatColor.GOLD + "Zone Flags: ");
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
						SendMessage(event, messageText);
						SendMessage(event, ChatColor.AQUA + "REGEN: " + ChatColor.GREEN + "Delay [" + zone.getRegenDelay() + "] Amount[" + zone.getRegenAmount() + "] Interval[" + zone.getRegenInterval() + "]");
					}
					else
					{
						messageText = messageText + ChatColor.AQUA + "REGEN: " + ChatColor.RED + "OFF ";
						SendMessage(event, messageText);
					}
					messageText = ChatColor.AQUA + "MOBS:" + ChatColor.GREEN + "";
					for(String mobType: zone.getAllowedMobs())
					{
						messageText = messageText + " " + mobType.replace("org.bukkit.craftbukkit.entity.Craft", "");
					}
					SendMessage(event, messageText);					
				}
				else
				{
					SendMessage(event, "No zone with the tag [" + data[2] + "] exists.");
				}
			}
			
		}
		else
		{
			Help(event, ezp, playerID);
		}
	}

	private static void Help(PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			SendMessage(event, ChatColor.GOLD + "You are currently in Edit mode.");
			SendMessage(event, ChatColor.GOLD + "/zone name " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Name.");
			SendMessage(event, ChatColor.GOLD + "/zone flag " + ChatColor.AQUA + "[1] [2] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Flag, " + ChatColor.AQUA + "[2]" + ChatColor.GREEN + " = Value");
			SendMessage(event, ChatColor.GOLD + "/zone floor " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Floor.");
			SendMessage(event, ChatColor.GOLD + "/zone ceiling " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Ceiling.");
			SendMessage(event, ChatColor.GOLD + "/zone addchildren " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Zone Tag, multiples allowed.");
			SendMessage(event, ChatColor.GOLD + "/zone removechildren " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = Zone Tag, multiples allowed.");
			SendMessage(event, ChatColor.GOLD + "/zone enter " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Enter Message");
			SendMessage(event, ChatColor.GOLD + "/zone exit " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = New Exit Message");
			SendMessage(event, ChatColor.GOLD + "/zone world " + ChatColor.AQUA + "[1] " + ChatColor.WHITE + "| " + ChatColor.AQUA + "[1]" + ChatColor.GREEN + " = World Name");
			SendMessage(event, ChatColor.GOLD + "/zone draw " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Prompts you to go into Draw mode.");
			SendMessage(event, ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Discards all current changes.");
			SendMessage(event, ChatColor.GOLD + "/zone delete " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Deletes the zone you are currently editing.");
			SendMessage(event, ChatColor.GOLD + "/zone save " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Saves all current changes.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			SendMessage(event, ChatColor.GOLD + "You are currently in Draw mode.");
			SendMessage(event, ChatColor.GOLD + "/zone save " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Saves the point data you have drawn.");
			SendMessage(event, ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Discards all current changes.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			SendMessage(event, ChatColor.GOLD + "You are currently in Draw Confirm mode.");
			SendMessage(event, ChatColor.GOLD + "/zone confirm " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Clears point data and puts you into Draw mode.");
			SendMessage(event, ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Puts you back into EditMode.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			SendMessage(event, ChatColor.GOLD + "You are currently in Delete Confirm mode.");
			SendMessage(event, ChatColor.GOLD + "/zone confirm " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Deletes the zone you are currently editing.");
			SendMessage(event, ChatColor.GOLD + "/zone cancel " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Puts you back into EditMode.");
		}
		else
		{
			SendMessage(event, ChatColor.GOLD + "Help for /zone command.");
			SendMessage(event, ChatColor.GOLD + "/zone edit " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Begin editing specified zone.");
			SendMessage(event, ChatColor.GOLD + "/zone create " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Create new zone.");
			SendMessage(event, ChatColor.GOLD + "/zone list " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Lists existing zones.");
			SendMessage(event, ChatColor.GOLD + "/zone info " + ChatColor.AQUA + "[tag] " + ChatColor.WHITE + "| " + ChatColor.GREEN + "Detailed info for specified zone.");
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

	private static boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
	}

}
