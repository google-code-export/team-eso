package com.epicsagaonline.bukkit.EpicGates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.bukkit.util.config.Configuration;
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

		root.put("reteleportDelay", reteleportDelay);
		
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