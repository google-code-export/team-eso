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

package com.epicsagaonline.bukkit.EpicZones.objects;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.epicsagaonline.bukkit.EpicZones.Log;

public class EpicZone {

	private String tag = "";
	private String name = "";
	//private Map<String, Boolean> flags = new  HashMap<String, Boolean>();
	private int floor = 0;
	private int ceiling = 128;
	private String world = "";
	private Polygon polygon = new Polygon();
	private Point center = new Point();
	private Rectangle boundingBox = new Rectangle(); 
	private String enterText = "";
	private String exitText = "";
	private EpicZone parent = null;
	private Map<String, EpicZone> children = new HashMap<String, EpicZone>();
	private Set<String> childrenTags = new HashSet<String>();
	private boolean hasChildrenFlag = false;
	private boolean hasParentFlag = false;
	private boolean hasPVP = false;
	private boolean hasRegen = false;
	private Date lastRegen = new Date();
	private int regenAmount = 0;
	private int regenDelay = 0;
	private int regenInterval = 500;
	private int radius = 0;
	private ArrayList<String> allowedMobs = new ArrayList<String>();
	private boolean allowFire = false;
	private boolean allowExplode = false;
	private ArrayList<String> owners = new ArrayList<String>();
	private boolean sanctuary = false;

	public EpicZone(){}

	public EpicZone(EpicZone prime)
	{
		this.tag = prime.tag;
		this.name = prime.name;
		this.floor = prime.floor;
		this.ceiling = prime.ceiling;
		this.world = prime.world;
		this.polygon = prime.polygon;
		this.center = prime.center;
		this.boundingBox = prime.boundingBox;
		this.enterText = prime.enterText;
		this.exitText = prime.exitText;
		this.parent = prime.parent;
		this.children = prime.children;
		this.childrenTags = prime.childrenTags;
		this.hasChildrenFlag = prime.hasChildrenFlag;
		this.hasParentFlag = prime.hasParentFlag;
		this.hasPVP = prime.hasPVP;
		this.hasRegen = prime.hasRegen;
		this.lastRegen = prime.lastRegen;
		this.regenAmount = prime.regenAmount;
		this.regenDelay = prime.regenDelay;
		this.regenInterval = prime.regenInterval;
		this.radius = prime.radius;
		this.allowedMobs = prime.allowedMobs;
		this.allowFire = prime.allowFire;
		this.allowExplode = prime.allowExplode;
		this.owners = prime.owners;
		this.sanctuary = prime.sanctuary;
	}

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

			rebuildBoundingBox();

