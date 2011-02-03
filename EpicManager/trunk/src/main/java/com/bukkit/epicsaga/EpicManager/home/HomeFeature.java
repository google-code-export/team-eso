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

package com.bukkit.epicsaga.EpicManager.home;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import com.bukkit.epicsaga.EpicManager.CommandHandler;
import com.bukkit.epicsaga.EpicManager.EpicManager;
import com.bukkit.epicsaga.EpicManager.PluginFeature;

/**
 * A PluginFeature that handles a player's alternate spawn points.
 *
 *
 * Commands implemented:
 * <li> /sethome (epimanager.home.sethome) - set your home</li>
 * <li>{@literal /sethome <player>}  (epimanager.home.setplayerhome) -
 * 		set a player's home to your location</li>
 * <li> /rmhome (epimanager.home.rmhome) - set your home</li>
 * <li>{@literal /rmhome <player>} (epimanager.home.rmplayerhome) - set player's home</li>
 * <li> /home (epicmanager.home.home) - teleport to your home</li>
 * <li>{@literal /home <player> } (epicmanager.home.playerhome) -
 * 		teleport to a player's home</li>
 * <li>{@literal /setgrouphome <group> }(epimanager.home.setgrouphome) -
 * 		set a group's home </li>
 * <li>{@literal /rmgrouphome <group> }(epimanager.home.rmgrouphome) -
 * 		remove a group home</li>
 * <li> /grouphome (epicmanager.home.grouphome) - teleport to your group home</li>
 * <li>{@literal /grouphome <player/group>} (epicmanager.home.playergrouphome) -
 * 				teleport to a group home by the player or group name</li>
 * <br>
 * NOTES:
 * <li>Currently,{@literal /sethome <player>} requires the player to be online.</li>
 * <li>{@literal /rmhome <player>}, when the player is offline, may not find find the
 *    actual player if it's displayName is signifigantly different then login name.</li>
 *
 * @author _sir_maniac
 */
public class HomeFeature implements PluginFeature {
	private static final String HOME_FILE = "playerhomes.txt";
	private static final String GROUPHOME_FILE = "grouphomes.txt";

	private EpicManager plugin;

	private HomeStore homes;
	private GroupHomeStore groupHomes;
    private Set<String> deadPlayers = new TreeSet<String>();

