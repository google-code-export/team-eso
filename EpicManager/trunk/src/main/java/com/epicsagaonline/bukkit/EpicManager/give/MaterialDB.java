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

package com.epicsagaonline.bukkit.EpicManager.give;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import com.epicsagaonline.bukkit.EpicManager.EpicManager;

public class MaterialDB implements Iterable<MaterialType> {
	private static final Pattern idEntryPattern = 
		Pattern.compile("^([0-9]{1,4}|[a-zA-Z0-9_]+)"+"(?:[ \t]*[,:][ \t]*" +
				"([0-2]?[0-9]{1,2}))?$");
	
	private static final Pattern numIdPattern = 
		Pattern.compile("^([0-9]{1,4})$");
	
	private Material[] actualMaterials = Material.values();
	
	private Map<String, MaterialTypeImpl> materialByName = 
		new HashMap<String, MaterialTypeImpl>(); 
	
	private SortedMap<MaterialTypeImpl, List<String>> namesByType = 
		new TreeMap<MaterialTypeImpl, List<String>>(); 
	
	private SortedMap<Integer, SortedSet<MaterialTypeImpl>> typesById = 
		new TreeMap<Integer, SortedSet<MaterialTypeImpl>>(); 

	private File file = null;
	private long fileModified = 0;

	public MaterialDB() {
		for (Material mat : actualMaterials) {
			MaterialTypeImpl type = new MaterialTypeImpl(mat, null);

			String name = mat.name().toLowerCase();
			String camelName = toCamelCase(name);

			addAlias(name, type);
			addAlias(camelName, type);
		}
		
		for (DyeColor color : DyeColor.values()) {
			MaterialTypeImpl type;
			String name;
			
			type = new MaterialTypeImpl(Material.WOOL, color.getData()); 
			name = color.name().toLowerCase()+"_wool";
			addAlias(name, type);
			addAlias(toCamelCase(name), type);
			
			// TODO: find out how to color dyes
//			type = new MaterialTypeImpl(Material.INK_SACK, color.getData()); 
//			name = color.name().toLowerCase()+"_dye";
//			addAlias(name, type);
//			addAlias(toCamelCase(name), type);
		}
	}
	
	/**
	 * returns Iterator that is sorted by material.id, data
	 */
	public Iterator<MaterialType> iterator() {
		reload();
		return new IteratorImpl(); 
	}

