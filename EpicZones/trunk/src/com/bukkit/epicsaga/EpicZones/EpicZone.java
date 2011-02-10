package com.bukkit.epicsaga.EpicZones;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import com.bukkit.epicsaga.EpicZones.EpicZonePermission;
import com.bukkit.epicsaga.EpicZones.General;

public class EpicZone {

	private String tag = "";
	private String name = "";
	private Map<String, Boolean> flags = new  HashMap<String, Boolean>();
	private int floor = 0;
	private int ceiling = 128;
	private String world = "";
	private Polygon polygon = new Polygon();
	private Rectangle boundingBox = new Rectangle(); 
	private String enterText = "";
	private String exitText = "";
	private EpicZone parent = null;
	private Map<String, EpicZone> children = new HashMap<String, EpicZone>();
	private Set<String> childrenNames = new HashSet<String>();
	private boolean hasChildrenFlag = false;
	private boolean hasParentFlag = false;

	public EpicZone(){}

	public EpicZone(String zoneData)
	{

		String[] split = zoneData.split("\\|");

		if(split.length == 10)
		{
			this.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
			this.world = split[1];
			this.name = split[2];
			this.enterText = split[4];
			this.exitText = split[5];
			this.floor = Integer.valueOf(split[6]);
			this.ceiling = Integer.valueOf(split[7]);
			this.parent = null;
			this.children = null;
			
			buildFlags(split[3]);
			buildChildren(split[8]);
			buildPolygon(split[9]);

			this.boundingBox = this.polygon.getBounds();

			System.out.println("Created Zone [" + this.name + "]");
		}

	}

	public String getTag(){return tag;}
	public String getName(){return name;}
	public Map<String, Boolean> getFlags(){return flags;}
	public int getFloor(){return floor;}
	public int getCeiling(){return ceiling;}
	public Polygon getPolygon(){return polygon;}
	public String getEnterText(){return enterText;}
	public String getExitText(){return exitText;}
	public String getWorld(){return world;}
	public EpicZone getParent(){return parent;}
	public Map<String, EpicZone> getChildren(){return children;}
	public Set<String> getChildrenTags(){return childrenNames;}
	public boolean hasChildren(){return hasChildrenFlag;}
	public boolean hasParent(){return hasParentFlag;}

	public void addChild(EpicZone childZone)
	{
		if(this.children == null){this.children = new HashMap<String, EpicZone>();}
		this.children.put(childZone.getTag(), childZone);
	}

	public void removeChild(String tag)
	{
		if(this.children != null)
		{
			this.children.remove(tag);
		}
	}

	public void setWorld(String value)
	{
		this.world = value;
	}
	
	public void setTag(String value)
	{
		this.tag=value;
	}

	public void setName(String value)
	{
		this.name=value;
	}

	public void setFloor(int value)
	{
		this.floor=value;
	}

	public void setCeiling(int value)
	{
		this.ceiling=value;
	}

	public void setEnterText(String value)
	{
		this.enterText=value;
	}

	public void setExitText(String value)
	{
		this.exitText=value;
	}

	public boolean pointWithin(Point point)
	{
		boolean result = false;
		if(this.boundingBox.contains(point))
		{
			if(this.polygon.contains(point))
			{
				result = true;
			}
		}	
		return result;
	}

	public void setParent(EpicZone parent)
	{
		this.parent = parent;
		this.hasParentFlag = true;
	}

	private void buildFlags(String data)
	{

		if(data.length() > 0)
		{
			String[] dataList = data.split("\\s");

			for(int i = 0;i < dataList.length; i++)
			{
				String[] split = dataList[i].split(":");
				this.flags.put(split[0], split[1].equalsIgnoreCase("true"));
			}
		}

	}

	private void buildChildren(String data)
	{

		if(data.length()>0)
		{
			String[] dataList = data.split("\\s");

			for(int i = 0;i < dataList.length; i++)
			{
				this.childrenNames.add(dataList[i]);
				this.hasChildrenFlag = true;
			}
		}

	}

	private void buildPolygon(String data)
	{
		String[] dataList = data.split("\\s");
		this.polygon = new Polygon();
		for(int i = 0;i < dataList.length; i++)
		{
			String[] split = dataList[i].split(":");
			this.polygon.addPoint(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
		}
	}

	public void addPoint(Point point)
	{
		if(this.polygon == null){this.polygon = new Polygon();}
		this.polygon.addPoint(point.x, point.y);
	}

	public void clearPolyPoints()
	{
		this.polygon = new Polygon();
	}
	
	public void rebuildBoundingBox()
	{
		this.boundingBox = this.polygon.getBounds();
	}
}
