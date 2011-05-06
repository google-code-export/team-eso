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

	public boolean enableRadius;
	public boolean enableHeroChat;
	public boolean globalZoneDefaultAllow;
	public int zoneTool = 280; //Default Tool Is Stick
	public String language = "EN_US";

	public Config(File file)
	{
		super(file);

		this.file = file;

		if(file == null)
			throw new IllegalArgumentException("file cannot be null");
	}

	public void setDefaults()
	{
		enableRadius = true;
		enableHeroChat = false;
		globalZoneDefaultAllow = true;
		zoneTool = 280;
		language = "EN_US";
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

			enableRadius = getBoolean("enableRadius", true);
			enableHeroChat = getBoolean("enableHeroChat", false);
			globalZoneDefaultAllow = getBoolean("globalZoneDefaultAllow", true);
			zoneTool = getInt("zoneTool", zoneTool);
			language = getString("language", language).toUpperCase();

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

		root.put("enableRadius", enableRadius);
		root.put("zoneTool", zoneTool);
		root.put("enableHeroChat", enableHeroChat);
		root.put("globalZoneDefaultAllow", globalZoneDefaultAllow);
		root.put("language", language);

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