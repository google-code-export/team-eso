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

package com.bukkit.epicsaga.EpicManager;

import java.io.File;

import com.bukkit.epicsaga.WritableConfiguration;

public class EMConfig extends WritableConfiguration {
	public String addGroup = null;
	public String kickMessage = null;
	public String banMessage = null;
	public String noLoginMessage = null;

	public EMConfig(File file) {
		super(file);
	}

	public void setDefaults() {
		addGroup = "Default";
		kickMessage = "Kicked by admin";
		banMessage = "You have been banned.";
		noLoginMessage = "You are not allowed on this server.";
	}

	@Override
	public void load() {
		// make sure there is always known values in case absent in config file
		setDefaults();

		super.load();

		boolean hasEmpty = false;
		String ret;

		ret = getString("messages.ban");
		if(ret != null) {
			banMessage = ret;
		}
		else 
			hasEmpty = true;

		ret = getString("messages.kick");
		if(ret != null) {
			kickMessage = ret;
		}
		else 
			hasEmpty = true;

		ret = getString("messages.nologin");
		if(ret != null) {
			noLoginMessage = ret;
		}
		else {
			// check for ond value
			ret = getString("messages.auth");
			if (ret != null) {
				noLoginMessage = ret;
				removeProperty("messages.auth");
			}
			else 
				hasEmpty = true;
		}
		
		ret = getString("auth.assign-group-on-add");
		if(ret != null) {
			addGroup = ret;
		}
		else 
			hasEmpty = true;
		
		if (hasEmpty) {
			save();
		}
	}

	/**
	 * Save settings to config file. File errors are ignored like load.
	 */
	public boolean save() {
		setProperty("messages.nologin", noLoginMessage);
		setProperty("messages.ban", banMessage);
		setProperty("messages.kick", kickMessage);
		setProperty("auth.assign-group-on-add", addGroup);
		
		return super.save();
	}
	
}
