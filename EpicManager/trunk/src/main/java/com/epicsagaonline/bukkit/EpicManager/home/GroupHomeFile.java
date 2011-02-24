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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

//TODO: move file management part into a single base class

/**
 * A flat file with group names and their home locations
 *
 * The file is cached in a TreeMap, and only read if the modified date changes.
 *
 * format is:
 *
 *  <playerName> : <worldId> <xPos> <yPos> <xPos>
 *
 * x,y,z postions are the double value rounded to nearest integer
 * worldId is a semi-unique world identifier returned by
 * 		org.bukkit.World.getId()
 *
 * @author _sir_maniac
 *
 */
public class GroupHomeFile implements GroupHomeStore {

	private File file;
	private long fileTime;
	private Server server;
	private Map<String, Location> groupHomes = new TreeMap<String, Location>();

	public GroupHomeFile(File file, Server server) {
		this.file = file;
		this.server = server;

		if(!file.exists()) {
			try {
				file.createNewFile();
			}
			catch(IOException e) {
			}
		}

		load(true);
	}

	public Location getGroupHome(String groupName) {
		checkFile();

		return groupHomes.get(groupName);
	}

	/**
	 * remove user's home
	 *
	 * the file is rewritten in this call, this should not be a problem
	 * since it won't be used very often.
	 */
	public void clearGroupHome(String groupName) {
		groupHomes.remove(groupName);

		save();
	}


	/**
	 * set or replace a player's home.
	 *
	 * the file is rewritten in this call, this should not be a problem
	 * since setHome won't be used very often.
	 */
	public void setGroupHome(String groupName, Location location) {
		groupHomes.put(groupName,
				new Location(location.getWorld(),
							  location.getBlockX(), location.getBlockY(),
							  location.getZ()));
		save();
	}

	private void checkFile() {
		if(file.lastModified() > fileTime)
			load(false);
	}

	/**
	 * save the in-memory date to the file, ignores file errors
	 */
	private void save() {
		// make sure file hasn't been edited before saving
		checkFile();

		try {
			FileOutputStream stream = new FileOutputStream(file);
			stream.getChannel().truncate(0);
			Writer writer = new BufferedWriter(new OutputStreamWriter(stream));

			Location val;
			long worldId, x, y, z;
			String line;

			try {
				for(Map.Entry<String, Location> home : groupHomes.entrySet() ) {
					val = home.getValue();

					worldId = val.getWorld().getId();
					x = val.getBlockX();
					y = val.getBlockY();
					z = val.getBlockZ();

					/*
					 * Using Long.toString() to output a signed hex instead of
					 *  toHexString().
					 *
					 * The Long type doesn't have a trivial way to convert from
					 * an unsigned hex number.
					 */

					line = String.format("%s : %s %d %d %d\n", home.getKey(),
							Long.toString(worldId,16), x, y, z);

					writer.write(line);
				}
			}
			finally {
				writer.close();
			}
			fileTime = file.lastModified();
		}
		catch(IOException e) {
		}

	}

	/**
	 * fill the in-memory database from the file, ignores file errors
	 * @param replace if true, the in-memory database is replaced, otherwise it
	 *    is merged.
	 */
	private void load(boolean replace) {
		String line;
		String playerName;
		Location location;
		String[] parts;

		World[] worlds = server.getWorlds().toArray(new World[0]);

		fileTime = file.lastModified();

		try {
			Scanner scanner = new Scanner(file);

			// clear here with a reasonable assurance the file can be read
			if(replace) {
				groupHomes.clear();
			}

			try {
				while(scanner.hasNext()) {
					location = null;

					line = scanner.nextLine().trim();

					if(line.startsWith("#") || line.isEmpty())
						continue;

					parts = line.split(":",2);
					if(parts.length != 2)
						continue;

					playerName = parts[0].trim();

					location = makeLocation(worlds, parts[1]);

					if(location == null)
						continue;

					groupHomes.put(playerName, location);
				}
			}
			finally {
				scanner.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * parses the line (which should have 4 space separated numbers)
	 * @param worlds
	 * @param line
	 * @return
	 */
	private Location makeLocation(World[] worlds, String line) {

		Scanner scanner = new Scanner(line);
		long worldId, x, y, z;

		try {
			scanner.skip("[ \t]*");

			worldId = scanner.nextLong(16);
			x = scanner.nextLong();
			y = scanner.nextLong();
			z = scanner.nextLong();

			return makeLocation(worlds, worldId, x, y, z);
		}
		catch(InputMismatchException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 *
	 * @return newly created location, or null if world isn't in worlds
	 */
	private Location makeLocation(World[] worlds, long worldId, long x, long y, long z) {
		for(World world : worlds) {
			if(world.getId() == worldId) {
				return new Location(world, x, y, z);
			}
		}
		return null;
	}


}
