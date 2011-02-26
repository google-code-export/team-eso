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

package com.epicsagaonline.bukkit.EpicManager.spawn;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import com.epicsagaonline.bukkit.EpicManager.CommandHandler;
import com.epicsagaonline.bukkit.EpicManager.EpicManager;
import com.epicsagaonline.bukkit.EpicManager.PluginFeature;

/**
 * A PluginFeature that handles a player's alternate spawn points.
 *
 *
 * Commands implemented:
 * <li> /setspawn (general.spawn.set) - set the default spawn point</li>
 * <li> /spawn (general.spawn) - teleport to the spawn point</li>
 * 
 * <li> /sethome (epimanager.home.set) - set your home</li>
 * <li> /sethome &lt; player > (epimanager.homeother.set) -
 * 		set a player's home to your location</li>
 * <li> /rmhome (epimanager.home.rm) - set your home</li>
 * <li> /rmhome &lt; player > (epimanager.homeother.rm) - set player's home</li>
 * <li> /home (epicmanager.home.home) - teleport to your home</li>
 * <li> /home &lt; player >  (epicmanager.homeother.home) -
 * 		teleport to a player's home</li>
 *
 * <li> /setghome &lt; group > (epicmanager.ghomeother.set) -
 * 		set a group's home </li>
 * <li> /rmghome &lt; group > (epimanager.ghomeother.rm) -
 * 		remove a group home</li>
 * <li> /ghome (epicmanager.ghome.ghome) - teleport to your group home</li>
 * <li> /ghome &lt; player/group > (epicmanager.ghomeother.ghome) -
 * 				teleport to a group home by the player or group name</li>
 * <br>
 * NOTES:
 * <li>Currently,{@literal /sethome <player>} requires the player to be online.</li>
 * <li>{@literal /rmhome <player>}, when the player is offline, may not find find the
 *    actual player if it's displayName is signifigantly different then login name.</li>
 * <li>doesn't detect if the spawn point is free of obstructions...</li>
 * 
 * TODO: check for obstrcutions during spawn
 *
 * @author _sir_maniac
 */
public class SpawnFeature implements PluginFeature {
	private static final String HOME_FILE = "playerhomes.txt";
	private static final String GROUPHOME_FILE = "grouphomes.txt";

	private static final String DEFAULT_SPAWN = "default_spawn";
	
	private static final String PERM_BASE="epicmanager.";
	
	private static final String PERM_SPAWN="general.spawn";
	private static final String PERM_SETSPAWN="general.setspawn";

	private static final String PERM_H = PERM_BASE+"home.";
	private static final String PERM_HO = PERM_BASE+"homeother.";

	private static final String PERM_GH = PERM_BASE+"ghome.";
	private static final String PERM_GHO = PERM_BASE+"ghomeother.";

	private static final String PERM_H_HOME = PERM_H+"home";
	private static final String PERM_HO_HOME = PERM_HO+"home";
	private static final String PERM_H_RMHOME = PERM_H+"rm";
	private static final String PERM_HO_RMHOME = PERM_HO+"rm";
	private static final String PERM_H_SETHOME = PERM_H+"set";
	private static final String PERM_HO_SETHOME = PERM_HO+"set";

	private static final String PERM_GH_GHOME = PERM_GH+"ghome";
	private static final String PERM_GHO_GHOME = PERM_GHO+"ghome";
	private static final String PERM_GHO_RMGHOME = PERM_GHO+"rm";
	private static final String PERM_GHO_SETGHOME = PERM_GHO+"set";

	private final SpawnCommand spawnCommand = new SpawnCommand();
	private final SetSpawnCommand setSpawnCommand = new SetSpawnCommand();
	
	private final SetHomeCommand setHomeCommand = new SetHomeCommand();
	private final RmHomeCommand rmHomeCommand = new RmHomeCommand();
	private final HomeCommand homeCommand = new HomeCommand();
	private final SetGHomeCommand setGHomeCommand = new SetGHomeCommand();
	private final RmGHomeCommand rmGHomeCommand = new RmGHomeCommand();
	private final GHomeCommand gHomeCommand = new GHomeCommand();

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

    	em.registerCommand("setspawn", setSpawnCommand);
    	em.registerCommand("spawn", spawnCommand);
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

