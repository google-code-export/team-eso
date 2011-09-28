package com.epicsagaonline.bukkit.EpicZones;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Message {
	
	public enum Message_ID
	{
		//result += "00000: ###,###,###\n"),
		Format_Numeric (0),
		Format_Flag_On (2),
		Format_Flag_Off (3),
		Format_KeyValue (5),
		//1 - 5 do not use
		//result += "00001: &6(0):&A (1)\n"),
		//result += "00002: &B(0): &AON\n"),
		//result += "00003: &B(0): &COFF\n"),
		//result += "00004: &6(0)\n"),
		//result += "00005: &A(0)&6 (1)\n"),
		//result += "\n"),
		//end of do not use
		
		
		//result += "00010: &B[] &6Required Parameter &B<> &6Optional Parameter @@&B... &6 Indicates repeatable. &B| &6 Indicates either/or\n"),
		Info_00010_Parameter_Description (10),
		//result += "00011: &6You are currently in Edit mode.\n"),
		Mode_00011_Edit (11),
		//result += "00012: &6You are currently in Draw mode.\n"),
		Mode_00012_Draw (12),
		//result += "00013: &6You are currently in Draw Confirm mode.\n"),
		Mode_00013_DrawConfirm (13),
		//result += "00014: &6You are currently in Delete Confirm mode.\n"),
		Mode_00014_DeleteConfirm (14),
		//result += "00015: EpicZones Reloaded.\n"),
		Info_00015_Reloaded (15),
		//result += "00016: Zone modification cancelled, no changes were saved.\n"),
		Info_00016_Cancel_Edit (16),
		//result += "00017: Draw Mode canceled, back in Edit Mode. type /zone for more options.\n"),
		Info_00017_Cancel_Draw (17),
		//result += "00018: Zone children updated.\n"),
		Info_00018_Updated_Children (18),
		//result += "00019: Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.\n"),
		Mode_00019_Draw_StartMessage (19),
		//result += "00020: Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.\n"),
		Mode_00020_Draw_StartAfterNew (20),
		//result += "00021: WARNING! Entering draw mode will erase all points for the zone! Type /zone draw confirm or /zone draw deny.\n"),
		Mode_00021_DrawConfirm_Warning (21),
		//result += "00022: Global zones cannot be edited.\n"),
		Warning_00022_CannotDeleteGlobalZones (22),
		//result += "00023: No zones to list.\n"),
		Warning_00023_NoZones (23),
		//result += "00024: You don't own any zones.\n"),
		Warning_00024_NoZones_Owner (24),
		//result += "00025: You did not specify if you wanted to edit the 'enter' or 'exit' text.\n"),
		Warning_00025_NoEnter_NoExit (25),
		//result += "00026: Zone Owners Updated.\n"),
		Info_00026_OwnersUpdated (26),
		//result += "00027: Zone Parents Updated.\n"),
		Info_00027_ParentsUpdated (27),
		//result += "00028: You must specify a single center point, before setting the radius.\n"),
		Warning_00028_Draw_NoCenterOfCircle (28),
		//result += "00029: Drawing Complete.\n"),
		Info_00029_DrawingComplete (29),
		//result += "00030: You must draw at least 3 points or 1 point and set a radius, before you can move on.\n"),
		Warning_00030_Draw_Need3Points (30),
		//result += "00031: Zone Saved.\n"),
		Info_00031_ZoneSaved (31),
		//result += "00032: You do not have permission to destroy in this zone.),\n"),
		Warning_00032_Perm_DestroyInZone (32),
		//result += "00033: You do not have permission to destroy outside the border of the map.),\n"),
		Warning_00033_Perm_DestroyOutsideRadius (33),
		//result += "00034: You do not have permission to build in this zone.),\n"),
		Warning_00034_Perm_BuildInZone (34),
		//result += "00035: You do not have permission to build outside the border of the map.),\n"),
		Warning_00035_Perm_BuildOutsideRadius (35),
		//result += "00036: You do not have permission to do that in this zone.\n"),
		Warning_00036_Perm_GenericInZone (36),
		//result += "00037: You do not have permission to use this command.\n"),
		Warning_00037_Perm_Command (37),
		//result += "00038: &6Zone Flags:\n"),
		Info_00038_ZoneFlags (38),
		//result += "00039: &6Permissions:\n"),
		Info_00039_Permissions (39),
		//result += "00040: Permissions cleared.\n"),
		Info_00040_PermissionsCleared (40),
		
		
		//result += "00100: Zone Updated. Set (0) to [(1)]\n"),
		Info_00100_ZoneUpdatedSet_X_to_Y (100),
		//result += "00101: (0) is not a numerical value.\n"),
		Warning_00101_X_IsNotNumeric (101),
		//result += "00102: Zone [(0)] has been deleted.\n"),
		Info_00102_Zone_X_Deleted (102),
		//result += "00103: A zone already exists with the tag [(0)]\n"),
		Warning_00103_Zone_X_Exists (103),
		//result += "00104: To continue deleting the zone [(0)] type /zone confirm.\n"),
		Info_00104_Mode_ZoneDelete (104),
		//result += "00105: You are now editing zone [(0)]\n"),
		Info_00105_Mode_Edit (105),
		//result += "00106: You do not have permission to edit the zone [(0)].\n"),
		Warning_00106_Perm_EditZone (106),
		//result += "00107: Zone Updated. Set flag [(0)] to [(1)].\n"),
		Info_00107_ZoneFlagUpdated_X_to_Y (107),
		//result += "00108: [(0)] is not a valid flag.@@Valid flags are: pvp, mobs, regen, fire, explode, sanctuary\n"),
		Warning_00108_InvalidFlag (108),
		//result += "00109: Permission Added: [(0)] > [(1)] : [(2)]\n"),
		Info_00109_PermissionAdded (109),
		//result += "00110: [(0)] is not a valid permission type.\n"),
		Warning_00110_InvalidPermissionType (110),
		//result += "00111: [(0)] is not a valid permission node.\n"),
		Warning_00111_InvalidPermissionNode (111),
		//result += "00112: Point X: (0)  Z: (1) added.\n"),
		Info_00112_Point_XZ_Added (112),
		//result += "00113: (0) players online [Page (1) of (2)]\n"),
		Info_00113_PlayersOnline_Global (113),
		//result += "00114: (0) players online in (1) [Page (2) of (3)]\n"),
		Info_00114_PlayersOnline_WithinZone_X (114),
		//result += "00115: (0) - (1) - Distance: (2)\n"),
		Info_00115_PlayerOnlineWithZone (115),
		//result += "00116: (0) - Distance: (1)\n"),
		Info_00116_PlayerOnline (116),
		//result += "00117: No zone with the tag [(0)] exists.\n"),
		Warning_00117_Zone_X_DoesNotExist (117),
		//result += "00118: &BREGEN: &ADelay (0) Amount (1) Interval (2)\n"),
		Info_00118_Zone_Regen (118),
		//result += "00119: &BMOBS: &A(0)\n"),
		Info_00119_Zone_Mobs (119),
		//result += "00120: &B(0) &F> &A[(1)] &F: &A[(2)]\n"),
		Info_00120_Zone_PermissionTemplate (120),
		//result += "00121: &6Shape: &ACircle &F| &6Radius:&F (0)\n"),
		Info_00121_Zone_Shape_Cirdle (121),
		//result += "00122: &6Shape: &APolygon &F| &6Points:&F (0)\n"),
		Info_00122_Zone_Shape_Poly (122),
		//result += "00123: &6Children:&A (0)\n"),
		Info_00123_Zone_Children (123),
		//result += "00124: &6Enter Text:&A (0)\n"),
		Info_00124_Zone_EnterText (124),
		//result += "00125: &6Exit Text:&A (0)\n"),
		Info_00125_Zone_ExitText (125),
		//result += "00126: &6Parent:&A (0)\n"),
		Info_00126_Zone_Parent (126),
		//result += "00127: &6Owners:&A (0)\n"),
		Info_00127_Zone_Owners (127),
		//result += "00128: Successfully copied permissions from &A[(0)]\n"),
		Info_00128_CopiedPermissions (128),
		//result += "00129: Permissions cleared for &A[(0)]\n"),
		Info_00129_PermissionsClearedFor_X (129),
		
		
		//result += "01000: &6Help for /zone command.\n"),
		Help_01000 (1000),
		//result += "01001: &6/zone name &B[name] &A - Sets the name of the zone.\n"),
		Help_01001_Name (1001),
		//result += "01002: &6/zone flag &B[flag] [value] &A - Updates the flag to the value.\n"),
		Help_01002_Flag (1002),
		//result += "01003: &6/zone flag pvp &B[true|false] &A - Updates the pvp flag to the value.\n"),
		Help_01003_Flag_PVP (1003),
		//result += "01004: &6/zone flag fire &B[true|false] &A - Updates the fire flag to the value.\n"),
		Help_01004_Flag_Fire (1004),
		//result += "01005: &6/zone flag explode &B[true|false] &A - Updates the explode flag to the value.\n"),
		Help_01005_Flag_Explode (1005),
		//result += "01006: &6/zone flag mobs &B[MobType]... &A - Sets the allowed mobs.\n"),
		Help_01006_Flag_Mobs (1006),
		//result += "01007: &6/zone flag regen &B[Amount] <Delay> <Interval> <MaxRegen> <MinDegen> <RestDelay> <BedBonus> &A - Updates the regen flag to the values.\n"),
		Help_01007_Flag_Regen (1007),
		//result += "01008: &6/zone flag sanctuary &B[true|false] &A - Updates the sanctuary flag to the value.\n"),
		Help_01008_Flag_Sanctuary (1008),
		//result += "01009: &6/zone flag fireburnsmobs &B[true|false] &A - Updates the fireburnsmobs flag to the value.\n"),
		Help_01009_Flag_FireBunsMobs (1009),
		//result += "01010: &6/zone floor &B[value] &A - Updates the floor to value.\n"),
		Help_01010_Floor (1010),
		//result += "01011: &6/zone ceiling &B[value] &A - Updates the ceiling to value.\n"),
		Help_01011_Ceiling (1011),
		//result += "01012: &6/zone child &B[add|remove] [ZoneTag]... &A - Adds or Removes the entered ZoneTags.\n"),
		Help_01012_Child (1012),
		//result += "01013: &6/zone owner &B[add|remove] [PlayerName|GroupName]... &A - Adds or Removes the entered names.\n"),
		Help_01013_Owner (1013),
		//result += "01014: &6/zone message &B[enter|exit] [Message] &A - Updates the enter|exit message.\n"),
		Help_01014_Message (1014),
		//result += "01015: &6/zone world &B[WorldName] &A - Updates the world the zone is in.\n"),
		Help_01015_World (1015),
		//result += "01016: &6/zone draw &A - Prompts you to go into Draw mode.\n"),
		Help_01016_Draw (1016),
		//result += "01017: &6/zone cancel &A - Discards all current changes.\n"),
		Help_01017_Cancel (1017),
		//result += "01018: &6/zone delete &A - Deletes the zone you are currently editing.\n"),
		Help_01018_Edit_Delete (1018),
		//result += "01019: &6/zone save &A - Saves the zone to file.\n"),
		Help_01019_Edit_Save (1019),
		//result += "01020: &6/zone save &A - Saves the point data you have drawn.\n"),
		Help_01020_Draw_Save (1020),
		//result += "01021: &6/zone confirm &A - Clears point data and puts you into Draw mode.\n"),
		Help_01021_DrawConfirm_Confirm (1021),
		//result += "01022: &6/zone cancel &A - Puts you back into Edit mode.\n"),
		Help_01022_DrawConfirm_Cancel (1022),
		//result += "01023: &6/zone confirm &A - Deletes the zone you are currently editing.\n"),
		Help_01023_DeleteConfirm_Confirm (1023),
		//result += "01024: &6/zone edit &B[ZoneTag] &A - Begin editing the specified zone.\n"),
		Help_01024_Edit (1024),
		//result += "01025: &6/zone create &B[ZoneTag] &A - Create new zone with the specified tag.\n"),
		Help_01025_Create (1025),
		//result += "01026: &6/zone list &A - Lists existing zones.\n"),
		Help_01026_List (1026),
		//result += "01027: &6/zone info &B[ZoneTag] &A - Detailed info for specified zone.\n"),
		Help_01027_Info (1027);
		
		public int ID;
		private Message_ID(int ID)
		{
			this.ID = ID;
		}
	}
	
	private static Map<Integer, String> messageList = new HashMap<Integer, String>();

	public static void Send(CommandSender sender, Message_ID messageID) {
		String message = get(messageID);
		SendMessage(sender, message);
	}

	public static void Send(CommandSender sender, Message_ID messageID, String[] args) {
		String message = get(messageID, args);
		SendMessage(sender, message);
	}

	public static void Send(CommandSender sender, String message) {
		message = format(message);
		SendMessage(sender, message);
	}

	public static void Send(CommandSender sender, String message, String[] args) {
		message = format(message, args);
		SendMessage(sender, message);
	}

	private static void SendMessage(CommandSender sender, String message) {
		if (message.indexOf("@@") > -1) {
			String[] split = message.split("@@");
			for (int i = 0; i < split.length; i++) {
				sender.sendMessage(split[i].replace("@@", "").trim());
			}
		} else {
			sender.sendMessage(message.trim());
		}
	}

	public static String get(Message_ID messageID) {
		return get(messageID, null);
	}

	public static String get(Message_ID messageID, String[] args) {
		if (messageList == null || messageList.size() == 0) {
			LoadMessageList();
		}

		String result = messageList.get(messageID.ID);
		if (result != null) {
			result = format(result, args);
		} else {
			result = "Invalid Message ID: " + messageID;
		}
		return result;
	}

	public static String format(String message) {
		return format(message, null);
	}

	public static String format(String message, String[] args) {
		String result = message;

		// 0 BLACK
		// 1 DARK_BLUE
		// 2 DARK_GREEN
		// 3 DARK_AQUA
		// 4 DARK_RED
		// 5 DARK_PURPLE
		// 6 GOLD
		// 7 GRAY
		// 8 DARK_GRAY
		// 9 BLUE
		// A GREEN
		// B AQUA
		// C RED
		// D LIGHT_PURPLE
		// E YELLOW
		// F WHITE

		// Replace Color Codes
		result = result.replace("&0", ChatColor.BLACK.toString());
		result = result.replace("&1", ChatColor.DARK_BLUE.toString());
		result = result.replace("&2", ChatColor.DARK_GREEN.toString());
		result = result.replace("&3", ChatColor.DARK_AQUA.toString());
		result = result.replace("&4", ChatColor.DARK_RED.toString());
		result = result.replace("&5", ChatColor.DARK_PURPLE.toString());
		result = result.replace("&6", ChatColor.GOLD.toString());
		result = result.replace("&7", ChatColor.GRAY.toString());
		result = result.replace("&8", ChatColor.DARK_GRAY.toString());
		result = result.replace("&9", ChatColor.BLUE.toString());
		result = result.replace("&A", ChatColor.GREEN.toString());
		result = result.replace("&a", ChatColor.GREEN.toString());
		result = result.replace("&B", ChatColor.AQUA.toString());
		result = result.replace("&b", ChatColor.AQUA.toString());
		result = result.replace("&C", ChatColor.RED.toString());
		result = result.replace("&c", ChatColor.RED.toString());
		result = result.replace("&D", ChatColor.LIGHT_PURPLE.toString());
		result = result.replace("&d", ChatColor.LIGHT_PURPLE.toString());
		result = result.replace("&E", ChatColor.YELLOW.toString());
		result = result.replace("&e", ChatColor.YELLOW.toString());
		result = result.replace("&F", ChatColor.WHITE.toString());
		result = result.replace("&f", ChatColor.WHITE.toString());

		// Insert Variable Data
		// Variables look like this: (0), (1)
		// Variables start at 0 and increment
		if (args != null) 
		{
			for (int i = 0; i < args.length; i++) 
			{
				result = result.replace("(" + i + ")", args[i]);
			}
		}

		// Format Numbers
		// Number format string is always ID 0 in language file
		String[] split = (result.trim() + " ").split(" ");
		if (split != null) 
		{
			NumberFormat formatter = new DecimalFormat(messageList.get(0));
			boolean changesMade = false;
			String updatedResult = "";
			for (int i = 0; i < split.length; i++) 
			{
				if (General.IsNumeric(split[i])) 
				{
					updatedResult = updatedResult + formatter.format(Integer.parseInt(split[i])) + " ";
					changesMade = true;
				} 
				else 
				{
					updatedResult = updatedResult + split[i] + " ";
				}
			}
			if (changesMade) 
			{
				result = updatedResult;
			}
		}
		return result.trim();
	}

	public static void LoadMessageList() 
	{
		String line;
		File file = new File(General.plugin.getDataFolder() + File.separator + "Language" + File.separator + General.config.language + ".txt");
		messageList = new HashMap<Integer, String>();
		boolean updateNeeded = false;
		boolean foundVersion = false;
		try 
		{
			if (!file.exists()) 
			{
				InitMessageList();
			}
			Scanner scanner = new Scanner(file);
			try 
			{
				while (scanner.hasNext()) 
				{
					line = scanner.nextLine().trim();
					if(!line.isEmpty())
					{
						if(line.startsWith("#"))
						{
							int versionIndex = line.indexOf("VERSION");
							if(versionIndex > -1)
							{
								foundVersion = true;
								String version = line.substring(line.indexOf(":") + 1, line.length());
								if(version != null && version.length() > 0)
								{
									if(!version.trim().equalsIgnoreCase(General.version))
									{
										updateNeeded = true;
										break;
									}
								}
								else
								{
									updateNeeded = true;
									break;
								}
							}
						}
						else
						{
							Integer id;
							String message;
							id = Integer.parseInt(line.substring(0, line.indexOf(":")).trim());
							message = line.substring(line.indexOf(":") + 1, line.length());
							messageList.put(id, message);
						}
					}					
				}
			} 
			finally 
			{
				scanner.close();
			}

			if(updateNeeded || !foundVersion)
			{
				Log.Write("Updating Message List...");
				InitMessageList();
				LoadMessageList();
			}
			else
			{
				Log.Write("Language File Loaded [" + General.config.language + ".txt" + "].");	
			}
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	}

	private static void InitMessageList() 
	{
		File file = new File(General.plugin.getDataFolder() + File.separator + "Language");

		if (!General.plugin.getDataFolder().exists()) 
		{
			General.plugin.getDataFolder().mkdir();
		}
		if (!file.exists()) 
		{
			file.mkdir();
		}
		file = new File(file + File.separator + General.config.language + ".txt");

		try 
		{
			if (!file.exists()) 
			{
				file.createNewFile();
			}
			String data = BuildInitialStrings();
			Writer output = new BufferedWriter(new FileWriter(file, false));
			try 
			{
				output.write(data);
			}
			finally 
			{
				output.close();
			}
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	}

	private static String BuildInitialStrings() {

		// if(General.config.language.equalsIgnoreCase("IT"))
		// {
		// return BuildInitialStrings_IT();
		// }
		return BuildInitialStrings_ENUS();
	}

	private static String BuildInitialStrings_ENUS() {
		String result = new String();
		result += "#VERSION: " + General.version + "\n";
		result += "#Author: jblaske@gmail.com\n";
		result += "#--------------------------\n";
		result += "#Formatting Strings\n";
		result += "00000: ###,###,###\n";
		result += "00001: &6(0):&A (1)\n";
		result += "00002: &B(0): &AON\n";
		result += "00003: &B(0): &COFF\n";
		result += "00004: &6(0)\n";
		result += "00005: &A(0)&6 (1)\n";
		result += "\n";
		result += "#Static Strings\n";
		result += "00010: &B[] &6Required Parameter &B<> &6Optional Parameter @@&B... &6 Indicates repeatable. &B| &6 Indicates either/or\n";
		result += "00011: &6You are currently in Edit mode.\n";
		result += "00012: &6You are currently in Draw mode.\n";
		result += "00013: &6You are currently in Draw Confirm mode.\n";
		result += "00014: &6You are currently in Delete Confirm mode.\n";
		result += "00015: EpicZones Reloaded.\n";
		result += "00016: Zone modification cancelled, no changes were saved.\n";
		result += "00017: Draw Mode canceled, back in Edit Mode. type /zone for more options.\n";
		result += "00018: Zone children updated.\n";
		result += "00019: Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.\n";
		result += "00020: Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.\n";
		result += "00021: WARNING! Entering draw mode will erase all points for the zone! Type /zone draw confirm or /zone draw deny.\n";
		result += "00022: Global zones cannot be edited.\n";
		result += "00023: No zones to list.\n";
		result += "00024: You don't own any zones.\n";
		result += "00025: You did not specify if you wanted to edit the 'enter' or 'exit' text.\n";
		result += "00026: Zone Owners Updated.\n";
		result += "00027: Zone Parents Updated.\n";
		result += "00028: You must specify a single center point, before setting the radius.\n";
		result += "00029: Drawing Complete.\n";
		result += "00030: You must draw at least 3 points or 1 point and set a radius, before you can move on.\n";
		result += "00031: Zone Saved.\n";
		result += "00032: You do not have permission to destroy in this zone.;\n";
		result += "00033: You do not have permission to destroy outside the border of the map.;\n";
		result += "00034: You do not have permission to build in this zone.;\n";
		result += "00035: You do not have permission to build outside the border of the map.;\n";
		result += "00036: You do not have permission to do that in this zone.\n";
		result += "00037: You do not have permission to use this command.\n";
		result += "00038: &6Zone Flags:\n";
		result += "00039: &6Permissions:\n";
		result += "00040: Permissions cleared.\n";
		result += "\n";
		result += "#Variable Strings\n";
		result += "00100: Zone Updated. Set (0) to [(1)]\n";
		result += "00101: (0) is not a numerical value.\n";
		result += "00102: Zone [(0)] has been deleted.\n";
		result += "00103: A zone already exists with the tag [(0)]\n";
		result += "00104: To continue deleting the zone [(0)] type /zone confirm.\n";
		result += "00105: You are now editing zone [(0)]\n";
		result += "00106: You do not have permission to edit the zone [(0)].\n";
		result += "00107: Zone Updated. Set flag [(0)] to [(1)].\n";
		result += "00108: [(0)] is not a valid flag.@@Valid flags are: pvp, mobs, regen, fire, explode, sanctuary\n";
		result += "00109: Permission Added: [(0)] > [(1)] : [(2)]\n";
		result += "00110: [(0)] is not a valid permission type.\n";
		result += "00111: [(0)] is not a valid permission node.\n";
		result += "00112: Point X: (0)  Y: (1) added.\n";
		result += "00113: (0) players online [Page (1) of (2)]\n";
		result += "00114: (0) players online in (1) [Page (2) of (3)]\n";
		result += "00115: (0) - (1) - Distance: (2)\n";
		result += "00116: (0) - Distance: (1)\n";
		result += "00117: No zone with the tag [(0)] exists.\n";
		result += "00118: &BREGEN: &ADelay (0) Amount (1) Interval (2)\n";
		result += "00119: &BMOBS: &A(0)\n";
		result += "00120: &B(0) &F> &A[(1)] &F: &A[(2)]\n";
		result += "00121: &6Shape: &ACircle &F| &6Radius:&F (0)\n";
		result += "00122: &6Shape: &APolygon &F| &6Points:&F (0)\n";
		result += "00123: &6Children:&A (0)\n";
		result += "00124: &6Enter Text:&A (0)\n";
		result += "00125: &6Exit Text:&A (0)\n";
		result += "00126: &6Parent:&A (0)\n";
		result += "00127: &6Owners:&A (0)\n";
		result += "00128: Successfully copied permissions from &A[(0)]\n";
		result += "00129: Permissions cleared for &A[(0)]\n";
		result += "\n";
		result += "#Help Strings\n";
		result += "01000: &6Help for /zone command.\n";
		result += "01001: &6/zone name &B[name] &A - Sets the name of the zone.\n";
		result += "01002: &6/zone flag &B[flag] [value] &A - Updates the flag to the value.\n";
		result += "01003: &6/zone flag pvp &B[true|false] &A - Updates the pvp flag to the value.\n";
		result += "01004: &6/zone flag fire &B[true|false] &A - Updates the fire flag to the value.\n";
		result += "01005: &6/zone flag explode &B[true|false] &A - Updates the explode flag to the value.\n";
		result += "01006: &6/zone flag mobs &B[MobType]... &A - Sets the allowed mobs.\n";
		result += "01007: &6/zone flag regen &B[Amount] <Delay> <Interval> <MaxRegen> <MinDegen> <RestDelay> <BedBonus> &A - Updates the regen flag to the values.\n";
		result += "01008: &6/zone flag sanctuary &B[true|false] &A - Updates the sanctuary flag to the value.\n";
		result += "01009: &6/zone flag fireburnsmobs &B[true|false] &A - Updates the fireburnsmobs flag to the value.\n";
		result += "01010: &6/zone floor &B[value] &A - Updates the floor to value.\n";
		result += "01011: &6/zone ceiling &B[value] &A - Updates the ceiling to value.\n";
		result += "01012: &6/zone child &B[add|remove] [ZoneTag]... &A - Adds or Removes the entered ZoneTags.\n";
		result += "01013: &6/zone owner &B[add|remove] [PlayerName|GroupName]... &A - Adds or Removes the entered names.\n";
		result += "01014: &6/zone message &B[enter|exit] [Message] &A - Updates the enter|exit message.\n";
		result += "01015: &6/zone world &B[WorldName] &A - Updates the world the zone is in.\n";
		result += "01016: &6/zone draw &A - Prompts you to go into Draw mode.\n";
		result += "01017: &6/zone cancel &A - Discards all current changes.\n";
		result += "01018: &6/zone delete &A - Deletes the zone you are currently editing.\n";
		result += "01019: &6/zone save &A - Saves the zone to file.\n";
		result += "01020: &6/zone save &A - Saves the point data you have drawn.\n";
		result += "01021: &6/zone confirm &A - Clears point data and puts you into Draw mode.\n";
		result += "01022: &6/zone cancel &A - Puts you back into Edit mode.\n";
		result += "01023: &6/zone confirm &A - Deletes the zone you are currently editing.\n";
		result += "01024: &6/zone edit &B[ZoneTag] &A - Begin editing the specified zone.\n";
		result += "01025: &6/zone create &B[ZoneTag] &A - Create new zone with the specified tag.\n";
		result += "01026: &6/zone list &A - Lists existing zones.\n";
		result += "01027: &6/zone info &B[ZoneTag] &A - Detailed info for specified zone.\n";

		return result;
	}
}
