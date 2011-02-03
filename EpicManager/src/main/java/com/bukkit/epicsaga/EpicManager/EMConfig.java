package com.bukkit.epicsaga.EpicManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class EMConfig extends Configuration {
	private static final Yaml yaml;

	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	private File file;

	public String kickMessage = null;
	public String banMessage = null;
	public String authMessage = null;


	public EMConfig(File file) {
		super(file);


		this.file = file;

		if(file == null)
			throw new IllegalArgumentException("file cannot be null");
	}

	public void setDefaults() {
		kickMessage = "Kicked by admin";
		banMessage = "You have been banned.";
		authMessage = "You are not on the whitelist.";
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
		else {
			super.load();

			String ret;

			ret = getString("messages.auth");
			if(ret != null)
				authMessage = ret;

			ret = getString("messages.ban");
			if(ret != null)
				banMessage = ret;

			ret = getString("messages.auth");
			if(ret != null)
				authMessage = ret;
		}
	}

	/**
	 * Save settings to config file. File errors are ignored like load.
	 */
	public void save() {
		FileOutputStream stream;
		BufferedWriter writer;

		Map<String, Object> messages = new HashMap<String,Object>();
		messages.put("auth", authMessage);
		messages.put("ban", banMessage);
		messages.put("kick", kickMessage);

		root.put("messages", messages);

		try {
			stream = new FileOutputStream(file);
			stream.getChannel().truncate(0);
			writer = new BufferedWriter(new OutputStreamWriter(stream));

			try{
				writer.write(yaml.dump(root));
			}
			finally {
				writer.close();
			}

		}
		catch(IOException e) {

		}
	}

}
