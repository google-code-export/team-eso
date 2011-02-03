package com.bukkit.epicsaga.EpicManager.home;

import org.bukkit.Location;

/**
 * A persistant store of groupName/location mappings
 * @author _sir_maniac
 *
 */
public interface GroupHomeStore {

	/**
	 * Sets or changes the home location for this group.
	 *
	 * @param groupName
	 * @param location
	 */
	public void setGroupHome(String groupName, Location location);

	/**
	 *
	 * @param groupName
	 * @return the home Location for this group, or null if not set
	 */
	public Location getGroupHome(String groupName);

	/**
	 *
	 */
	public void clearGroupHome(String groupName);
}
