/*

This file is part of EpicZones

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
* @author jblaske@gmail.com
* @license MIT License
*/

package com.epicsagaonline.bukkit.EpicZones;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.plugin.Plugin;

import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * PermissionsManager for EpicZones for Bukkit
 *
 * Original Author: phaed
 * Modified By: jblaske
 */
public class PermissionsManager
{
	private PermissionHandler GroupManager_Perms;
	private PermissionHandler Permissions_Perms;
	private EpicZones plugin;

	public PermissionsManager(EpicZones plugin)
	{
		boolean permStart = false;
		this.plugin = plugin;
		
		if(General.config.permissionSystem.equalsIgnoreCase("GroupManager"))
		{
			permStart = startGroupManager();	
		}
		else if (General.config.permissionSystem.equalsIgnoreCase("Permissions"))
		{
			permStart = startPermissions();
		}
		
		if (!permStart)
		{
			System.out.println("[" + plugin.getDescription().getName() + "] Permission system [" + General.config.permissionSystem + "] not found. Disabling plugin.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public boolean hasPermission(Player player, String permission)
	{		
		if(General.config.permissionSystem.equalsIgnoreCase("GroupManager"))
		{
			return (GroupManager_Perms != null && GroupManager_Perms.has(player, permission));	
		}
		else if (General.config.permissionSystem.equalsIgnoreCase("Permissions"))
		{
			return (Permissions_Perms != null && Permissions_Perms.has(player, permission));
		}
		return false;
	}

	public boolean startGroupManager()
	{
		Plugin p = plugin.getServer().getPluginManager().getPlugin("GroupManager");
		if (p != null)
		{
			if (!p.isEnabled())
			{
				plugin.getServer().getPluginManager().enablePlugin(p);
			}
			GroupManager_Perms = ((GroupManager) p).getPermissionHandler();
			return true;
		}
		return false;
	}
	
	public boolean startPermissions()
	{
		Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
		if (p != null)
		{
			if (!p.isEnabled())
			{
				plugin.getServer().getPluginManager().enablePlugin(p);
			}
			Permissions_Perms = ((Permissions)p).getHandler();
			return true;
		}
		return false;
	}
}
