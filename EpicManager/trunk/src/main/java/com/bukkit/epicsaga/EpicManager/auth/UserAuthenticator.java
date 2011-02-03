package com.bukkit.epicsaga.EpicManager.auth;

/**
 * A class that can authenticate a user by it's name.
 *
 * @author _sir_maniac
 */
public interface UserAuthenticator {
	/**
	 * @param name
	 * @return true if name is allowed under criteria of object instance
	 */
	boolean isAllowed(String name);

	/**
	 * change authenticator to guarantee this user is allowed.
	 * @param name
	 */
	void accept(String name);

	/**
	 * change authenticator to guarantee this user is disallowed
	 * @param name
	 */
	void deny(String name);
}