	/**
	 * 
	 * @param name
	 * @return true if the item by this name exists
	 */
	public boolean hasByName(String name) {
		reload();
		return materialByName.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return true if item described by this description can exist
	 */
	public boolean hasByDesc(String desc) {
		reload();
		return parseItem(desc) != null;
	}
	
	/**
	 * return the number of {@link MaterialType} values given a name, which 
	 * 	includes all types defined by @{link Material}, and any aliases within
	 *  the file.
	 * @return
	 */
	public int size() {
		reload();
		return namesByType.size();
	}
	
	public MaterialType[] toArray() {
		reload();
		return namesByType.keySet().toArray(new MaterialType[0]);
	}
	
	/**
	 * 
	 * @param name
	 * @return the MaterialDef of this name, or null if not found
	 */
	public MaterialType getByName(String name) {
		reload();
		
		return materialByName.get(name.toLowerCase());
	}
	
	/**
	 * Return MaterialType by &lt; name | id > [ : data ]
	 * @param desc
	 * @return Etiher:
	 * 			<li> MaterialType </li> 
	 * 			<li> null - description couldn't be parsed, 
	 * 			or name|id not found </li>
	 */
	public MaterialType getByDesc(String desc) {
		reload();
		
		return parseItem(desc);
	}
	
	public void setFile(File file) {
		this.file = file;
		this.fileModified = 0;
		
		reload();
	}
	
	/**
	 * re-checks on-disk file's modification date, if file has changed, reload it.
	 */
	public void reload() {
		if (file.lastModified() <= fileModified)
			return;
	
		readFile();
	}

	
	private void readFile() {
		try {
			Scanner scanner = new Scanner(file);
			
			int lineNumber = 0;
			String line;
			try{
				while (scanner.hasNextLine()) {
					line = scanner.nextLine().trim();
					lineNumber++;
			
					parseLine(line, lineNumber);
				}
			}
			finally {
				scanner.close();
			}
			
		}
		catch (FileNotFoundException e) {
			EpicManager.logWarning("Error accessing file: "+file.getPath());
		}
		
		fileModified = file.lastModified();
	}
	
	private void parseLine(String line, int lineNumber) {
		String[] parts;
		
		if (line.startsWith("#"))
			return;
		
		line = line.split("[ \t]#", 2)[0];
		
		if (!line.contains("=")) {
			EpicManager.logWarning(file.getName()+":"+lineNumber+
					"  Bad line \""+line+"\"" );
			return;
		}
		
		parts = line.split("[ \t]*=[ \t]*", 2);
		
		MaterialTypeImpl type;
		String aliases;

		type = parseItem(parts[0]);
		if (type == null) {
			EpicManager.logWarning(file.getName()+":"+lineNumber+
					" No such item: " + parts[0]);
		}
		aliases = parts[1];
		
		addAliases(aliases, type);
	}

	/**
	 * return a MaterialType 
	 * @param item either a numeric id, or a name
	 * @param data optional metadata value
	 * @return material if found, otherwise null
	 * 
	 * FIXME: this allocates new MaterialTypes even for a search
	 */
	private MaterialTypeImpl match(String item, Byte data) {
		
		Material material;
		if (numIdPattern.matcher(item).matches()) {
			int id = Integer.parseInt(item);
			
			material = Material.getMaterial(id);
			if (material == null)
				return null;

			return new MaterialTypeImpl(material, data);
		}
		else {
			MaterialTypeImpl ret = materialByName.get(item.toLowerCase());
			if (ret == null) 
				return null;
			
			// if the type is handled, we are done
			if (ret.getData() != null || data == null) {
				return ret;
			}

			return new MaterialTypeImpl(ret.getMaterial(), data);
		}
		
	}
	
	/**
	 * parse an item string if possible
	 * @return the Material, MaterialData, or null if not parsed correctly
	 *        (MaterialData can be null) 
	 */
	private MaterialTypeImpl parseItem(String desc) {
		Matcher idMatcher = idEntryPattern.matcher(desc);
		
		if (!idMatcher.matches())
			return null;
		
		String itemIdStr = idMatcher.group(1);
		String dataStr = idMatcher.group(2);

		Byte data = null;
		if(dataStr != null) {
			try {
				data = Byte.parseByte(dataStr);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}

		return match(itemIdStr, data);
	}
	
	/**
	 * 
	 * @param aliases comma separated list of aliaeses
	 * @param type
	 */
	private void addAliases(String aliases, MaterialTypeImpl type) {
		for (String alias : aliases.split("[ \t]*,[ \t]*" )) {
			addAlias(alias, type);
		}
	}

	private void addAlias(String alias, MaterialTypeImpl type) {
		if (materialByName.get(alias.toLowerCase()) != null)
			return;
		
		materialByName.put(alias.toLowerCase(), type);
		
		List<String> names = namesByType.get(type);
		if (names == null) {
			names = new ArrayList<String>();
			namesByType.put(type, names);
		}
		names.add(alias);

		SortedSet<MaterialTypeImpl> types = typesById.get(type.getMaterial().getId()); 
		if (types == null) {
			types = new TreeSet<MaterialTypeImpl>();
			typesById.put(type.getMaterial().getId(), types);
		}
		types.add(type);
	}

	private static String toCamelCase(String s) {
		String[] parts = s.split("_");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			sb.append(part.substring(0, 1).toUpperCase());
			sb.append(part.substring(1));
		}
		return sb.toString();
	}

	private class MaterialTypeImpl implements MaterialType, Comparable<MaterialType> {
		private Material material;
		private Byte data;
		
		public MaterialTypeImpl(Material material, Byte data) {
			this.material = material;
			
			if (data != null && material.getData() != null) {
				this.data = data; 
			}
			else {
				this.data = null;
			}
		}

		/**
		 * compares the equality of the material.id and the byte value of data
		 */
		@Override
		public boolean equals(Object other) {
			if (other instanceof MaterialType) {
				MaterialType otherType = (MaterialType)other;
				return this.material.getId() == otherType.getMaterial().getId() &&
					(this.data != null ? ((short)this.data)+1 : 0) ==
					(otherType.getData() != null ? ((short)otherType.getData())+1 : 0);
			}
			else
				return false;
		}
		
		@Override
		public int compareTo(MaterialType other) {
			return this.hashCode() -
				(((other.getMaterial().getId()+1) << 16) +
				(other.getData() != null ? ((short)other.getData())+1 : (short)0));
			
		}

		/**
		 * return a hashcode uniquely identifying the contents.  
		 * This is currently done with {@code ((material.id+1) << 16 +) + 
		 * 		(value of data +1, or 0 if data is null)  }
		 */
		@Override
		public int hashCode() {
			return ((material.getId()+1) << 16) + 
				(data != null ? ((short)data)+1 : (short)0);
		}

		@Override
		public String toString() {
			String result = material.toString();
			if (data != null) {
				result += ":"+data;
			}
			
			return result;
		}
		
		public String getDesc() {
			String result = Integer.toString(material.getId());
			if (data != null)
				result += ":" + Byte.toString(data);
			return result;
		}
		
		public String[] getNames() {
			return namesByType.get(this).toArray(new String[0]);
		}
		
		public Material getMaterial() {
			return material;
		}
		
		public Byte getData() {
			return data;
		}
	}	
	
	
	private class IteratorImpl implements Iterator<MaterialType> {
		Iterator<MaterialTypeImpl> i;
		
		public IteratorImpl() {
			i = namesByType.keySet().iterator(); 
		}
		
		public boolean hasNext() {
			return i.hasNext();
		}

		public MaterialType next() {
			return i.next();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}


}
