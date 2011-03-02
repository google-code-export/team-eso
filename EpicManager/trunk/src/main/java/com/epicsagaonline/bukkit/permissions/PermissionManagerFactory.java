package com.epicsagaonline.bukkit.permissions;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

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
		PluginManager pm = server.getPluginManager();
		Plugin plugin = pm.getPlugin("GroupManager");
		if (plugin != null) {
			return new GroupManagerPermissionManager(server);
		}
		
		plugin = pm.getPlugin("Permissions");
		if (plugin != null) {
			return new NijikoPermissionManager(server);
		}
		
		throw new EnableError("Must have either Permissions or GroupManager");
	}
	
}
