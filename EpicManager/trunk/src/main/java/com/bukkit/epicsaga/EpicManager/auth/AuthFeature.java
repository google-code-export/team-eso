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

package com.bukkit.epicsaga.EpicManager.auth;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.PluginManager;

import com.bukkit.epicsaga.EpicManager.CommandHandler;
import com.bukkit.epicsaga.EpicManager.EpicManager;
import com.bukkit.epicsaga.EpicManager.PluginFeature;
import com.bukkit.epicsaga.EpicManager.EpicManager.EnableError;

/**
 * A PluginFeature that handles user login authorization.  Currently implemented
 *   as a flat-file whitelist.
 *
 * @author _sir_maniac
 *
 */
public class AuthFeature implements PluginFeature, CommandHandler {
	private static final String WHITELIST_FILE = "whitelist.txt";

	private final PListener pListener = new PListener();

	private EpicManager plugin;

	public UserAuthenticator userAuth = null;

	public void onEnable(EpicManager em) throws EnableError {
		plugin = em;
    	File file  = new File(em.getDataFolder() + File.separator +
    			WHITELIST_FILE);

        try {
        	userAuth = new FileWhitelist(file);
		}
        catch (IOException e) {
    		throw new EnableError(" Could not access or create " +
    				file.getPath(), e);
		}

        em.registerCommand("ban", this);
        em.registerCommand("unban", this);

        PluginManager pm = em.getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_LOGIN, pListener, Priority.Highest, em);
	}

	public void onDisable(EpicManager em) {

	}

	public boolean onCommand(String command, CommandSender sender, String[] args) {
		if(command.equalsIgnoreCase("ban"))
			return doBan(sender, args);
		else if(command.equalsIgnoreCase("unban"))
			return doUnban(sender, args);
		return true;
	}

    private boolean doBan(CommandSender op, String[] playerNames) {
    	if(op instanceof Player &&
    	   !EpicManager.permissions.has((Player)op, "epicmanager.ban"))
	    		return true;

    	if(playerNames.length == 0)
    		return false;

		for(String playerName : playerNames) {
			userAuth.deny(playerName);

			op.sendMessage(
					ChatColor.YELLOW+
					"Player '"+playerName+"' no longer allowed.");

			Player player = plugin.getPlayerByDisplayName(playerName);

			if(player != null) {
				player.kickPlayer(plugin.config.banMessage);

	    		String opName;
	    		if(op instanceof Player)
	    			opName = ((Player)op).getName();
	    		else
	    			opName = "console";

	    		plugin.logInfo("Player '" + playerName +
						"' denied server access by '" +
						opName + "'.");
			}
		}

		return true;
    }

    private boolean doUnban(CommandSender op, String[] playerNames) {
    	if(op instanceof Player &&
    	   !EpicManager.permissions.has((Player)op, "epicmanager.unban"))
    		return true;

    	if(playerNames.length == 0)
    		return false;

    	for(String playerName : playerNames) {
    		userAuth.accept(playerName);

	    	op.sendMessage(ChatColor.YELLOW+"Player '"+playerName+
	    			"' is now allowed.");

    		String opName;
    		if(op instanceof Player)
    			opName = ((Player)op).getName();
    		else
    			opName = "console";

    		plugin.logInfo("Player '" + playerName + "' accepted on server by '" +
					opName + "'.");
    	}

		return true;
    }

	private class PListener extends PlayerListener {

		@Override
		public void onPlayerLogin(PlayerLoginEvent event) {
			String name = event.getPlayer().getName();

			if(userAuth.isAllowed(name)) {
				event.allow();
				return;
			}
			if (plugin.config == null) {
				System.out.println("--------------------Null");
			}
			event.disallow(Result.KICK_BANNED, plugin.config.authMessage);
		}

	}

}
