package com.epicsagaonline.bukkit.EpicZones;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Set<String> childrenNames = new HashSet<String>();
	private boolean hasChildrenFlag = false;
	private boolean hasParentFlag = false;
	private boolean hasPVP = false;
	private boolean hasRegen = false;
	private Date lastRegen = new Date();
	private int regenAmount = 0;
	private int regenDelay = 0;
	private int regenInterval = 500;
	private int radius = 0;

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
		this.childrenNames = prime.childrenNames;
		this.hasChildrenFlag = prime.hasChildrenFlag;
		this.hasParentFlag = prime.hasParentFlag;
		this.hasPVP = prime.hasPVP;
		this.hasRegen = prime.hasRegen;
		this.lastRegen = prime.lastRegen;
		this.regenAmount = prime.regenAmount;
		this.regenDelay = prime.regenDelay;
		this.regenInterval = prime.regenInterval;
		this.radius = prime.radius;
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

			System.out.println("Created Zone [" + this.name + "]");
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
	public Set<String> getChildrenTags(){return childrenNames;}
	public boolean hasChildren(){return hasChildrenFlag;}
	public boolean hasParent(){return hasParentFlag;}
	public boolean hasRegen(){return hasRegen;}
	public int getRadius(){return radius;}
	public Point getCenter(){return center;}

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
					//ToDo
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
				this.childrenNames.add(dataList[i]);
				this.hasChildrenFlag = true;
			}
		}

	}

	private void buildPolygon(String data)
	{
		String[] dataList = data.split("\\s");
		//System.out.println("Data: " + data);
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
			//System.out.println("Center: " + this.center.x + " " + this.center.y);
			this.radius = Integer.valueOf(dataList[1]);
			//System.out.println("Radius: " + this.radius);
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
		this.boundingBox = null;
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
		//System.out.println(this.center);
		//System.out.println(x + " " + y);
		return distanceFromCenter <= this.radius;

	}
}
