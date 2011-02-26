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

package com.epicsagaonline.bukkit.EpicManager.auth;

import com.epicsagaonline.bukkit.NotFoundError;
import com.epicsagaonline.bukkit.EpicManager.EpicManager;
import com.epicsagaonline.bukkit.permissions.PermissionManager;
import com.epicsagaonline.bukkit.permissions.VariableContainer;

/**
 * Class that verifies a player login by checking for existance in
 *  the server's Permission configuration. If the player has a boolean variable
 *  under it's info: group named banned and it is set to true, the player cannot
 *  log in even if the player exists in permissions.
 *
 * @author _sir_maniac
 *
 */
public class PermissionLoginAuthenticator implements PlayerAuthenticator {
	private static final String BANNED_VAR = "banned";
	private static final String BANNED_TIMES_VAR = "banned-times";
	private static final String BANNED_REASON_VAR = "banned-reason";

	PermissionManager manager;
	/**
	 * 
	 * @param addGroup the group to add users to, if they are added.
	 * 			(i.e. "Default" )
	 * @throws FileNotFoundException if Permissions plugin doesn't exists, or if
	 * 	it's config.yml doesn't exist
	 */
	public PermissionLoginAuthenticator(PermissionManager manager) {
		this.manager = manager;
	}

	/**
	 * currently adds the player to Default, and if that doesn't exists, it 
	 * add the player to the first group known.
	 */
	public void accept(String name) {
		name = name.toLowerCase();
		
		if(!manager.hasUser(name)) {
			manager.addUser(name, null);
		}
		
		VariableContainer vars;
		try {
			vars = manager.getUserVars(name);
		}
		catch (NotFoundError e) {
			e.printStackTrace();
			return;
		}
		
		// set banned to false if set to true
		Boolean isBanned = vars.getBoolean(BANNED_VAR);
		if(isBanned != null && isBanned) {
			vars.set(BANNED_VAR, false);
		}
	}

	public void deny(String name) {
		this.deny(name, null);
	}

	public void deny(String name, String reason) {
		name = name.toLowerCase();

		VariableContainer vars;
		try {
			vars = manager.getUserVars(name);
		}
		catch (NotFoundError e) {
			EpicManager.logWarning("PermissionLoginAuthenticator: " +
					"User "+name+" not found in permissions when trying to deny.");
			return;
		}

		Integer timesBanned = 
			vars.getInteger(BANNED_TIMES_VAR);
		if (timesBanned == null)
			timesBanned = 0;
		
		Boolean isBanned = vars.getBoolean(BANNED_VAR);
		
		timesBanned++;
		isBanned = true;
		
		vars.set(BANNED_TIMES_VAR, timesBanned);
		vars.set(BANNED_VAR, isBanned);
		vars.set(BANNED_REASON_VAR, reason);
	}


	public boolean isAllowed(String name) {
		name = name.toLowerCase();

		VariableContainer vars;
		try {
			vars = manager.getUserVars(name);
		}
		catch (NotFoundError e) {
			return false;
		}
		
		Boolean isBanned = vars.getBoolean(BANNED_VAR);
		if (isBanned == null)
			isBanned = false;
		
		return isBanned; 
	}

	public String getBannedReason(String name) {
		name = name.toLowerCase();

		VariableContainer vars;
		try {
			vars = manager.getUserVars(name);
		}
		catch (NotFoundError e) {
			return null;
		}
		
		return vars.getString(BANNED_REASON_VAR);
	}
}
