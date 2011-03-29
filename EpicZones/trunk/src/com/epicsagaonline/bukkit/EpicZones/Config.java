/*

This file is part of EpicZones

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
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.epicsagaonline.bukkit.EpicZones;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Config extends Configuration {
	private static final Yaml yaml;

	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	private File file;

	public Map<String, Integer> mapRadius = new HashMap<String, Integer>();
	public boolean defaultEnter;
	public boolean defaultBuild;
	public boolean defaultDestroy;
	public boolean defaultPVP;
	public boolean enableRadius;
	public boolean enableHeroChat;
	public String permissionSystem;
	public int zoneTool = 280; //Default Tool Is Stick

	public Config(File file)
	{
		super(file);

		this.file = file;

		if(file == null)
			throw new IllegalArgumentException("file cannot be null");
	}

	public void setDefaults()
	{
		getDefaultMapRadius(1000);
		defaultEnter = true;
		defaultBuild = true;
		defaultDestroy = true;
		defaultPVP = false;
		enableRadius = true;
		enableHeroChat = false;
		zoneTool = 280;
		permissionSystem = "GroupManager";
	}

	@Override
	public void load() {
		// make sure there is always known values in case absent in config file
		setDefaults();

		if(file == null)
			throw new IllegalArgumentException("file cannot be null");

		if(!file.exists()) {
			try {
				file.createNewFile();
				save();
			}
			catch(IOException e) {

			}
		}
		else
		{
			super.load();

			setMapRadius(getString("mapRadius").trim());
			defaultEnter = getBoolean("defaultEnter", true);
			defaultBuild = getBoolean("defaultBuild", true);
			defaultDestroy = getBoolean("defaultDestroy", true);
			defaultPVP = getBoolean("defaultPVP", false);
			enableRadius = getBoolean("enableRadius", true);
			enableHeroChat = getBoolean("enableHeroChat", false);
			zoneTool = getInt("zoneTool", zoneTool);
			permissionSystem = getString("permissionSystem");
			
			if(permissionSystem == null || permissionSystem.trim().length() == 0)
			{
				permissionSystem = "GroupManager";
			}
			else
			{
				permissionSystem = permissionSystem.trim();
			}
			
		}
	}

	/**
	 * Save settings to config file. File errors are ignored like load.
	 * @return 
	 */
	public boolean save()
	{
		FileOutputStream stream;
		BufferedWriter writer;

		root.put("#The default flags are used when no zone can be found for a given event.", "");
		root.put("#Defaults are NOT applied to zones on creation.", "");
		root.put("defaultEnter", defaultEnter);
		root.put("defaultBuild", defaultBuild);
		root.put("defaultDestroy", defaultDestroy);
		root.put("defaultPVP", defaultPVP);
		root.put("#The map radius is set as a block radius from 0,0 of your world.", "");
		root.put("enableRadius", enableRadius);
		root.put("mapRadius", getMapRadius());
		root.put("#zoneTool is the tool used to draw zones with.", "");
		root.put("zoneTool", zoneTool);
		root.put("#Defines what permissions system you use, valid values are Permissions or GroupManager.", "");
		root.put("permissionSystem", permissionSystem);
		root.put("#Enables or disables HeroChat integration.", "");
		root.put("enableHeroChat", enableHeroChat);

		try 
		{
			stream = new FileOutputStream(file);
			stream.getChannel().truncate(0);
			writer = new BufferedWriter(new OutputStreamWriter(stream));

			try
			{
				writer.write(yaml.dump(root));
			}
			finally 
			{
				writer.close();
			}

		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}

	private void getDefaultMapRadius(int value)
	{
		mapRadius = new HashMap<String, Integer>();
		for(World w: General.plugin.getServer().getWorlds())
		{
			mapRadius.put(w.getName(), value);	
		}
	}
	
	private void setMapRadius(String data)
	{
		if(data.length() > 0)
		{
			if(data.contains(" "))
			{
				for(String border : data.split(" "))
				{
					if(border.contains(":"))
					{
						String[] split = border.split(":");
						mapRadius.remove(split[0]);
						mapRadius.put(split[0], Integer.parseInt(split[1]));
					}
				}
			}
			else if(data.contains(":"))
			{
				String[] split = data.split(":");
				mapRadius.remove(split[0]);
				mapRadius.put(split[0], Integer.parseInt(split[1]));
			}
			else
			{
				if(General.IsNumeric(data))
				{
					getDefaultMapRadius(Integer.parseInt(data));
				}
			}
		}
	}
	
	private String getMapRadius()
	{
		String result = "";
		for(World w: General.plugin.getServer().getWorlds())
		{
			result = result + w.getName() + ":" + mapRadius.get(w.getName()) + " ";	
		}
		return result.trim();
	}
}