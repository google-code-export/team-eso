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


package com.epicsagaonline.bukkit.EpicManager;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.epicsagaonline.bukkit.EnableError;
import com.epicsagaonline.bukkit.EpicManager.auth.AuthFeature;
import com.epicsagaonline.bukkit.EpicManager.give.GiveFeature;
import com.epicsagaonline.bukkit.EpicManager.spawn.SpawnFeature;
import com.epicsagaonline.bukkit.permissions.PermissionManager;
import com.epicsagaonline.bukkit.permissions.PermissionManagerFactory;

/**
 * Core plugin handler.
 * 
 * @author _sir_maniac
 */
public class EpicManager extends JavaPlugin {
	private static final String CONFIG_NAME = "config.yml";
	private static final String MATERIALS_NAME = "item_aliases.txt";

	private static List<PluginFeature> features = new ArrayList<PluginFeature>();

	public static final Logger log = Logger.getLogger("Minecraft");

    private PermissionManager perms = null;

    public EMConfig config;
    private File materialsFile;
    
	private static String pluginName = EpicManager.class.getName();
    private Map<String, CommandHandler> handlers =
    			new HashMap<String, CommandHandler>();

    static {
    	features.add(new AuthFeature());
    	features.add(new SpawnFeature());
    	features.add(new GiveFeature());
    }

    
    public void onEnable() {
        pluginName = this.getDescription().getName();

        File file = new File(getDataFolder()+File.separator+CONFIG_NAME);
        config = new EMConfig(file);

        materialsFile = new File(getDataFolder()+File.separator+MATERIALS_NAME);

    	try{
	    	checkConfigDir();
	    	config.load();

	    	perms = PermissionManagerFactory.getPermissionManager(getServer());
	    	
	    	for(PluginFeature feature : features) {
	    		feature.onEnable(this);
	    	}

	        PluginDescriptionFile pdfFile = this.getDescription();
	        logInfo( " version " + pdfFile.getVersion() + " is enabled." );
    	}
        catch(Throwable e) {
        	e.printStackTrace();        	
        	logSevere("Error intilizing plugin, disabling.");

        	this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
    	for(PluginFeature subPlugin : features) {
    		subPlugin.onDisable(this);
    	}
    }

    public void registerCommand(String command, CommandHandler handler) {
    	handlers.put(command.toLowerCase(), handler);
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

    	CommandHandler handler = handlers.get(commandLabel.toLowerCase());
    	if(handler == null)
    		return true;

		return handler.onCommand(commandLabel, sender, args);
	}

    public PermissionManager getPermissionManager() {
    	return perms;
    }
    
    /**
     * Reload configuration information.
     *
     */
    public void reload() {
    	config.load();
    }

    public File getMaterialsFile() {
    	return materialsFile;
    }
    
    private void checkConfigDir() throws EnableError {
    	File dir = this.getDataFolder();

    	if(!dir.isDirectory() && !dir.mkdirs()) {
    		throw new EnableError("Could not make configuration directory "+
    				dir.getPath());
    	}
    }

    /**
     * @return val with all minecraft formatting removed
     *
     */
    private String stripFormatting(String val) {
    	// remove any color codes
    	return val.replaceAll("\u00A7.", "");
    }

    /**
     *  Find closest matching player begining with searchName
     *
     *  Compares the value of getName() for every player on the server,
     *  if getName() doesn't match any player, it then tries the value of
     *  getDisplayName() stripped of formatting characters.
     *
     *  This method is necessary if the displayName(what the op sees in game)
     *   is significantly different from name.
     *
     *
     * @param searchName
     * @return the player found, otherwise null
     */
    public Player getPlayerByDisplayName(String searchName) {
    	searchName = searchName.toLowerCase();
    	searchName = stripFormatting(searchName);

    	Player[] players = getServer().getOnlinePlayers();

    	Player matchingPlayer = null;
    	String playerName;
    	int diff, maxDiff = Integer.MAX_VALUE;

    	for(Player player : players) {
    		playerName = player.getName().toLowerCase();

    		if(playerName.startsWith(searchName)) {
    			diff = playerName.length() - searchName.length();

    			if(diff == 0)
    				return player;

    			if(diff < maxDiff) {
    				maxDiff = diff;
    				matchingPlayer = player;
    			}
    		}
    	}

    	/*
    	 * Continue to try displayNames if no exact matches.
    	 * The name that matches the most of the two loops
    	 * will be returned.
    	 *
    	 * why two loops? I felt it would be faster if there wasn't two searches
    	 * for each name, unless absolutely neccessary.
    	 */
    	for(Player player : players) {
    		playerName = player.getDisplayName().toLowerCase();
    		playerName = stripFormatting(playerName);

    		if(playerName.startsWith(searchName)) {
    			diff = playerName.length() - searchName.length();

    			if(diff == 0)
    				return player;

    			if(diff < maxDiff) {
    				maxDiff = diff;
    				matchingPlayer = player;
    			}
    		}
    	}

    	return matchingPlayer;
    }

    public static void logSevere(String message) {
    	log.severe("[" + pluginName + "] " + message);
    }

    public static void logWarning(String message) {
    	log.warning("[" + pluginName + "] " + message);
    }

    public static void logInfo(String message) {
    	log.info("[" + pluginName + "] " + message);
    }

}
