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

package com.bukkit.epicsaga;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WritablePermissionHandler extends PermissionHandler {
	private static final String FILENAME="config.yml";
	
	private Permissions perms;
	private PermissionHandler handler;
	private WritableConfiguration source;
	private File sourceFile;
	private long sourceModDate;
	
	/**
	 * 
	 * @param server
	 * @throws FileNotFoundException when PluginManager or it's config.yml isn't
	 *        found.
	 */
	public WritablePermissionHandler(Server server) 
			throws FileNotFoundException {
		PluginManager pm = server.getPluginManager();
		
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
		sourceFile = new File(perms.getDataFolder(), FILENAME);
		sourceModDate = 0;
		source = new WritableConfiguration(sourceFile);
		
		refreshSource();
	}
	
	/**
	 * Reload Permissions plugin by issuing an "/rp" chat command 
	 * 
	 * @param server
	 */
	public void reload() {
		perms.getConfiguration().load();
		perms.setupPermissions();
	}
	
	private void refreshSource() {
		if (sourceFile.lastModified() > sourceModDate) {
			source.load();
			sourceModDate = sourceFile.lastModified();
		}
	}
	
	private void saveSource() {
		source.save();
		reload();
	}

	
	@SuppressWarnings("unchecked")
	public void setGroupPermissionVariable(String name, String variable, Object value) 
			throws NotFound {
		if (value instanceof Collection) {
			throw new IllegalArgumentException("Cannot use an Collections in " +
					"a permissions variable");
		}
		
		refreshSource();
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
		
		refreshSource();
		String path = "users."+name;
		
		if (source.getProperty(path) == null)
			throw new NotFound("Cannot find user: "+name);
		
		source.setProperty(path+".info."+variable, value);
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
		refreshSource();
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
