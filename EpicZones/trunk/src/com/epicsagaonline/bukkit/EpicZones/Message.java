package com.epicsagaonline.bukkit.EpicZones;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Message
{

	public enum Message_ID
	{
		Format_Numeric(0), Format_Flag_On(2), Format_Flag_Off(3), Format_KeyValue(5),

		Info_00010_Parameter_Description(10), Mode_00011_Edit(11), Mode_00012_Draw(12), Mode_00013_DrawConfirm(13), Mode_00014_DeleteConfirm(14), Info_00015_Reloaded(15), Info_00016_Cancel_Edit(16), Info_00017_Cancel_Draw(17), Info_00018_Updated_Children(18), Mode_00019_Draw_StartMessage(19), Mode_00020_Draw_StartAfterNew(20), Mode_00021_DrawConfirm_Warning(21), Warning_00022_CannotDeleteGlobalZones(22), Warning_00023_NoZones(23), Warning_00024_NoZones_Owner(24), Warning_00025_NoEnter_NoExit(25), Info_00026_OwnersUpdated(26), Info_00027_ParentsUpdated(27), Warning_00028_Draw_NoCenterOfCircle(28), Info_00029_DrawingComplete(29), Warning_00030_Draw_Need3Points(30), Info_00031_ZoneSaved(31), Warning_00032_Perm_DestroyInZone(32), Warning_00033_Perm_DestroyOutsideRadius(33), Warning_00034_Perm_BuildInZone(34), Warning_00035_Perm_BuildOutsideRadius(35), Warning_00036_Perm_GenericInZone(36), Warning_00037_Perm_Command(37), Info_00038_ZoneFlags(38), Info_00039_Permissions(39), Info_00040_PermissionsCleared(40), Info_00041_DebugGenerated(41),

		Info_00100_ZoneUpdatedSet_X_to_Y(100), Warning_00101_X_IsNotNumeric(101), Info_00102_Zone_X_Deleted(102), Warning_00103_Zone_X_Exists(103), Info_00104_Mode_ZoneDelete(104), Info_00105_Mode_Edit(105), Warning_00106_Perm_EditZone(106), Info_00107_ZoneFlagUpdated_X_to_Y(107), Warning_00108_InvalidFlag(108), Info_00109_PermissionAdded(109), Warning_00110_InvalidPermissionType(110), Warning_00111_InvalidPermissionNode(111), Info_00112_Point_XZ_Added(112), Info_00113_PlayersOnline_Global(113), Info_00114_PlayersOnline_WithinZone_X(114), Info_00115_PlayerOnlineWithZone(115), Info_00116_PlayerOnline(116), Warning_00117_Zone_X_DoesNotExist(117), Info_00118_Zone_Regen(118), Info_00119_Zone_Mobs(119), Info_00120_Zone_PermissionTemplate(120), Info_00121_Zone_Shape_Cirdle(121), Info_00122_Zone_Shape_Poly(122), Info_00123_Zone_Children(123), Info_00124_Zone_EnterText(124), Info_00125_Zone_ExitText(125), Info_00126_Zone_Parent(126), Info_00127_Zone_Owners(127), Info_00128_CopiedPermissions(128), Info_00129_PermissionsClearedFor_X(129),

		Help_01000(1000), Help_01001_Name(1001), Help_01002_Flag(1002), Help_01003_Flag_PVP(1003), Help_01004_Flag_Fire(1004), Help_01005_Flag_Explode(1005), Help_01006_Flag_Mobs(1006), Help_01007_Flag_Regen(1007), Help_01008_Flag_Sanctuary(1008), Help_01009_Flag_FireBunsMobs(1009), Help_01010_Floor(1010), Help_01011_Ceiling(1011), Help_01012_Child(1012), Help_01013_Owner(1013), Help_01014_Message(1014), Help_01015_World(1015), Help_01016_Draw(1016), Help_01017_Cancel(1017), Help_01018_Edit_Delete(1018), Help_01019_Edit_Save(1019), Help_01020_Draw_Save(1020), Help_01021_DrawConfirm_Confirm(1021), Help_01022_DrawConfirm_Cancel(1022), Help_01023_DeleteConfirm_Confirm(1023), Help_01024_Edit(1024), Help_01025_Create(1025), Help_01026_List(1026), Help_01027_Info(1027);

		public int ID;

		private Message_ID(int ID)
		{
			this.ID = ID;
		}
	}

	public static Map<Integer, String> messageList = new HashMap<Integer, String>();

	public static void Send(CommandSender sender, Message_ID messageID)
	{
		String message = get(messageID);
		SendMessage(sender, message);
	}

	public static void Send(CommandSender sender, Message_ID messageID, String[] args)
	{
		String message = get(messageID, args);
		SendMessage(sender, message);
	}

	public static void Send(CommandSender sender, String message)
	{
		message = format(message);
		SendMessage(sender, message);
	}

	public static void Send(CommandSender sender, String message, String[] args)
	{
		message = format(message, args);
		SendMessage(sender, message);
	}

	private static void SendMessage(CommandSender sender, String message)
	{
		if (message.indexOf("@@") > -1)
		{
			String[] split = message.split("@@");
			for (int i = 0; i < split.length; i++)
			{
				sender.sendMessage(split[i].replace("@@", "").trim());
			}
		}
		else
		{
			sender.sendMessage(message.trim());
		}
	}

	public static String get(Message_ID messageID)
	{
		return get(messageID, null);
	}

	public static String get(Message_ID messageID, String[] args)
	{
//		if (messageList == null || messageList.size() == 0)
//		{
//			LoadMessageList();
//		}

		String result = messageList.get(messageID.ID);
		if (result != null)
		{
			result = format(result, args);
		}
		else
		{
			result = "Invalid Message ID: " + messageID;
		}
		return result;
	}

	public static String format(String message)
	{
		return format(message, null);
	}

	public static String format(String message, String[] args)
	{
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

	
}
