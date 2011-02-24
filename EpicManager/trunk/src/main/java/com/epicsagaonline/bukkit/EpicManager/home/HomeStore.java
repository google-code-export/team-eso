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
package com.epicsagaonline.bukkit.EpicManager.home;

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