			Log.Write("Created Zone [" + this.name + "]");
		}

	}

	public String getTag(){return tag;}
	public String getName(){return name;}
	//public Map<String, Boolean> getFlags(){return flags;}
	public int getFloor(){return floor;}
	public int getCeiling(){return ceiling;}
	public Polygon getPolygon(){return polygon;}
	public String getEnterText(){return enterText;}
	public String getExitText(){return exitText;}
	public String getWorld(){return world;}
	public EpicZone getParent(){return parent;}
	public Map<String, EpicZone> getChildren(){return children;}
	public Set<String> getChildrenTags(){return childrenTags;}
	public boolean hasChildren(){return hasChildrenFlag;}
	public boolean hasParent(){return hasParentFlag;}
	public boolean hasRegen(){return hasRegen;}
	public int getRadius(){return radius;}
	public Point getCenter(){return center;}
	public ArrayList<String> getAllowedMobs(){return allowedMobs;}
	public boolean getAllowFire(){return allowFire;}
	public boolean getAllowExplode(){return allowExplode;}
	public boolean isSanctuary(){return sanctuary;}
	public ArrayList<String> getOwners(){return owners;} 
	public void addChild(EpicZone childZone)
	{
		if(this.children == null){this.children = new HashMap<String, EpicZone>();}
		this.children.put(childZone.getTag(), childZone);
	}

	public void setSanctuary(Boolean value)
	{
		this.sanctuary = value;
	}

	public void setAllowFire(Boolean value)
	{
		this.allowFire = value;
	}

	public void setAllowExplode(Boolean value)
	{
		this.allowExplode = value;
	}

	public void removeChild(String tag)
	{
		if(this.childrenTags != null)
		{
			this.childrenTags.remove(tag);
		}

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

	public void setRadius(int value)
	{
		this.radius = value;
		if(this.polygon.npoints == 1)
		{
			this.center.x = this.polygon.xpoints[0];
			this.center.y = this.polygon.ypoints[0];
		}
	}

	public boolean pointWithin(Point point)
	{
		boolean result = false;
		if(this.boundingBox != null)
		{
			if(this.boundingBox.contains(point))
			{
				if(this.polygon != null)
				{
					if(this.polygon.contains(point))
					{
						result = true;
					}
				}
			}
		}
		else if(this.center != null)
		{
			if(this.pointWithinCircle(point))
			{
				result = true;
			}
		}
		return result;
	}

	public void setCenter(Point value)
	{
		this.center = value;
	}

	public void setParent(EpicZone parent)
	{
		this.parent = parent;
		this.hasParentFlag = true;
	}

	private void buildFlags(String data)
	{

		allowedMobs.add("all");

		if(data.length() > 0)
		{
			String[] dataList = data.split("\\s");

			for(int i = 0;i < dataList.length; i++)
			{
				String[] split = dataList[i].split(":");
				String flag = split[0].toLowerCase();

				if(flag.equals("pvp"))
				{
					this.hasPVP = split[1].equalsIgnoreCase("true");
				}
				else if(flag.equals("regen"))
				{
					if (split.length > 2)
					{
						if(split.length > 3)
						{
							setRegen(split[1] + " " + split[2] + " " + split[3]);
						}
						else
						{
							setRegen(split[1] + " " + split[2] + " 0");
						}
					}
					else
					{
						setRegen(split[1] + " 1");
					}
				}
				else if(flag.equals("mobs"))
				{
					BuildMobsFlag(split);
				}
				else if(flag.equals("fire"))
				{
					this.allowFire = split[1].equalsIgnoreCase("true");
				}
				else if(flag.equals("explode"))
				{
					this.allowExplode = split[1].equalsIgnoreCase("true");
				}
				else if(flag.equals("owners"))
				{
					BuildOwnersFlag(split);
				}
				else if(flag.equals("sanctuary"))
				{
					this.sanctuary = split[1].equalsIgnoreCase("true");
				}

			}
		}
	}

	public void SetMobs(String data)
	{
		if (data.length() > 0)
		{
			String[] split;
			split = (data + ":").split(":");
			if(split.length == 0)
			{
				split = (data + ",").split(",");
			}
			if(split.length == 0)
			{
				split = (data + " ").split(" ");
			}
			if(split.length > 0)
			{
				BuildMobsFlag(split);
			}
		}
	}

	private void BuildMobsFlag(String[] split)
	{
		if(split.length > 0)
		{
			allowedMobs = new ArrayList<String>();
			for(String mobType: split)
			{
				mobType = mobType.trim();
				if(mobType.equalsIgnoreCase("none"))
				{
					allowedMobs.add("none");
					return;
				}
				if(mobType.equalsIgnoreCase("animal"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSquid"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSquid");
					}

					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftChicken"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftChicken");
					}

					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftCow"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftCow");
					}

					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSheep"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSheep");
					}

					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftPig"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftPig");
					}
				}	
				else if(mobType.equalsIgnoreCase("monster"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftCreeper"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftCreeper");
					}
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftZombie"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftZombie");
					}
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftGhast"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftGhast");
					}
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftGiant"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftGiant");
					}
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSkeleton"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSkeleton");
					}
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSlime"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSlime");
					}
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSpider"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSpider");
					}
				}
				else if(mobType.equalsIgnoreCase("all"))
				{
					allowedMobs.add("all");
				}
				else if(mobType.equalsIgnoreCase("creeper"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftCreeper"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftCreeper");
					}
				}
				else if(mobType.equalsIgnoreCase("zombie"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftZombie"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftZombie");
					}
				}
				else if(mobType.equalsIgnoreCase("ghast"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftGhast"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftGhast");
					}
				}
				else if(mobType.equalsIgnoreCase("giant"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftGiant"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftGiant");
					}
				}
				else if(mobType.equalsIgnoreCase("skeleton"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSkeleton"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSkeleton");
					}
				}
				else if(mobType.equalsIgnoreCase("slime"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSlime"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSlime");
					}
				}
				else if(mobType.equalsIgnoreCase("spider"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSpider"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSpider");
					}
				}
				else if(mobType.equalsIgnoreCase("squid"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSquid"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSquid");
					}
				}
				else if(mobType.equalsIgnoreCase("chicken"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftChicken"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftChicken");
					}
				}
				else if(mobType.equalsIgnoreCase("cow"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftCow"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftCow");
					}
				}
				else if(mobType.equalsIgnoreCase("sheep"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftSheep"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftSheep");
					}
				}
				else if(mobType.equalsIgnoreCase("pig"))
				{
					if(!allowedMobs.contains("org.bukkit.craftbukkit.entity.CraftPig"))
					{
						allowedMobs.add("org.bukkit.craftbukkit.entity.CraftPig");
					}
				}
			}
		}
		else
		{
			allowedMobs.add("all");	
		}
	}

	public void BuildOwnersFlag(String[] split)
	{

		boolean skip = true;
		this.owners = new ArrayList<String>();

		if(split.length > 0)
		{
			this.owners = new ArrayList<String>();
			for(String owner: split)
			{
				if(!skip)
				{
					owners.add(owner.trim());
				}
				else
				{
					skip = false;
				}
			}
		}
	}

	public void setPVP(boolean value)
	{
		this.hasPVP = value;
	}

	public void setRegen(String value)
	{

		String[] split = value.split("\\s");
		int interval = 0;
		int amount = 0;
		int delay = 0;

		if(split.length > 1)
		{
			interval = Integer.valueOf(split[0]);
			amount = Integer.valueOf(split[1]);
			if(split.length > 2)
			{
				delay = Integer.valueOf(split[2]);
			}
		}

		if(amount != 0)
		{ 
			this.hasRegen = true;
			this.regenInterval = interval;
			this.regenAmount = amount;
			this.regenDelay = delay;
		}
		else
		{
			this.hasRegen = false;
			this.regenInterval = 0;
			this.regenDelay = 0;
			this.regenAmount = 0;
		}
	}

	private void buildChildren(String data)
	{

		if(data.length()>0)
		{
			String[] dataList = data.split("\\s");

			for(int i = 0;i < dataList.length; i++)
			{
				this.childrenTags.add(dataList[i]);
				this.hasChildrenFlag = true;
			}
		}

	}

	private void buildPolygon(String data)
	{
		String[] dataList = data.split("\\s");

		if (dataList.length > 2)
		{
			this.polygon = new Polygon();
			this.center = null;
			for(int i = 0;i < dataList.length; i++)
			{
				String[] split = dataList[i].split(":");
				this.polygon.addPoint(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
			}
		}
		else if(dataList.length >= 1)
		{
			String[] split = dataList[0].split(":");
			this.polygon = null;
			this.center = new Point(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
			this.radius = Integer.valueOf(dataList[1]);
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
		if(this.polygon != null)
		{
			this.boundingBox = this.polygon.getBounds();
		}
		else	
		{
			this.boundingBox = null;
		}
	}

	public boolean hasPVP() 
	{
		return this.hasPVP;
	}

	public int getRegenAmount() 
	{
		return regenAmount;
	}

	public int getRegenDelay() 
	{
		return regenDelay;
	}

	public int getRegenInterval() 
	{
		return regenInterval;
	}

	public void Regen()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, this.regenInterval);
		this.lastRegen = cal.getTime();
	}

	public Date getAdjustedRegenDelay()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, -this.regenDelay);
		return cal.getTime();
	}

	public boolean timeToRegen()
	{
		if (this.hasRegen)
		{
			if(this.lastRegen.before(new Date()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean pointWithinCircle(Point test)
	{
		double x = test.x - this.center.x;
		double y = test.y - this.center.y;
		double xsquared = x * x;
		double ysquared = y * y;
		double distanceFromCenter = Math.sqrt(xsquared + ysquared);
		return distanceFromCenter <= this.radius;

	}

	public boolean isOwner(String playerName)
	{
		return owners.contains(playerName);
	}

	public void addOwner(String playerName)
	{
		owners.add(playerName);
	}

	public void removeOwner(String playerName)
	{
		owners.remove(playerName);
	}

}
