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

package com.epicsagaonline.bukkit;

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
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

/**
 * A writable configuration, won't be needed soon, as upstream Configuration 
 * implements it.
 * 
 * @author _sir_maniac
 *
 */
public class WritableConfiguration extends Configuration {
	protected Yaml yaml;
	
	private File file;

	public WritableConfiguration(File file) {
		this(file, new NullRepresenter());
	}

	protected WritableConfiguration(File file, Representer rep) {
		super(file);
		this.file = file;

		// make config files look nicer
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(rep, options);

	}
	/**
	 * Saves the configuration to disk.
	 * 
	 * All errors are clobbered.
	 * 
	 * @return
	 */
	public boolean save() {
		FileOutputStream stream;
		BufferedWriter writer;

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
			return false;
		}

		return true;
	}
	
	
	/**
	 * Returns a map representing path adding new maps along the way if 
	 *  necessary.  If any node in the path exists and isn't a map, null is 
	 *  returned.
	 * 
	 * @param path path to node (dot notation)
	 * @return null or map
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getPath(String path) {
		Map<String, Object> map = root;
		Map<String, Object> ret;
		String[] parts;
		
		try {
			while(path.contains(".")) {
				parts = path.split("\\.", 2);
				
				ret = (Map<String, Object>)map.get(parts[0]);
				if (ret == null) {
					ret = new HashMap<String, Object>();
					map.put(parts[0], ret);
				}
				map = ret;
				path = parts[1];
			}

			ret = (Map<String, Object>)map.get(path);
			if(ret == null) {
				ret = new HashMap<String, Object>();
				map.put(path, ret);
			}
			return ret;
				
		}
		catch(ClassCastException e) {
			return null;
		}
	}

	/**
	 * Set property at given path.  
	 * 
	 * NOTE: Won't be necessary soon, since bukkit defines this later.
	 * 
	 * @param path path to node (dot notation)
	 * @param val the value to set
	 */
	public void setProperty(String path, Object val) {
		int index = path.lastIndexOf(".");
		if(index != -1) {
			String mapPath = path.substring(0, index);
			String key = path.substring(index+1);
			
			Map<String, Object> map = getPath(mapPath);
			map.put(key, val);
		}
		else {
			root.put(path, val);
		}
	}

	public void removeProperty(String path) {
		int index = path.lastIndexOf(".");
		if(index != -1) {
			String mapPath = path.substring(0, index);
			String key = path.substring(index+1);
			
			Map<String, Object> map = getPath(mapPath);
			map.remove(key);
		}
		else {
			root.remove(path);
		}
	}
	
	/*
	 * borrowed from:
	 * http://code.google.com/p/snakeyaml/source/browse/src/test/java/org/yaml/snakeyaml/types/NullTagTest.java
	 * 
	 */
	 protected static class NullRepresenter extends Representer {
	        public NullRepresenter() {
	            super();
	            // null representer is exceptional and it is stored as an instance
	            // variable.
	            this.nullRepresenter = new RepresentNull();
	        }

	        private class RepresentNull implements Represent {
	            public Node representData(Object data) {
	                // possible values are here http://yaml.org/type/null.html
	                return representScalar(Tag.NULL, "");
	            }
	        }
	    }

	

}
