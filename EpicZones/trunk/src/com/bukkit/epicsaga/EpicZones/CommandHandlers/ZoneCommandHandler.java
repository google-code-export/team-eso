package com.bukkit.epicsaga.EpicZones.CommandHandlers;

import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.epicsaga.EpicZones.EpicZone;
import com.bukkit.epicsaga.EpicZones.EpicZonePlayer;
import com.bukkit.epicsaga.EpicZones.EpicZones;
import com.bukkit.epicsaga.EpicZones.General;
import com.bukkit.epicsaga.EpicZones.EpicZonePlayer.EpicZoneMode;

public class ZoneCommandHandler {

	public static void Process(String[] data, PlayerChatEvent event)
	{

		if(EpicZones.permissions.has(event.getPlayer(), "epiczones.admin"))
		{

			EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getEntityId());
			int playerID = ezp.getEntityID();

			if(data.length > 1)
			{
				if(data[1].equalsIgnoreCase("create")){Create(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("save")){Save(data, event, ezp, playerID);}
				else if(data[1].equalsIgnoreCase("flag")){Flag(data, event, ezp, playerID);}
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
		else if(propertyName.equals("flag:pvp")){General.getPlayer(playerID).getEditZone().getFlags().put("pvp", (Boolean)value);}
		else if(propertyName.equals("flag:nomobs")){General.getPlayer(playerID).getEditZone().getFlags().put("nomobs", (Boolean)value);}
		else if(propertyName.equals("flag:regen")){General.getPlayer(playerID).getEditZone().getFlags().put("regen", (Boolean)value);}
		else if(propertyName.equals("flag:noanimals")){General.getPlayer(playerID).getEditZone().getFlags().put("noanimals", (Boolean)value);}
		else if(propertyName.equals("floor")){General.getPlayer(playerID).getEditZone().setFloor((Integer)value);}
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
			else
			{
				SendMessage(event, "You must draw at least 3 points before you can move on.");
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
			General.SaveZones();
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
				String value = data[3];
				if(ValidFlag(flag))
				{
					Set(playerID, "flag:" + flag.toLowerCase(), value);
					SendMessage(event, "Zone Updated. Flag:" + flag + " set to: " + value);
				}
				else
				{
					SendMessage(event, "The flag [" + flag + "] is not a valid flag.");
					SendMessage(event, "Valid flags are: pvp");
				}
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
			General.myZoneTags.remove(ezp.getEditZone().getTag());
			General.SaveZones();
			General.loadZones(null);
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
						Set(playerID, "editzone", General.myZones.get(tag));
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

	private static void Help(PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			SendMessage(event, "You are currently in Edit mode. The following commands are available.");
			SendMessage(event, "/zone name [value] - Sets the name of the zone you are currently editing.");
			SendMessage(event, "/zone flag [pvp] [true|false] - Sets the indicated flag true or false.");
			SendMessage(event, "/zone floor [value] Sets the floor of the zone you are currently editing.");
			SendMessage(event, "/zone ceiling [value] Sets the ceiling of the zone you are currently editing.");
			SendMessage(event, "/zone addchildren|removechildren [value] [value]... - Adds or removes children from the zone you are currently editing.");
			SendMessage(event, "/zone enter|exit [value] Sets the enter or exit message of the zone you are currently editing.");
			SendMessage(event, "/zone world [value] world of the zone you are currently editing.");
			SendMessage(event, "/zone draw - Prompts you to go back into Draw mode.");
			SendMessage(event, "/zone cancel - Discards all changes for the current zone you are editing.");
			SendMessage(event, "/zone delete - Deletes the zone you are currently editing.");
			SendMessage(event, "/zone save - Saves all changes for the current zone you are editing, and dumps you out of edit mode.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			SendMessage(event, "You are currently in Draw mode. The following commands are available.");
			SendMessage(event, "/zone save - Saves the point data you have drawn and puts you into Edit mode.");
			SendMessage(event, "/zone cancel - Discards all changes for the current zone you are editing and dumps you out of Draw and Edit mode.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			SendMessage(event, "You are currently in Draw Confirm mode. The following commands are available.");
			SendMessage(event, "/zone confirm - Clears point data for the current zone and puts you into Draw mode.");
			SendMessage(event, "/zone cancel - Puts you back into EditMode.");
		}
		else if(ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
		{
			SendMessage(event, "You are currently in Delete Confirm mode. The following commands are available.");
			SendMessage(event, "/zone confirm - Deletes the zone you are currently editing.");
			SendMessage(event, "/zone cancel - Puts you back into EditMode.");
		}
		else
		{
			SendMessage(event, "To use the /zone command, type one of the following commands.");
			SendMessage(event, "/zone edit [tag] - Edits an existing zone and puts you into Edit mode.");
			SendMessage(event, "/zone create [tag] - Creates a new zone and puts you into Draw mode.");
		}
	}

	private static boolean ValidFlag(String flag)
	{
		if(flag.equals("pvp")){return true;}
		//else if(flag.equals("nomobs")){return true;}
		//else if(flag.equals("regen")){return true;}
		//else if(flag.equals("noanimals")){return true;}
		else {return false;}
	}

	private static boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
	}

}
