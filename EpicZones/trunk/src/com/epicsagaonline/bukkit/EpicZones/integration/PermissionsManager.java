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

package com.epicsagaonline.bukkit.EpicZones.integration;

import java.util.ArrayList;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.plugin.Plugin;

import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicZones.EpicZones;
import com.epicsagaonline.bukkit.EpicZones.Log;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsManager
{
	private WorldsHolder GroupManager_Perms;
	private PermissionHandler Permissions_Perms;
	private EpicZones plugin;

	public PermissionsManager(EpicZones plugin)
	{
		boolean permStart = false;
		this.plugin = plugin;

		permStart = startPermissions();

		if(!permStart)
		{
			permStart = startGroupManager();
		}

		if (!permStart)
		{
			Log.Write("[NOTICE] Unable to detect a permissions system, some features will not be available.");
		}
	}

	public boolean hasPermission(Player player, String permission)
	{		

		boolean result = false;

		if(Permissions_Perms != null)
		{
			result = (Permissions_Perms.has(player, permission));
		}
		else if(GroupManager_Perms != null)
		{
			result = (GroupManager_Perms.getWorldData(player).getPermissionsHandler().has(player, permission));			
		}

		return result;

	}

	public ArrayList<String> getGroupNames(Player player)
	{
		ArrayList<String> result = new ArrayList<String>();

		if(Permissions_Perms != null )
		{	
			for(String grp: Permissions_Perms.getGroups(player.getWorld().getName(), player.getName()))
			{
				result.add(grp);	 
			}
		}
		else if(GroupManager_Perms != null)
		{
			for(org.anjocaido.groupmanager.data.Group grp: GroupManager_Perms.getWorldData(player).getGroupList())
			{
				result.add(grp.getName());
			}	
		}

		return result;
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
			GroupManager gm = (GroupManager) p;
			GroupManager_Perms = gm.getWorldsHolder();
			return GroupManager_Perms != null;
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
