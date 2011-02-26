/*

	This file is part of EpicManager

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
 * @author sir.manic@gmail.com
 * @license MIT License
 */

package com.epicsagaonline.bukkit.EpicManager.give;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.epicsagaonline.bukkit.EpicManager.CommandHandler;
import com.epicsagaonline.bukkit.EpicManager.EpicManager;
import com.epicsagaonline.bukkit.EpicManager.PluginFeature;
import com.epicsagaonline.bukkit.EpicManager.EpicManager.EnableError;

public class GiveFeature implements PluginFeature, CommandHandler {
	private static final String PERM_GIVE = "epicmanager.give";
	private static final String[] COMMANDS =  
		new String[] {"give", "i", "il", "ilist", "givelist", "givel"};
	
	private EpicManager plugin;

	private MaterialDB db = new MaterialDB();
	MaterialType[] types;
	
	public GiveFeature() {
	}
	
	public void onEnable(EpicManager em) throws EnableError {
		plugin = em;
		db.setFile(plugin.getMaterialsFile());
		db.load();
		types = db.toArray();
		
		for (String command : COMMANDS) {
			em.registerCommand(command, this);
		}
	}

	public void onDisable(EpicManager em) {
	}
	
	
	/**
	 * 
	 * @return the number of items actually given, if inventory is full, this
	 *         will be zero.
	 */
	private int giveToPlayer(Player player, 
			   Material material, Byte data, int count) {
		HashMap<Integer, ItemStack> ret;
		
		ItemStack stack;
		if(data != null)
			stack = new ItemStack(material, count, data);
		else
			stack = new ItemStack(material, count);
			
		ret = player.getInventory().addItem(stack);
		
		if (ret.isEmpty()) {
			return count;
		}
		else {
			int left = 0;
			for (ItemStack i : ret.values() ) {
				left += i.getAmount();
			}
			return count-left;
		}
	}
	
	private String columnFormatter(String[] entries, int columns) {
		
		int numEntries = entries.length;
		
		int maxWidth=0;
		int i, j;
		for (i=0; i < numEntries; i++) {
			maxWidth = Math.max(maxWidth, entries[i].length());
		}
		
		//int totalWidth = maxWidth*columns+1;
		
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);

		int lines = (numEntries+columns-1)/columns;
		int nextEntry;
		for(i=0; i < lines; i++) {
			nextEntry = i;
			f.format("%-"+maxWidth+"."+maxWidth+"s", entries[nextEntry]);
			
			for(j=1; j < columns; j++) {
				nextEntry += lines;
				if (nextEntry >= numEntries)
					break;

				sb.append(" ");
				f.format("%-"+maxWidth+"."+maxWidth+"s", entries[nextEntry]);
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	
	private boolean doList(CommandSender sender, String[] args) {
		final int LINES_PER_PAGE = 8;
		final int COLUMN_WIDTH = 32;
		final int COLUMNS = 2;
		
		
		if (args.length > 1) { 
			sender.sendMessage(ChatColor.RED+"Too many arguments.");
			return false;
		}

		int page;
		if (args.length == 1) {
			try{
				page = Integer.parseInt(args[0])-1;
				if (page < 0) 
					page=0;
			}
			catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED+"Bad argument, must be page number.");
				return false;
			}
		}
		else {
			page = 0;
		}
		
		int length = types.length;
		int numlines = (length+COLUMNS-1)/COLUMNS -1;
		int maxPage = (numlines+LINES_PER_PAGE-1)/LINES_PER_PAGE -1;
		
		if(page > maxPage)
			page = maxPage;
		
		int start = page*LINES_PER_PAGE*COLUMNS;
		int end = start+LINES_PER_PAGE*COLUMNS;
		
		if (end > length) 
			end = length;
		

		StringBuilder message = new StringBuilder();
		
		message.append(ChatColor.GREEN) 
			.append(length)  
			.append(" Types Available [Page ")
			.append(ChatColor.YELLOW) .append(page+1)
			.append(ChatColor.GREEN)  .append(" of ")
			.append(ChatColor.YELLOW) .append(maxPage+1)
			.append(ChatColor.GREEN+"]\n");
		
		List<String> lines = new ArrayList<String>();
		StringBuilder entry;
		MaterialType type;
		String[] names;
		for(int i=start; i < end; i++) {
			entry = new StringBuilder();
			type = types[i];
			names = type.getNames();
			
			entry.append(ChatColor.BLUE + " " + names[0]);
			entry.append(" ("+type.getDesc()+")");
			if (names.length > 1) {
				entry.append(" : "+names[1]);
				
				for (int j = 2; j < names.length; j++) {
					entry.append(", "+names[j]);
				}
			}
			if(entry.length() > COLUMN_WIDTH) {
				entry.setLength(COLUMN_WIDTH-3);
				entry.append("...");
			}
			
			lines.add(entry.toString());
		}
		
		message.append(columnFormatter(lines.toArray(new String[0]), COLUMNS));
		
		Scanner scanner = new Scanner(message.toString());
		
		String out;
		while (scanner.hasNextLine()) {
			out = scanner.nextLine();
			sender.sendMessage(out);
		}
		return true;
	}
	
