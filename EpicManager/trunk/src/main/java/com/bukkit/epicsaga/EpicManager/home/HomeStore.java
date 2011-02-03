package com.bukkit.epicsaga.EpicManager.home;

import org.bukkit.Location;

/**
 * A persistant store of playerName/location mappings
 * @author _sir_maniac
 *
 */
public interface HomeStore {

	/**
	 * Sets or changes the home location for this user.
	 *
	 * @param playerName
	 * @param location
	 */
	void setHome(String playerName, Location location);

	/**
	 *
	 * @param playerName
	 * @return the home Location for this user, or null if not set
	 */
	Location getHome(String playerName);

	/**
	 *
	 */
	void clearHome(String playerName);
}
