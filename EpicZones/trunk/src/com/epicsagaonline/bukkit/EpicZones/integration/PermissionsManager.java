/*

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

import com.epicsagaonline.bukkit.EpicZones.Log;
import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.PrivilegedList.PrivilegeLevel;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PermissionsManager
{
    private static WorldsHolder GroupManager_Perms = null;
    private static PermissionHandler Permissions_Perms = null;
    private static PermissionManager PEX_Perms = null;
    private static Lists Lists_Perms = null;
    private static Plugin plugin = null;

    public static void Init(Plugin inPlugin)
    {

        boolean permStart;
        plugin = inPlugin;

        permStart = startPermissionsEX();
        if (permStart)
        {
            Log.Write("Using 'PermissionsEX' plugin for permission management.");
        }
        if (!permStart)
        {
            permStart = startGroupManager();
            if (permStart)
            {
                Log.Write("Using 'GroupManager' plugin for permission management.");
            }
        }
        if (!permStart)
        {
            permStart = startLists();
            if (permStart)
            {
                Log.Write("Using 'Lists' plugin for permission management.");
            }
        }
        if (!permStart)
        {
            permStart = startPermissions();
            if (permStart)
            {
                Log.Write("Using 'Permissions' plugin for permission management.");
            }
        }
        if (!permStart)
        {
            Log.Write("Using Bukkit Permissions for permission management.");
        }

    }

    public static boolean hasPermission(Player player, String permission)
    {
        boolean result = false;
        try
        {
            if (Permissions_Perms != null)
            {
                result = (Permissions_Perms.has(player, permission));
            }
            else if (PEX_Perms != null)
            {
                result = PEX_Perms.has(player, permission);
            }
            else if (GroupManager_Perms != null)
            {
                result = (GroupManager_Perms.getWorldData(player).getPermissionsHandler().has(player, permission));
            }
            else if (Lists_Perms != null)
            {
                PrivilegedList lst = Lists_Perms.getList(permission.replace("epiczones.", ""));
                if (lst != null)
                {
                    Map<String, PrivilegeLevel> users = lst.getUsers();
                    if (users != null)
                    {
                        if (users.get(player.getName().toLowerCase()) != null)
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
            else
            {
                result = player.hasPermission(permission);
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
        return result;
    }

    public static ArrayList<String> getGroupNames(Player player)
    {
        ArrayList<String> result = new ArrayList<String>();
        if (PEX_Perms != null)
        {
            for (PermissionGroup pg : PEX_Perms.getGroups())
            {
                for (PermissionUser pu : pg.getUsers())
                {
                    if (pu.getName().equalsIgnoreCase(player.getName()))
                    {
                        result.add(pg.getName());
                    }
                }
            }
        }
        else if (GroupManager_Perms != null)
        {
            Collection<Group> grps = GroupManager_Perms.getWorldData(player).getGroupList();
            if (grps != null)
            {
                for (Group grp : grps)
                {
                    result.add(grp.getName());
                }
            }
        }
        else if (Lists_Perms != null)
        {
            PrivilegedList[] lst = Lists_Perms.getLists(player.getName());
            if (lst != null)
            {
                for (PrivilegedList prv : lst)
                {
                    result.add(prv.getName());
                }
            }
        }
        else if (Permissions_Perms != null)
        {
            String[] grps = Permissions_Perms.getGroups(player.getWorld().getName(), player.getName());
            if (grps != null)
            {
                for (String grp : grps)
                {
                    result.add(0, grp);
                }
            }
        }
        else
        {
            if (player.isOp())
            {
                result.add("op");
            }
            result.add("default");
        }
        return result;
    }

    public static boolean startPermissions()
    {
        Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (p != null)
        {
            if (!p.isEnabled())
            {
                plugin.getServer().getPluginManager().enablePlugin(p);
            }
            Permissions_Perms = ((Permissions) p).getHandler();
            return Permissions_Perms != null;
        }
        return false;
    }

    public static boolean startPermissionsEX()
    {
        Plugin p = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (p != null)
        {
            if (!p.isEnabled())
            {
                plugin.getServer().getPluginManager().enablePlugin(p);
            }
            PEX_Perms = PermissionsEx.getPermissionManager();
            return PEX_Perms != null;
        }
        return false;
    }

    public static boolean startGroupManager()
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

    private static boolean startLists()
    {
        Plugin p = plugin.getServer().getPluginManager().getPlugin("Lists");
        if (p != null)
        {
            if (!p.isEnabled())
            {
                plugin.getServer().getPluginManager().enablePlugin(p);
            }
            Lists_Perms = (Lists) p;
            return true;
        }
        return false;
    }
}