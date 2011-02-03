package com.bukkit.epicsaga.EpicManager.misc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bukkit.epicsaga.EpicManager.CommandHandler;
import com.bukkit.epicsaga.EpicManager.EpicManager;
import com.bukkit.epicsaga.EpicManager.PluginFeature;
import com.bukkit.epicsaga.EpicManager.EpicManager.EnableError;

/**
 * Handles Miscelaneous plugin features
 *
 * Commands:
 * <li>/kick (epicmanager.kick) - kick the user from the server</li>
 *
 * @author _sir_maniac
 *
 * Notes:
 * kick feature doesn't prevent player from immediately rejoining
 *
 */
public class MiscFeature implements PluginFeature {
	private final KickCommand kickCommand = new KickCommand();

	private EpicManager plugin;


	public void onEnable(EpicManager em) throws EnableError {
		plugin = em;

		em.registerCommand("kick", kickCommand);
	}

	public void onDisable(EpicManager em) {

	}


	private class KickCommand implements CommandHandler {
	    public boolean onCommand(String command, CommandSender op,
	    		String[] playerNames) {
	    	if(op instanceof Player &&
	    	   !EpicManager.permissions.has((Player)op, "epicmanager.kick"))
	    		return true;

	    	if(playerNames.length == 0)
	    		return false;

	    	for(String playerName : playerNames) {
		    	Player player = plugin.getPlayerByDisplayName(playerName);

		    	if(player != null) {
		    		String opName;

		    		//TODO: make kick message configurable
		    		player.kickPlayer(plugin.config.kickMessage);
		    		op.sendMessage(ChatColor.YELLOW+"Player kicked from server.");

		    		if(op instanceof Player)
		    			opName = ((Player)op).getName();
		    		else
		    			opName = "console";

		    		plugin.logInfo("Player '" + player + "' kicked from server by '" +
		    				opName + "'.");
		    	}
		    	else {
		    		op.sendMessage(ChatColor.YELLOW+"Player not found.");
		    	}
	    	}
	    	return true;
	    }

	}


}