	public void onEnable(EpicManager em) throws EpicManager.EnableError {
		plugin = em;
		Server server = em.getServer();

    	File homeFile  = new File(em.getDataFolder() + File.separator +
    			HOME_FILE);
    	File groupFile = new File(em.getDataFolder() + File.separator +
    			GROUPHOME_FILE);

    	homes = new HomeFile(homeFile, server);
    	groupHomes = new GroupHomeFile(groupFile, server);

		em.registerCommand("home", homeCommand);
		em.registerCommand("sethome", setHomeCommand);
		em.registerCommand("rmhome", rmHomeCommand);

		PluginManager pm = em.getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_TELEPORT, pListener, Priority.Highest, em);
        pm.registerEvent(Event.Type.ENTITY_DEATH, eListener, Priority.Monitor, em);
	}

	public void onDisable(EpicManager em) {

	}

	private EntityListener eListener = new EntityListener() {
    	// track player death for spawn detection
		@Override
		public void onEntityDeath(EntityDeathEvent event) {
			if(!(event.getEntity() instanceof Player))
				return;

//			System.out.println("(((((((((((((( death ))))))))");

			Player player = (Player) event.getEntity();
			deadPlayers.add(player.getName());
		}

    };


    //TODO cancel onPlayerMoved event to prevent a "player moved wrongly!" check.
	private PlayerListener pListener = new PlayerListener() {

		private boolean tried = false;

		// teleport a user to a home or grouphome
		@Override
		public void onPlayerTeleport(PlayerMoveEvent event) {
			//System.out.println("================== teleport ============");
			String playerName = event.getPlayer().getName();

			System.out.println("PlayerName: "+playerName);

			if(!deadPlayers.contains(playerName))
				return;

			// if dead, teleport to their home if they have one
			Location dest = homes.getHome(playerName);
			if(dest == null)
			{
				String group = EpicManager.permissions.getGroup(playerName);
				if(group == null) {
					System.out.println("*** Group is null");
					return;
				}

				dest = groupHomes.getGroupHome(group);
				if(dest == null)
					return;
//				System.out.println("================== Teleporting to grouphome ");
			}
//			else
//				System.out.println("================== Teleporting to home ");

//			System.out.println("====  canceled: "+event.isCancelled());
//			System.out.println(event.getFrom());
//			System.out.println(event.getTo());

			event.setCancelled(false);
//			if(!tried) {
//				event.setTo(dest);
//				tried = true;
//			}
//			else {
				event.setTo(dest);
				deadPlayers.remove(playerName);
//				tried = false;
//			}

			return;
		}
	};

	@SuppressWarnings("serial")
	private static class PermissionException extends Exception {
		public PermissionException() {
			super();
		}
	}

	@SuppressWarnings("serial")
	private static class PlayerNotFoundException extends Exception {
		public PlayerNotFoundException() {
			super();
		}
	}

	private static class PlayerInfo {
		public PlayerInfo(String name, Player other) {
			this.name = name; this.otherPlayer = other;
		}

		/** name of player */
		public String name;
		/** possibly null other person found as an argument */
		public Player otherPlayer;
	}

	private PlayerInfo getPlayerInfo(CommandSender op, String[] args,
						String selfPerm, String otherPerm)
		throws PlayerNotFoundException, PermissionException {

		if(args.length == 0) {
        	if(!EpicManager.permissions.has((Player)op, selfPerm))
        		throw new PermissionException();

        	return new PlayerInfo(((Player)op).getName(), null);
    	}
    	else {
        	if(!EpicManager.permissions.has((Player)op, otherPerm))
        		throw new SecurityException();

        	Player player = plugin.getPlayerByDisplayName(args[0]);
    		if(player == null)
    			throw new PlayerNotFoundException();
    		return new PlayerInfo(((Player)op).getName(), player);
    	}

	}

	private final CommandHandler rmHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	System.out.println("*** rmhome");

	    	if(!(op instanceof Player))
	    		return true;

	    	PlayerInfo ret;
			try {
				ret = getPlayerInfo(op, args,
						"epicmanager.home.rmhome", "epicmanager.home.rmplayerhome");
			}
			catch (PlayerNotFoundException e) {
				op.sendMessage(ChatColor.YELLOW+"Player not found on server.");
				return false;
			}
			catch (PermissionException e) { return true; }

	    	homes.clearHome(ret.name);

			String message = ChatColor.YELLOW+"Your home has been removed.";
	    	if(ret.otherPlayer == null) {
	    		op.sendMessage(message);
	    	}
	    	else {
		    	/* if both an op and player exist, notify both of success */
	    		op.sendMessage(ChatColor.YELLOW+"Home has been removed.");
	    		ret.otherPlayer.sendMessage(message);
	    	}

	    	return true;
	    }

	};

	private final CommandHandler setHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	PlayerInfo ret;
			try {
				ret = getPlayerInfo(op, args,
						"epicmanager.home.sethome", "epicmanager.home.setplayerhome");
			}
			catch (PlayerNotFoundException e) {
				op.sendMessage(ChatColor.YELLOW+"Player not found on server.");
				return false;
			}
			catch (PermissionException e) { return true; }

	    	Location dest = ((Player)op).getLocation();
	    	homes.setHome(ret.name, dest);

	    	String message = ChatColor.YELLOW+"Your home has been set.";
	    	if(ret.otherPlayer == null) {
	    		op.sendMessage(message);
	    	}
	    	else {
		    	/* if both an op and player exist, notify both of success */
	    		op.sendMessage(ChatColor.YELLOW+"Home has been set.");
	    		ret.otherPlayer.sendMessage(message);
	    	}

	    	return true;
	    }

	};

	private final CommandHandler homeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	PlayerInfo ret;
			try {
				ret = getPlayerInfo(op, args,
						"epicmanager.home.home", "epicmanager.home.playerhome");
			}
			catch (PlayerNotFoundException e) {
				// not fatal, try with the specified name
				ret = new PlayerInfo(args[0], null);
			}
			catch (PermissionException e) { return true; }

			Location location = homes.getHome(ret.name);
			if(location == null) {
				op.sendMessage(ChatColor.YELLOW+"Home not set, please use " +
						"/sethome, or contact admin.");
				return true;
			}

	    	((Player)op).teleportTo(location);
	    	return true;
	    }
	};

}


