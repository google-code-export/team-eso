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

package com.bukkit.epicsaga.EpicManager.home;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
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
 * <li>{@literal /sethome <player>} (epimanager.homeother.sethome) -
 * 		set a player's home to your location</li>
 * <li> /rmhome (epimanager.home.rmhome) - set your home</li>
 * <li>{@literal /rmhome <player>} (epimanager.homeother.rmhome) - set player's home</li>
 * <li> /home (epicmanager.home.home) - teleport to your home</li>
 * <li>{@literal /home <player> } (epicmanager.homeother.home) -
 * 		teleport to a player's home</li>
 *
 * <li>{@literal /setghome <group> }(epicmanager.ghomeother.setghome) -
 * 		set a group's home </li>
 * <li>{@literal /rmghome <group> }(epimanager.ghomeother.rmghome) -
 * 		remove a group home</li>
 * <li> /ghome (epicmanager.ghome.ghome) - teleport to your group home</li>
 * <li>{@literal /ghome <player/group>} (epicmanager.ghomeother.ghome) -
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

	private static final String PERM_BASE="epicmanager.";

	private static final String PERM_H = PERM_BASE+"home.";
	private static final String PERM_HO = PERM_BASE+"homeother.";

	private static final String PERM_GH = PERM_BASE+"ghome.";
	private static final String PERM_GHO = PERM_BASE+"ghomeother.";

	private static final String PERM_H_HOME = PERM_H+"home";
	private static final String PERM_HO_HOME = PERM_HO+"home";
	private static final String PERM_H_RMHOME = PERM_H+"rmhome";
	private static final String PERM_HO_RMHOME = PERM_HO+"rmhome";
	private static final String PERM_H_SETHOME = PERM_H+"sethome";
	private static final String PERM_HO_SETHOME = PERM_HO+"sethome";

	private static final String PERM_GH_GHOME = PERM_GH+"ghome";
	private static final String PERM_GHO_GHOME = PERM_GHO+"ghome";
	private static final String PERM_GHO_RMGHOME = PERM_GHO+"rmghome";
	private static final String PERM_GHO_SETGHOME = PERM_GHO+"segthome";


	private EpicManager plugin;

	private HomeStore homes;
	private GroupHomeStore groupHomes;
    private Set<Integer> deadPlayers = new HashSet<Integer>();
    private Set<Integer> spawningPlayers = new HashSet<Integer>();

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
		em.registerCommand("ghome", gHomeCommand);
		em.registerCommand("setghome", setGHomeCommand);
		em.registerCommand("rmghome", rmGHomeCommand);

		PluginManager pm = em.getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_TELEPORT, pListener, Priority.Highest, em);
        pm.registerEvent(Event.Type.PLAYER_MOVE, pListener, Priority.Highest, em);
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

			Player player = (Player) event.getEntity();
			deadPlayers.add(player.getEntityId());
		}

    };

	private PlayerListener pListener = new PlayerListener() {


		/*
		 * Teleport to a home or group after death.  This requires the next
		 * PLAYER_MOVE event to be canceled.
		 *
		 */
		@Override
		public void onPlayerTeleport (PlayerMoveEvent event) {

			String playerName = event.getPlayer().getName();
			int playerId = event.getPlayer().getEntityId();
			if(!deadPlayers.contains(playerId))
				return;

			// if dead, teleport to their home if they have one
			Location dest = homes.getHome(playerName);
			if(dest == null) {
				String group = EpicManager.permissions.getGroup(playerName);
				if(group == null) {
					return;
				}

				dest = groupHomes.getGroupHome(group);
				if(dest == null)
					return;
			}


			World world = event.getPlayer().getWorld();

			// attempt to preload the chunk
			int chunkx = dest.getBlockX() >> 4;
			int chunkz = dest.getBlockZ() >> 4;

			Chunk chunk = world.getChunkAt(chunkx, chunkz);
			if (!world.isChunkLoaded(chunk)) {
				world.loadChunk(chunk);
			}

			event.setTo(dest);
			deadPlayers.remove(playerId);
			spawningPlayers.add(playerId);

			return;
		}

		/*
		 *  ignore the next report from the client
		 *  (Packet 10), which seem to always be wrong after a
		 *  change in spawn location (and tiggers a "moved wrongly" cheat
		 *  response)
		 */
		@Override
		public void onPlayerMove(PlayerMoveEvent event) {

			int playerId = event.getPlayer().getEntityId();
			if(!spawningPlayers.contains(playerId))
				return;

			spawningPlayers.remove(playerId);

			event.setCancelled(true);
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

	private final CommandHandler setHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	PlayerInfo ret;
			try {
				ret = getPlayerInfo(op, args,
						PERM_H_SETHOME, PERM_HO_SETHOME);
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

	private final CommandHandler rmHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	PlayerInfo ret;
			try {
				ret = getPlayerInfo(op, args,
						PERM_H_RMHOME, PERM_HO_RMHOME);
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

	private final CommandHandler homeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	PlayerInfo ret;
			try {
				ret = getPlayerInfo(op, args,
						PERM_H_HOME, PERM_HO_HOME);
			}
			catch (PlayerNotFoundException e) {
				// not fatal, try with the specified name
				ret = new PlayerInfo(args[0], null);
			}
			catch (PermissionException e) { return true; }

			String errMsg = ChatColor.YELLOW+"Home not set, please use " +
				"/sethome, or contact admin.";

			Location location = homes.getHome(ret.name);
			if(location == null) {
				String group = EpicManager.permissions.getGroup(ret.name);
				if (group == null) {
					op.sendMessage(errMsg);
					return true;
				}

				location = groupHomes.getGroupHome(group);
				if (location == null) {
					op.sendMessage(errMsg);
					return true;
				}
			}

	    	((Player)op).teleportTo(location);
	    	return true;
	    }
	};



	private final CommandHandler setGHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	if (!EpicManager.permissions.has((Player)op, PERM_GHO_SETGHOME))
	    		return true;

	    	if (args.length != 1)
	    		return false;

	    	String group = args[0];

	    	Location dest = ((Player)op).getLocation();
	    	groupHomes.setGroupHome(group, dest);

    		op.sendMessage(ChatColor.YELLOW+"Group home has been set.");
	    	return true;
	    }

	};

	private final CommandHandler rmGHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	if (!EpicManager.permissions.has((Player)op, PERM_GHO_RMGHOME))
	    		return true;

	    	if (args.length != 1)
	    		return false;

	    	String group = args[0];

	    	groupHomes.clearGroupHome(group);

    		op.sendMessage(ChatColor.YELLOW+"Group home has been set.");
	    	return true;
	    }

	};


	private final CommandHandler gHomeCommand = new CommandHandler() {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	Location location;
	    	if (args.length == 0) {
	    		if (!EpicManager.permissions.has((Player)op, PERM_GH_GHOME))
	    			return true;

	    		String group = EpicManager.permissions.getGroup(((Player)op).getName());
	    		if (group == null) {
	    			op.sendMessage(ChatColor.YELLOW+"Group not found.");
	    			return true;
	    		}

	    		location = groupHomes.getGroupHome(group);
	    	}
	    	else {
	    		if (!EpicManager.permissions.has((Player)op, PERM_GHO_GHOME))
	    			return true;

	    		location = groupHomes.getGroupHome(args[0]);

	    		if (location == null) {
	    			Player player = plugin.getPlayerByDisplayName(args[0]);
	    			if(player == null) {
		    			op.sendMessage(ChatColor.YELLOW+"Player not found.");
		    			return true;
	    			}

	    			String group = EpicManager.permissions.getGroup(player.getName());

	    			if(group == null) {
		    			op.sendMessage(ChatColor.YELLOW+"Player's group not found.");
		    			return true;
	    			}

	    			location = groupHomes.getGroupHome(group);
	    		}

	    	}

			if(location == null) {
				op.sendMessage("Grouphome must be set with /setghome");
				return true;
			}

	    	((Player)op).teleportTo(location);
	    	return true;
	    }
	};
}


