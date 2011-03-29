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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.CommandHandlers.CommandHandler;
import com.epicsagaonline.bukkit.EpicZones.CommandHandlers.ReloadCommandHandler;
import com.epicsagaonline.bukkit.EpicZones.CommandHandlers.WhoCommandHandler;
import com.epicsagaonline.bukkit.EpicZones.CommandHandlers.ZoneCommandHandler;
import com.herocraftonline.dthielke.herochat.HeroChat;

import org.bukkit.plugin.Plugin;

public class EpicZones extends JavaPlugin 
{

	private final LPlayer playerListener = new LPlayer(this);
	private final LBlock blockListener = new LBlock(this);
	private final LEntity entityListener = new LEntity(this);
	private final Listener_Vehicle vehicleListener = new Listener_Vehicle(this);

	private final Regen regen = new Regen(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private Map<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();

	private static final String[] ZONE_COMMANDS = {"zone"};
	private static final String[] WHO_COMMANDS = {"who", "online", "whois"};
	private static final String[] RELOAD_COMMANDS = {"reload", "reloadez"};
	private static final String CONFIG_FILE = "config.yml";
	
	private static CommandHandler reloadCommandHandler = new ReloadCommandHandler();
	private static CommandHandler zoneCommandHandler = new ZoneCommandHandler();
	private static CommandHandler whoCommandHandler = new WhoCommandHandler();

	public static HeroChat heroChat = null;
	public static PermissionsManager permissions;

	public void onEnable() 
	{

		File file = new File(this.getDataFolder() + File.separator + CONFIG_FILE);
		General.config = new Config(file);

		PluginDescriptionFile pdfFile = this.getDescription();

		try 
		{

			PluginManager pm = getServer().getPluginManager();
			
			pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Monitor, this);
			pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
			pm.registerEvent(Event.Type.PLAYER_INTERACT , this.playerListener, Event.Priority.Normal, this);
			
			pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Event.Priority.Normal, this);
			
			pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.CREATURE_SPAWN, this.entityListener, Event.Priority.Normal, this); //Causing Wierd Isues!
			pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Normal, this);
			
			pm.registerEvent(Event.Type.VEHICLE_MOVE, this.vehicleListener, Event.Priority.Normal, this);
			
			
			getServer().getScheduler().scheduleAsyncRepeatingTask(this, regen, 10, 10);

			registerCommands();
			
			setupMultiWorld();
			setupEpicZones();
			setupPermissions();
			setupHeroChat();

			Log.Write("version " + pdfFile.getVersion() + " is enabled.");

		} 
		catch (Throwable e) 
		{
			Log.Write(" error starting: " + e.getMessage() +" Disabling plugin");
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() 
	{
		PluginDescriptionFile pdfFile = this.getDescription();	
		Log.Write("version " + pdfFile.getVersion() + " is disabled.");
	}

	public void registerCommand(String command, CommandHandler handler) {
		handlers.put(command.toLowerCase(), handler);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		CommandHandler handler = handlers.get(commandLabel.toLowerCase());
		if(handler == null)
		{
			return true;
		}
		return handler.onCommand(commandLabel, sender, args);
	}


	public boolean isDebugging(final Player player) 
	{
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) 
	{
		debugees.put(player, value);
	}

	public void setupPermissions()
	{
		EpicZones.permissions = new PermissionsManager(this);
	}

	public void setupMultiWorld()
	{
		EnablePlugin("EpicGates", "Multi World");
		EnablePlugin("MultiVerse", "Multi World");
	}
	
	private void EnablePlugin(String pluginName, String pluginType)
	{
		Plugin plg;
		plg = this.getServer().getPluginManager().getPlugin(pluginName);
		if (plg != null)
		{
			if (!plg.isEnabled())
			{
				Log.Write("Detected " + pluginType + " Plugin > " + pluginName + " > Enabling...");
				this.getServer().getPluginManager().enablePlugin(plg);
			}
		}
	}
	
	public void setupHeroChat()
	{
		if(General.config.enableHeroChat)
		{
			Plugin test = this.getServer().getPluginManager().getPlugin("HeroChat");
			if (test != null)
			{
				heroChat = (HeroChat)test;
				Log.Write("HeroChat Integration Enabled.");
			}
		}
	}

	private void registerCommands()
	{
		for (String cmd : ZONE_COMMANDS) 
		{
			registerCommand(cmd, zoneCommandHandler);
		}

		for (String cmd : WHO_COMMANDS) 
		{
			registerCommand(cmd, whoCommandHandler);
		}

		for (String cmd : RELOAD_COMMANDS) 
		{
			registerCommand(cmd, reloadCommandHandler);
		}
	}

	public void setupEpicZones()
	{
		General.plugin = this;
		General.myZones.clear();
		General.myZoneTags.clear();
		General.myPlayers.clear();
		General.config.load();
		General.config.save();
		General.loadZones();
		for(Player p:getServer().getOnlinePlayers())
		{
			General.addPlayer(p.getEntityId(), p.getName());
		}
	}
}

