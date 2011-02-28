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
		this.plugin = plugin;

		if (!startGroupManager() && !startPermissoins())
		{
			System.out.println("[" + plugin.getDescription().getName() + "] Permission system not found. Disabling plugin.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public boolean hasPermission(Player player, String permission)
	{
		return ((GroupManager_Perms != null && GroupManager_Perms.has(player, permission)) 
				|| (Permissions_Perms != null && Permissions_Perms.has(player, permission)));
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
			GroupManager _obj = (GroupManager) p;
			GroupManager_Perms = _obj.getPermissionHandler();
			return true;
		}
		return false;
	}
	
	public boolean startPermissoins()
	{
		Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
		if (p != null)
		{
			if (!p.isEnabled())
			{
				plugin.getServer().getPluginManager().enablePlugin(p);
			}
			Permissions _obj = (Permissions) p;
			Permissions_Perms = _obj.getHandler();
			return true;
		}
		return false;
	}
}