	private boolean doGive(CommandSender sender, String[] args) {
		String playerArg = null;
		String itemArg = null;
		String countArg = null;
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED+"Usage:");
			return false;
		}

		if (args[0].equalsIgnoreCase("to")) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED+"too few arguments");
				return false;
			}
			
			playerArg = args[1];
			itemArg = args[2];

			if(args.length >= 4) {
				countArg = args[3];
			}
		}
		else if (args.length == 2) {
			itemArg = args[0];
			countArg = args[1];
		}
		else if (args.length == 1) {
			itemArg = args[0];
		}
		
		Player player;
		if(playerArg != null) {
			player = plugin.getPlayerByDisplayName(playerArg);
			if (player == null) {
				sender.sendMessage(ChatColor.RED+"Don't know argument: "+playerArg);
				return false;
			}
		}
		else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED+"You must specify the player.");
				return false;
			}
			player = (Player)sender;
		}
		
		MaterialType type = db.getByDesc(itemArg);
		if(type == null) {
			sender.sendMessage(ChatColor.RED+"Don't know argument: "+itemArg);
			return false;	
		}
		
		int count;
		if (countArg == null) {
			count = 1;
		}
		else {
			try {
				count = Integer.parseInt(countArg);
			}
			catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED+"Bad amount: "+countArg);
				return false;	
			}
		}
		
		count = giveToPlayer(player, type.getMaterial(), type.getData(), count);
		
		if(count < 1) {
			sender.sendMessage(ChatColor.YELLOW+"Inventory is full");
		}
		else {
			String typeName = type.getNames()[0]; 
			String added = typeName + " (";
				
			// display data byte,
			// which is not needed if the type_name has the same data value
			if (type.getData() != null &&
					!db.getByName(typeName).equals(type)) {
				added += "type: "+type.getData()+", ";
			}
			
			added += "amount: "+count+")";
	
			String senderName;
			if (sender instanceof Player)
				senderName = ((Player)sender).getDisplayName();
			else
				senderName = "console";
			
			if (player != sender) {
				sender.sendMessage(ChatColor.GREEN+"Added "+added+" to player "+
						player.getDisplayName());
				
				player.sendMessage(ChatColor.GREEN+senderName+" added "+added+
						" to your inventory");
				
				EpicManager.logInfo(senderName+" used /give to add "+added+" to "+
						player.getDisplayName());
			}
			else {
				player.sendMessage(ChatColor.GREEN+"Added "+added+
				" to your inventory");
			
				EpicManager.logInfo(senderName+" used /give to add "+added);
			}
		}
		
		return true;
	}
	
	
	/*
	 * <command> [to <player>] <id/name>[:<type>] [amount]
	 * <command> list [page]
	 */
	public boolean onCommand(String command, CommandSender sender, String[] args) {
		if (sender instanceof Player && 
				!EpicManager.permissions.has((Player)sender, PERM_GIVE)) {
			return true;
		}
		
		if(command.equalsIgnoreCase("givelist") || command.equalsIgnoreCase("givel") ||
				command.equalsIgnoreCase("ilist") || command.equalsIgnoreCase("il")) {
			return doList(sender, args);
		}
		
		if (args.length > 0 && 
				(args[0].equalsIgnoreCase("list") || 
						args[0].equalsIgnoreCase("l")) 
			) { 
			return doList(sender, Arrays.copyOfRange(args, 1, args.length));
		}
		else {
			return doGive(sender, args);
		}
	}
}
