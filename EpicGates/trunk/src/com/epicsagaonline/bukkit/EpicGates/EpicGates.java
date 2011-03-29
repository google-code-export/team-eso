/*

        This file is part of EpicGates

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

package com.epicsagaonline.bukkit.EpicGates;

import integration.PermissionsManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import listeners.PlayerEvents;

import objects.EpicGatesWorld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


import commands.CommandHandler;
import commands.EGGate;
import commands.EGReload;
import commands.EGWorld;

/**
 * EpicZones for Bukkit
 *
 * @author jblaske
 */
public class EpicGates extends JavaPlugin 
{

	private final PlayerEvents playerListener = new PlayerEvents(this);
	private static final String CONFIG_FILE = "config.yml";
	private Map<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();
	private static final String[] GATE_COMMANDS = {"eggate", "gate"};
	private static final String[] RELOAD_COMMANDS = {"egreload", "reload"};
	private static final String[] WORLD_COMMANDS = {"egworld", "world"};
	private static CommandHandler gateCommandHandler = new EGGate();
	private static CommandHandler reloadCommandHandler = new EGReload();
	private static CommandHandler worldCommandHandler = new EGWorld();
	
	public static PermissionsManager permissions;

	public void onEnable() {

		File file = new File(this.getDataFolder() + File.separator + CONFIG_FILE);
		General.config = new Config(file);
		
		PluginDescriptionFile pdfFile = this.getDescription();

		try 
		{

			PluginManager pm = getServer().getPluginManager();
			
			pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Monitor, this);
			pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
			
			registerCommands();
			setupEpicGates();
			setupPermissions();
			
			System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled." );

		} 
		catch (Throwable e) 
		{
			System.out.println( "["+pdfFile.getName()+"]" + " error starting: "+
					e.getMessage() +" Disabling plugin" );
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() 
	{
		PluginDescriptionFile pdfFile = this.getDescription();	
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled." );
	}
	public void setupPermissions()
	{
		EpicGates.permissions = new PermissionsManager(this);
	}

	public void setupEpicGates()
	{
		General.plugin = this;
		General.myGates.clear();
		General.myGateTags.clear();
		General.myPlayers.clear();
		General.config.load();
		General.config.save();
		
		for(EpicGatesWorld egw : General.config.additionalWorlds)
		{
			this.getServer().createWorld(egw.name, egw.environment);
			System.out.println("Loaded World [" + egw.name + "]");
		}
		
		General.loadGates();
		General.saveGates();
		
		for(Player p:getServer().getOnlinePlayers())
		{
			General.addPlayer(p.getName());
		}
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
	
	private void registerCommands()
	{
		for (String cmd : GATE_COMMANDS) 
		{
			registerCommand(cmd, gateCommandHandler);
		}
		for (String cmd : RELOAD_COMMANDS) 
		{
			registerCommand(cmd, reloadCommandHandler);
		}
		for (String cmd : WORLD_COMMANDS) 
		{
			registerCommand(cmd, worldCommandHandler);
		}
	}
	
}