	/**
	 * Verify the destination can be teleported to, if not, change
	 *    the y until safe.
	 *    
	 * This assumes the blocks at x,z don't reach the ceiling
	 *  
	 * @param dest
	 */
	private static void ensureDest(Location dest) {
		World world = dest.getWorld();

		// attempt to preload the chunk
		int chunkx = dest.getBlockX() >> 4;
		int chunkz = dest.getBlockZ() >> 4;

		Chunk chunk = world.getChunkAt(chunkx, chunkz);
		if (!world.isChunkLoaded(chunk)) {
			world.loadChunk(chunk);
		}
		
		Block b1, b2;
		
		b1 = world.getBlockAt(dest);
		b2 = b1.getRelative(0, 1, 0);
		while(b1.getType() != Material.AIR && b2.getType() != Material.AIR) {
			dest.setY(dest.getBlockY()+1);
			b1 = world.getBlockAt(dest);
			b2 = b1.getRelative(0, 1, 0);
		}
		dest.setY(dest.getBlockY()+1.00);
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

			deadPlayers.remove(playerId);
			
			// if dead, teleport to their home if they have one
			Location dest = homes.getHome(playerName);
			if (dest != null) {
				overrideDest(event, playerId, dest);
				return;
			}

			
			String group = EpicManager.permissions.getGroup(playerName);
			if(group != null) {
				dest = groupHomes.getGroupHome(group);
				if (dest != null) {
					overrideDest(event, playerId, dest);
					return;
				}
			}

			// spawn to the default spawn location if available
			dest = groupHomes.getGroupHome(DEFAULT_SPAWN);
			if (dest != null) {
				overrideDest(event, playerId, dest);
				return;
			}
		}

		private void overrideDest(PlayerMoveEvent event, int playerId, Location dest) {
			ensureDest(dest);
			
			event.setTo(dest);
			spawningPlayers.add(playerId);
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

	private class SpawnCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player) || 
	    			!EpicManager.permissions.has((Player)op, PERM_SPAWN))
	    		return true;

	    	Player player = (Player)op;
	    	
	    	Location dest = groupHomes.getGroupHome(DEFAULT_SPAWN);
			if (dest != null) {
				ensureDest(dest);
				player.teleportTo(dest);
		    	return true;
			}
			
			dest = player.getWorld().getSpawnLocation();
			
			ensureDest(dest);
	    	player.teleportTo(dest);
	    	return true;
	    }
	}

	private class SetSpawnCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player) || 
	    			!EpicManager.permissions.has((Player)op, PERM_SETSPAWN))
	    		return true;

	    	groupHomes.setGroupHome(DEFAULT_SPAWN, ((Player)op).getLocation());
	    	
	    	op.sendMessage(ChatColor.GREEN+"Default spawn point set.");
	    	return true;
	    }
	}
	
	private class SetHomeCommand implements CommandHandler {
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

	}

	private class RmHomeCommand implements CommandHandler {
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

	}

	private class HomeCommand implements CommandHandler {
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

			Location dest = homes.getHome(ret.name);
			if(dest == null) {
				String group = EpicManager.permissions.getGroup(ret.name);
				if (group == null) {
					op.sendMessage(errMsg);
					return true;
				}

				dest = groupHomes.getGroupHome(group);
				if (dest == null) {
					op.sendMessage(errMsg);
					return true;
				}
			}

			ensureDest(dest);
	    	((Player)op).teleportTo(dest);
	    	return true;
	    }
	}



	private class SetGHomeCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	if (!EpicManager.permissions.has((Player)op, PERM_GHO_SETGHOME))
	    		return true;

	    	if (args.length != 1)
	    		return false;

	    	String group = args[0];

	    	Location dest = ((Player)op).getLocation();
			ensureDest(dest);
	    	groupHomes.setGroupHome(group, dest);

    		op.sendMessage(ChatColor.YELLOW+"Group home has been set.");
	    	return true;
	    }

	}

	private class RmGHomeCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	if (!EpicManager.permissions.has((Player)op, PERM_GHO_RMGHOME))
	    		return true;

	    	if (args.length != 1)
	    		return false;

	    	String group = args[0];

	    	groupHomes.clearGroupHome(group);

    		op.sendMessage(ChatColor.YELLOW+"Group home removed.");
	    	return true;
	    }

	}


	private class GHomeCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op, String[] args) {
	    	if(!(op instanceof Player))
	    		return true;

	    	Location dest;
	    	if (args.length == 0) {
	    		if (!EpicManager.permissions.has((Player)op, PERM_GH_GHOME))
	    			return true;

	    		String group = EpicManager.permissions.getGroup(((Player)op).getName());
	    		if (group == null) {
	    			op.sendMessage(ChatColor.YELLOW+"Group not found.");
	    			return true;
	    		}

	    		dest = groupHomes.getGroupHome(group);
	    	}
	    	else {
	    		if (!EpicManager.permissions.has((Player)op, PERM_GHO_GHOME))
	    			return true;

	    		dest = groupHomes.getGroupHome(args[0]);

	    		if (dest == null) {
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

	    			dest = groupHomes.getGroupHome(group);
	    		}

	    	}

			if(dest == null) {
				op.sendMessage("Grouphome must be set with /setghome");
				return true;
			}

			ensureDest(dest);
	    	((Player)op).teleportTo(dest);
	    	return true;
	    }
	}
}


