package com.bukkit.epicsaga.EpicManager.auth;

import java.io.FileNotFoundException;

import org.bukkit.Server;

import com.bukkit.epicsaga.WritablePermissionHandler;
import com.bukkit.epicsaga.EpicManager.EpicManager;
import com.bukkit.epicsaga.WritablePermissionHandler.NotFound;

/**
 * Class that verifies a player login by checking for existance in
 *  the server's Permission configuration. If the player has a boolean variable
 *  under it's info: group named banned and it is set to true, the player cannot
 *  log in even if the player exists in permissions.
 *
 * @author _sir_maniac
 *
 */
public class PermissionLoginAuthenticator implements PlayerAuthenticator {
	private final static String BANNED_VAR = "banned";
	private final static String BANNED_TIMES_VAR = "banned-times";
	private final static String BANNED_REASON_VAR = "banned-reason";
	
	private WritablePermissionHandler permission;
	private String addGroup;

	/**
	 * 
	 * @param addGroup the group to add users to, if they are added.
	 * 			(i.e. "Default" )
	 * @throws FileNotFoundException if Permissions plugin doesn't exists, or if
	 * 	it's config.yml doesn't exist
	 */
	public PermissionLoginAuthenticator(Server server, String addGroup) 
			throws FileNotFoundException {
		this.permission = new WritablePermissionHandler(server);
		this.addGroup = addGroup;
	}

	/**
	 * currently adds the player to Default, and if that doesn't exists, it 
	 * add the player to the first group known.
	 */
	public void accept(String name) {
		name = name.toLowerCase();
		
		if(!permission.hasUser(name)) {
			try {
				permission.addPlayer(name, addGroup, null);
			}
			catch (NotFound e) {
				e.printStackTrace();
				EpicManager.logSevere("PermissionLoginAuthenticator: " +
						"Error adding player to permissions file.");
				return;
			}
		}
		
		try {
			permission.setUserPermissionVariable(name, BANNED_VAR, false);
		}
		catch (NotFound e) {
			e.printStackTrace();
			EpicManager.logSevere("PermissionLoginAuthenticator: " +
					"Error adding variable: "+BANNED_VAR+" to file.");
			return;
		}
	}

	public void deny(String name) {
		this.deny(name, null);
	}

	public void deny(String name, String reason) {
		name = name.toLowerCase();
		int timesBanned = 
			permission.getUserPermissionInteger(name, BANNED_TIMES_VAR);
		if(timesBanned == -1)
			timesBanned = 0;
		
		boolean isBanned = permission.getPermissionBoolean(name, BANNED_VAR); 
		
		timesBanned++;
		isBanned = true;
		
		try {
			permission.setUserPermissionVariable(name, BANNED_TIMES_VAR, timesBanned);
			permission.setUserPermissionVariable(name, BANNED_VAR, isBanned);
			if(reason != null)
				permission.setUserPermissionVariable(name, BANNED_REASON_VAR, reason);
		}
		catch (NotFound e) {
			EpicManager.logWarning("PermissionLoginAuthenticator: " +
					"User "+name+" not found in permissions when trying to deny.");
		}
	}


	public boolean isAllowed(String name) {
		name = name.toLowerCase();
		
		if (!permission.hasUser(name))
			return false;
		
		return permission.getPermissionBoolean(name.toLowerCase(), 
				BANNED_VAR);
	}

	public String getBannedReason(String name) {
		name = name.toLowerCase();
		return permission.getUserPermissionString(name, BANNED_REASON_VAR);
	}
	

}
