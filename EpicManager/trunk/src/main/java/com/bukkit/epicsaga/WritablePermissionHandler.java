package com.bukkit.epicsaga;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WritablePermissionHandler extends PermissionHandler {
	private static final String FILENAME="config.yml";
	
	private Server server;
	private PermissionHandler handler;
	private WritableConfiguration source;
	
	/**
	 * 
	 * @param server
	 * @throws FileNotFoundException when PluginManager or it's config.yml isn't
	 *        found.
	 */
	public WritablePermissionHandler(Server server) 
			throws FileNotFoundException {
		this.server = server;
		
		PluginManager pm = server.getPluginManager();

		Permissions perms;		
		
		try {
			perms = (Permissions)pm.getPlugin("Permissions");
			if (perms == null) {
				throw new FileNotFoundException("Permissions plugin doesn't " +
						"exist on this server. Please make sure Permissions " +
						"exists in the plugins directory");
			}
    		// make sure Permissions gets enabled first
    		if(!perms.isEnabled())
    			server.getPluginManager().enablePlugin(perms);
		}
		catch (ClassCastException e) {
			throw new FileNotFoundException("Permisisons plugins isn't type " +
					"com.nijikokun.bukkit.Permissions.Permissions");
		}
		
		handler = perms.getHandler();
		source = new WritableConfiguration(new File(perms.getDataFolder(), 
				FILENAME));
		
		reloadSource();
	}
	
	/**
	 * Reload Permissions plugin by issuing an "/rp" chat command 
	 * 
	 * @param server
	 */
	public static void reloadPermissions(Server server) {
		Event event = new PlayerChatEvent(Type.PLAYER_COMMAND, new FakePlayer(), 
				"/rp");
		server.getPluginManager().callEvent(event);
	}
	
	private void reloadSource() {
		source.load();
	}
	
	private void saveSource() {
		source.save();
		reloadPermissions(server);
	}

	
	@SuppressWarnings("unchecked")
	public void setGroupPermissionVariable(String name, String variable, Object value) 
			throws NotFound {
		if (value instanceof Collection) {
			throw new IllegalArgumentException("Cannot use an Collections in " +
					"a permissions variable");
		}
		
		reloadSource();
		String path = "groups."+name;
		
		if (source.getProperty(path) == null)
			throw new NotFound("Cannot find group: "+name);
		
		source.setProperty(path+".info."+name, value);
		saveSource();
	}
	
	@SuppressWarnings("unchecked")
	public void setUserPermissionVariable(String name, String variable, Object value) 
		throws NotFound {
		if (value instanceof Collection) {
			throw new IllegalArgumentException("Cannot use an Collections in " +
					"permissions");
		}
		
		String val = value.toString();
		
		reloadSource();
		String path = "users."+name;
		
		if (source.getProperty(path) == null)
			throw new NotFound("Cannot find user: "+name);
		
		source.setProperty(path+".info."+variable, val);
		saveSource();
	}

	/**
	 * return true if player exists in the permisisons file 
	 * @param player
	 */
	public boolean hasUser(String name) {
		return source.getProperty(
				"users."+name.toLowerCase()) == null ? false : true;
	}
	
	/**
	 * Adds the player to the permissions database.  Refuses to overwrite an 
	 *   existing user.
	 * 
	 * @param player
	 * @param group group user belongs to, mandatory
	 * @param permissions optional list of permissions to add for user 
	 * 		  ( can be null or empty )
	 * @throws NotFound when the group doesn't exist
	 */
	public void addPlayer(String name, String group, List<String> permissions) 
			throws NotFound {
		reloadSource();
		name = name.toLowerCase();
		
		if(source.getProperty("groups."+group) == null)
			throw new NotFound("Group "+group+" not found in permissions");
		
		if(permissions != null && permissions.isEmpty())
			permissions = null;
		
		source.setProperty("users."+name+".group", group);
		source.setProperty("users."+name+".permissions", permissions);
		
		saveSource();
	}
	
	
	/* *****************************

	  Wrapped methods

	 *******************************/
	
	@Override
	public boolean canGroupBuild(String name) {
		return handler.canGroupBuild(name);
	}

	@Override
	public String getGroup(String name) {
		return handler.getGroup(name);
	}


	@Override
	public boolean getGroupPermissionBoolean(String name,
			String variable) {
		return handler.getGroupPermissionBoolean(name, variable);
	}


	@Override
	public double getGroupPermissionDouble(String name, String variable) {
		return handler.getGroupPermissionDouble(name, variable);
		
	}


	@Override
	public int getGroupPermissionInteger(String name, String variable) {
		return handler.getGroupPermissionInteger(name, variable);
	}


	@Override
	public String getGroupPermissionString(String name, String variable) {
		return handler.getGroupPermissionString(name, variable);
	}


	@Override
	public String getGroupPrefix(String name) {
		return handler.getGroupPrefix(name);
	}


	@Override
	public String getGroupSuffix(String name) {
		return handler.getGroupSuffix(name);
	}


	@Override
	public boolean getPermissionBoolean(String name, String variable) {
		return handler.getPermissionBoolean(name, variable);
	}


	@Override
	public double getPermissionDouble(String name, String variable) {
		return handler.getPermissionDouble(name, variable);
	}


	@Override
	public int getPermissionInteger(String name, String variable) {
		return handler.getPermissionInteger(name, variable);
	}


	@Override
	public String getPermissionString(String name, String variable) {
		return handler.getPermissionString(name, variable);
	}

	@Override
	public boolean getUserPermissionBoolean(String name, String variable) {
		return handler.getUserPermissionBoolean(name, variable);
	}


	@Override
	public double getUserPermissionDouble(String name, String variable) {
		return handler.getUserPermissionDouble(name, variable);
	}


	@Override
	public int getUserPermissionInteger(String name, String variable) {
		return handler.getUserPermissionInteger(name, variable);
	}


	@Override
	public String getUserPermissionString(String name, String variable) {
		return handler.getUserPermissionString(name, variable);
	}


	@Override
	public boolean has(Player player, String perm) {
		return handler.has(player, perm);
	}


	@Override
	public boolean inGroup(String name, String group) {
		return handler.inGroup(name, group);
	}


	@Override
	public void load() {
		throw new IllegalStateException("load() shouldn't be called from a " +
				"plugin");
	}


	@Override
	public boolean permission(Player paramPlayer, String paramString) {
		return handler.permission(paramPlayer, paramString);
	}


	
	/**
	 * A fake player to use when triggering the PLAYER_COMMAND event
	 * 
	 * @author _sir_maniac
	 *
	 */
	private static class FakePlayer implements Player {
		private static final String NAME="console";

		@Override
		public InetSocketAddress getAddress() {
			return null;
		}

		@Override
		public String getDisplayName() {
			return NAME;
		}

		@Override
		public boolean isOnline() {
			return true;
		}

		@Override
		public void kickPlayer(String message) {
		}

		@Override
		public boolean performCommand(String command) {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public void setCompassTarget(Location loc) {
		}

		@Override
		public void setDisplayName(String name) {
		}

		@Override
		public int getHealth() {
			return 20;
		}

		@Override
		public int getMaximumAir() {
			return 1000;
		}

		@Override
		public int getRemainingAir() {
			return 1000;
		}

		@Override
		public Vehicle getVehicle() {
			return null;
		}

		@Override
		public boolean isInsideVehicle() {
			return false;
		}

		@Override
		public boolean leaveVehicle() {
			return false;
		}

		@Override
		public void setHealth(int health) {
		}

		@Override
		public void setMaximumAir(int ticks) {
		}

		@Override
		public void setRemainingAir(int ticks) {
		}

		@Override
		public Arrow shootArrow() {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public Egg throwEgg() {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public Snowball throwSnowball() {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public boolean isOp() {
			return true;
		}

		@Override
		public boolean isPlayer() {
			return true;
		}

		@Override
		public void sendMessage(String message) {
		}

		@Override
		public PlayerInventory getInventory() {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public ItemStack getItemInHand() {
			return null;
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void setItemInHand(ItemStack item) {
		}

		@Override
		public int getEntityId() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Location getLocation() {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public World getWorld() {
			throw new IllegalStateException("not implemented in FakePlayer");
		}

		@Override
		public void teleportTo(Entity destination) {
		}

		@Override
		public void teleportTo(Location location) {
		}

	}
	
	/**
	 * thrown when a user doesn't exist during a set operation
	 * @author _sir_maniac
	 *
	 */
	@SuppressWarnings("serial")
	public static class NotFound extends Exception {

		public NotFound() {
			super();
		}

		public NotFound(String message, Throwable cause) {
			super(message, cause);
		}

		public NotFound(String message) {
			super(message);
		}

		public NotFound(Throwable cause) {
			super(cause);
		}
		
	}
	
}
