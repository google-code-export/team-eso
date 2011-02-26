package com.epicsagaonline.bukkit.permissions;

import org.bukkit.Server;

import com.epicsagaonline.bukkit.EnableError;


/*
 * Manage permissions, whether Permissions 2.0 or GroupManager
 */
public final class PermissionManagerFactory {
	
	private PermissionManagerFactory() {
	}
	
	/**
	 * a permission manager for permissions implementation, or null if not found
	 * 
	 * @param server
	 * @return
	 * @throws EnableError when suitable plugin isn't available
	 */
	public static PermissionManager getPermissionManager(Server server) 
			throws EnableError {
		try {
			return new GroupManagerPermissionManager(server);
		}
		catch (EnableError e) {
			return new NijikoPermissionManager(server);
		}
	}
	
}
