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

package com.bukkit.epicsaga.EpicManager.misc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bukkit.epicsaga.EpicManager.CommandHandler;
import com.bukkit.epicsaga.EpicManager.EpicManager;
import com.bukkit.epicsaga.EpicManager.PluginFeature;
import com.bukkit.epicsaga.EpicManager.EpicManager.EnableError;

/**
 * Handles Miscelaneous plugin features
 *
 * Commands:
 * <li>/kick (epicmanager.kick) - kick the user from the server</li>
 *
 * @author _sir_maniac
 *
 * Notes:
 * kick feature doesn't prevent player from immediately rejoining
 *
 */
public class MiscFeature implements PluginFeature {
	private final KickCommand kickCommand = new KickCommand();

	private EpicManager plugin;


	public void onEnable(EpicManager em) throws EnableError {
		plugin = em;

		em.registerCommand("kick", kickCommand);
	}

	public void onDisable(EpicManager em) {

	}


	private class KickCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op,
	    		String[] playerNames) {
	    	if(op instanceof Player &&
	    	   !EpicManager.permissions.has((Player)op, "epicmanager.kick"))
	    		return true;

	    	if(playerNames.length == 0)
	    		return false;

	    	for(String playerName : playerNames) {
		    	Player player = plugin.getPlayerByDisplayName(playerName);

		    	if(player != null) {
		    		String opName;

		    		//TODO: make kick message configurable
		    		player.kickPlayer(plugin.config.kickMessage);
		    		op.sendMessage(ChatColor.YELLOW+"Player kicked from server.");

		    		if(op instanceof Player)
		    			opName = ((Player)op).getName();
		    		else
		    			opName = "console";

		    		plugin.logInfo("Player '" + player + "' kicked from server by '" +
		    				opName + "'.");
		    	}
		    	else {
		    		op.sendMessage(ChatColor.YELLOW+"Player not found.");
		    	}
	    	}
	    	return true;
	    }

	}


}
