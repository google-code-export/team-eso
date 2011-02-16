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

import java.io.FileNotFoundException;

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

/* TODO: make the permission work directly from the permissions file.
 * Also support a banned reason to be included.
 */


/**
 * A PluginFeature that handles user login authorization.  Currently implemented
 *   as a flat-file whitelist.
 *
 * Commands implemented:
 * <li> /ban &lt; player > [reason] (epimanager.ban) - prevent player from logging in</li>
 * <li> /unban &lt; playerlist > (epicmanager.unban) - allow the player to log in</li>
 * <li> /kick &lt; playerlist > (epicmanager.unban) - allow the player to log in</li>
 *
 * @author _sir_maniac
 *
 */
public class AuthFeature implements PluginFeature{
	private static final String PERM_BAN = "epicmanager.ban";
	private static final String PERM_UNBAN = "epicmanager.unban";
	private static final String PERM_KICK = "epicmanager.kick";
	
	private static final String[] BAN_COMMANDS = {"ban", "disallow"};
	private static final String[] ALLOW_COMMANDS = {"unban", "allow", "pardon"};
	private static final String[] KICK_COMMANDS = {"kick"};
	
	private final PListener pListener = new PListener();

	private EpicManager plugin;

	public PlayerAuthenticator userAuth = null;

	public void onEnable(EpicManager em) throws EnableError {
		plugin = em;

        try {
        	userAuth = new PermissionLoginAuthenticator(em.getServer(), 
        					plugin.config.addGroup);
		}
        catch (FileNotFoundException e) {
    		throw new EnableError(" Could not access permissions.  " +
    				"Is Permissions plugin installed?" , e);
		}

        for (String cmd : BAN_COMMANDS) {
            em.registerCommand(cmd, banHandler);
        }
        
        for (String cmd : ALLOW_COMMANDS) {
            em.registerCommand(cmd, allowHandler);
        }

        for (String cmd : KICK_COMMANDS) {
            em.registerCommand(cmd, kickHandler);
        }
        
        PluginManager pm = em.getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_LOGIN, pListener, Priority.Highest, em);
	}

	public void onDisable(EpicManager em) {

	}


	private CommandHandler banHandler = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(op instanceof Player &&
	    	   !EpicManager.permissions.has((Player)op, PERM_BAN))
		    		return true;
	
	    	if(args.length == 0)
	    		return false;
	
	    	String playerName = args[0];
	    	String banReason = null;
	    	if(args.length > 1) {
	        	StringBuilder b = new StringBuilder();
	    		for(int i = 1; i < args.length; i++) {
	    			b.append(args[i]).append(" ");
	    		}
	    		banReason = b.toString().trim();
	    	}
	    	
			userAuth.deny(playerName, banReason);
	
			// send message to person who gave command 
			op.sendMessage(
					ChatColor.YELLOW+
					"Player '"+playerName+"' no longer allowed.");
	
			// Write log message and kick player if on server.
			String opName;
			if(op instanceof Player)
				opName = ((Player)op).getName();
			else
				opName = "console";
	
			String banMessage = plugin.config.banMessage;
			String logMessage = opName + " banned player '"+playerName+"'";
	
			if (banReason != null) {
				banMessage = banMessage + " Ban Reason: " + banReason;
				logMessage = logMessage + " Ban Reason: " + banReason;
			}
			
			EpicManager.logInfo(logMessage);
	
			Player player = plugin.getPlayerByDisplayName(playerName);
			if(player != null) {
				player.kickPlayer(banMessage);
			}
	
			return true;
	    }
	};

	private CommandHandler allowHandler = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(op instanceof Player &&
	    	   !EpicManager.permissions.has((Player)op, PERM_UNBAN))
	    		return true;
	
	    	if(args.length == 0)
	    		return false;
	
	    	for(String playerName : args) {
	    		userAuth.accept(playerName);
	
		    	op.sendMessage(ChatColor.YELLOW+"Player '"+playerName+
		    			"' is now allowed.");
	
	    		String opName;
	    		if(op instanceof Player)
	    			opName = ((Player)op).getName();
	    		else
	    			opName = "console";
	
	    		EpicManager.logInfo("Player '" + playerName + "' accepted on server by '" +
						opName + "'.");
	    	}
	
			return true;
	    }
	};

	private CommandHandler kickHandler = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op,
	    		String[] playerNames) {
	    	if(op instanceof Player &&
	    	   !EpicManager.permissions.has((Player)op, PERM_KICK))
	    		return true;

	    	if(playerNames.length == 0)
	    		return false;

	    	for(String playerName : playerNames) {
		    	Player player = plugin.getPlayerByDisplayName(playerName);

		    	if(player != null) {
		    		String opName;

		    		player.kickPlayer(plugin.config.kickMessage);
		    		op.sendMessage(ChatColor.YELLOW+"Player kicked from server.");

		    		if(op instanceof Player)
		    			opName = ((Player)op).getName();
		    		else
		    			opName = "console";

		    		EpicManager.logInfo("Player '" + player + "' kicked from server by '" +
		    				opName + "'.");
		    	}
		    	else {
		    		op.sendMessage(ChatColor.YELLOW+"Player not found: "+playerName);
		    	}
	    	}
	    	return true;
	    }
	};

	private class PListener extends PlayerListener {

		@Override
		public void onPlayerLogin(PlayerLoginEvent event) {
			String name = event.getPlayer().getName();

			if(userAuth.isAllowed(name)) {
				event.allow();
				return;
			}
			if (plugin.config == null) {
				EpicManager.logWarning("AuthFeature: EpicManager.config is null");
			}
			String reason = userAuth.getBannedReason(name);
			
			String authMessage;
			
			if (reason != null) {
				authMessage = plugin.config.noLoginMessage + " Ban Reason: "+reason;
			}
			else {
				authMessage = plugin.config.noLoginMessage;
			}
			
			event.disallow(Result.KICK_BANNED, authMessage);
		}

	}

}
