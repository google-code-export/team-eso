package com.bukkit.jblaske.EpicZones;
import java.awt.Point;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class EpicZone {

	private String name = "";
	private ArrayList<SimpleEntry<String, String>> flags = new ArrayList<SimpleEntry<String, String>>();
	private Map<String,EpicZonePermission> permissions = new HashMap<String,EpicZonePermission>();
	private int floor = 0;
	private int ceiling = 0;
	private ArrayList<Point> pointList = new ArrayList<Point>();
	private String enterText = "";
	private String exitText = "";
	private EpicZone parent = null;
	private EpicZone child = null;

	public EpicZone(String zoneData)
	{

		String[] split = zoneData.split("\\|");

		if(split.length == 10)
		{
			this.name = split[0];
			this.flags = buildFlags(split[1]);
			this.enterText = split[2];
			this.exitText = split[3];
			this.permissions = buildPermissions(split[4]);
			this.floor = Integer.valueOf(split[5]);
			this.ceiling = Integer.valueOf(split[6]);
			this.parent = General.getZone(split[7]);
			this.child = General.getZone(split[8]);
			this.pointList = buildPointList(split[9]);
			System.out.println("Loaded Zone [" + this.name + "]");
		}

	}

	public String getName(){return name;}
	public ArrayList<SimpleEntry<String, String>> getFlags(){return flags;}
	public int getFloor(){return floor;}
	public int getCeiling(){return ceiling;}
	public ArrayList<Point> getPointList(){return pointList;}
	public String getEnterText(){return enterText;}
	public String getExitText(){return exitText;}
	public EpicZone getParent(){return parent;}
	public EpicZone getChild(){return child;}

	public EpicZonePermission getPermission(String name)
	{
		return permissions.get(name);
	}

	public boolean pointWithin(Point point)
	{

        int j = this.pointList.size() - 1;
        boolean result = false;

        for (int i = 0; i < this.pointList.size(); i++)
        {
            if (this.pointList.get(i).y < point.y && this.pointList.get(j).y >= point.y ||
            		this.pointList.get(j).y < point.y && this.pointList.get(i).y >= point.y)
            {
                if (this.pointList.get(i).x +
                    (point.y - this.pointList.get(i).y)/(this.pointList.get(j).y - this.pointList.get(i).y)*(this.pointList.get(j).x - this.pointList.get(i).x) < point.x)
                {
                	result = !result;
                }
            }
            j = i;
        }

        return result;

	}

	private ArrayList<SimpleEntry<String, String>> buildFlags(String data)
	{

		ArrayList<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();

		if(data.length() > 0)
		{
			String[] dataList = data.split("\\s");

			for(int i = 0;i < dataList.length; i++)
			{
				String[] split = dataList[i].split(":");
				result.add(new SimpleEntry<String, String>(split[0], split[1]));
			}
		}
		return result;
	}

	private Map<String,EpicZonePermission> buildPermissions(String data)
	{
		Map<String,EpicZonePermission> result = new HashMap<String,EpicZonePermission>();
		if(data.length()>0)
		{
			String[] dataList = data.split(",");
			EpicZonePermission permission;
			
			for(int i = 0;i < dataList.length; i++)
			{
				permission = new EpicZonePermission(dataList[i]);
			}
		}
		return result;
	}

	private ArrayList<Point> buildPointList(String data)
	{
		ArrayList<Point> result = new ArrayList<Point>();
		String[] dataList = data.split("\\s");

		for(int i = 0;i < dataList.length; i++)
		{
			String[] split = dataList[i].split(":");
			result.add(new Point(Integer.valueOf(split[0]), Integer.valueOf(split[1])));
		}
		return result;
	}

}
