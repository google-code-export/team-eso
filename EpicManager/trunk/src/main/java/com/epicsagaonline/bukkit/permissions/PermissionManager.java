package com.epicsagaonline.bukkit.permissions;

import java.util.List;

import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.NotFoundError;


/**
 * simple permission interface
 * 
 * @author _sir_maniac
 *
 */
public interface PermissionManager {
	/**
	 * returns true if the user has this permission available
	 * @param player
	 * @param perm
	 * @return
	 */
	boolean has(Player player, String perm);
	
	/**
	 * return true if user exists in permissions database
	 * @param userName
	 * @return
	 */
	boolean hasUser(String userName);
	
	/**
	 * Adds the player to the permissions database.  Refuses to overwrite an 
	 *   existing user.
	 * 
	 * @param player
	 * @param group group user belongs to, mandatory
	 * @param permissions optional list of permissions to add for user 
	 * 		  ( can be null or empty )
	 */
	void addUser(String user, List<String> permissions);
	
	/**
	 * return the group name this player belongs to
	 * @param playerName
	 * @return non-null and non-empty string
	 * @throws NotFoundError when player doesn't exist
	 */
	String getGroup(String playerName) throws NotFoundError;

	/**
	 * return a variable container for a given user 
	 * @throws NotFoundError if user doesn't exist
	 */
	VariableContainer getUserVars(String user) throws NotFoundError;

	/**
	 * return a variable container for a given group 
	 * @throws NotFoundError if group doesn't exist
	 */
	VariableContainer getGroupVars(String group) throws NotFoundError;
}
