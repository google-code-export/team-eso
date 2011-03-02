/*

        This file is part of EpicGates

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

package com.epicsagaonline.bukkit.EpicGates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World.Environment;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class EpicGatesConfig extends Configuration {
	private static final Yaml yaml;

	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	private File file;

	public int reteleportDelay = 5;
	public String permissionSystem;
	public List<EpicGatesWorld> additionalWorlds = new ArrayList<EpicGatesWorld>();

	public EpicGatesConfig(File file)
	{
		super(file);

		this.file = file;

		if(file == null)
			throw new IllegalArgumentException("file cannot be null");
	}

	public void setDefaults()
	{
		reteleportDelay = 5;
		permissionSystem = "GroupManager";
	}

	@Override
	public void load() {
		// make sure there is always known values in case absent in config file
		setDefaults();

		if(file == null)
			throw new IllegalArgumentException("file cannot be null");

		//System.out.println(file.toString());

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
			reteleportDelay = getInt("reteleportDelay", reteleportDelay);	
			permissionSystem = getString("permissionSystem");

			additionalWorlds = new ArrayList<EpicGatesWorld>();
			Map<String, ConfigurationNode> nodes = getNodes("additionalWorlds");
			if(nodes != null)
			{
				for(String worldName: nodes.keySet())
				{
					EpicGatesWorld egw = new EpicGatesWorld(worldName);
					if(nodes.get(worldName).getString("environment").equalsIgnoreCase("nether"))
					{
						egw.environment = Environment.NETHER;
					}
					else
					{
						egw.environment = Environment.NORMAL;
					}
					additionalWorlds.add(egw);
				}
			}

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

	public boolean save()
	{
		FileOutputStream stream;
		BufferedWriter writer;

		root.put("reteleportDelay", reteleportDelay);
		root.put("permissionSystem", permissionSystem);

		Map<String, Object> worldMap = new HashMap<String, Object>();
		for(EpicGatesWorld egw : additionalWorlds)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("environment", egw.environment.toString());
			worldMap.put(egw.name, map);
		}

		root.put("additionalWorlds", worldMap);

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

}