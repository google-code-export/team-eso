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

			if(data[1].equalsIgnoreCase("create")){Create(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("save")){Save(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("flag")){Flag(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("floor")){Floor(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("ceiling")){Ceiling(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("addchildren")){AddChildren(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("removechildren")){RemoveChildren(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("name")){Name(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("entermessage")){EnterMessage(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("exitmessage")){LeaveMessage(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("draw")){Draw(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("edit")){Edit(data, event, ezp, playerID);}
			else if(data[1].equalsIgnoreCase("cancel")){Cancel(data, event, ezp, playerID);}
			else {Help(event, ezp, playerID);}

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
					Set(playerID, "editzone", zone);
					Set(playerID, "mode", EpicZoneMode.ZoneDraw);
					SendMessage(event, "Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
				else
				{
					SendMessage(event, "Tag Already Exists");
				}
			}
			else
			{
				SendMessage(event, "No Tag Specified.");
			}
		}
		else
		{
			SendMessage(event, "Player Already Editing a Zone.");
		}
	}

	private static void Save(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneDraw)
		{
			Set(playerID, "mode", EpicZoneMode.ZoneEdit);
			SendMessage(event, "Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
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
				General.myZones.put(ezp.getEditZone().getTag(), ezp.getEditZone());
			}
			General.SaveZones();
			Set(playerID, "mode", EpicZoneMode.None);
			SendMessage(event, "Zone Saved.");
		}
		else
		{
			SendMessage(event, "Player Not In ZoneEdit or ZoneDraw mode");
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
					SendMessage(event, "Invalid Flag");
				}
			}
		}
		else
		{
			SendMessage(event, "Player not in ZoneEdit mode");
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
				SendMessage(event, "Invalid Number Input");
			}
		}
		else
		{
			SendMessage(event, "Player not in ZoneEdit mode");
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
				SendMessage(event, "Invalid Number Input");
			}
		}
		else
		{
			SendMessage(event, "Player not in ZoneEdit mode");
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
			SendMessage(event, "Player not in ZoneEdit mode");
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
			SendMessage(event, "Player not in ZoneEdit mode");
		}
	}

	private static void Name(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			if(data.length > 2)
			{
				if(data[2].length() > 0)
				{
					Set(playerID, "name", data[2]);
					SendMessage(event, "Zone Updated. Name set to: " + data[2]);
				}
			}
		}
		else
		{
			SendMessage(event, "Player not in ZoneEdit mode");
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
			SendMessage(event, "Player not in ZoneEdit mode");
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
			SendMessage(event, "Player not in ZoneEdit mode");
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
			SendMessage(event, "Player not in ZoneEdit mode");
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
			SendMessage(event, "Player not in Null mode");
		}
	}

	private static void Cancel(String[] data, PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{
			Set(playerID, "mode", EpicZoneMode.None);
			Set(playerID, "editzone", "");
			SendMessage(event, "Zone modification cancelled, no changes were saved.");
		}
		else
		{
			SendMessage(event, "Player not in ZoneEdit mode");
		}
	}

	private static void Help(PlayerChatEvent event, EpicZonePlayer ezp, int playerID)
	{
		if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{

		}
		else if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{

		}
		else if(ezp.getMode() == EpicZoneMode.ZoneEdit)
		{

		}
		else
		{

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
