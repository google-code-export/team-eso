package com.bukkit.epicsaga.EpicZones;
import java.awt.Point;
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
	private ArrayList<SimpleEntry<String, String>> flags = new ArrayList<SimpleEntry<String, String>>();
	private int floor = 0;
	private int ceiling = 0;
	private ArrayList<Point> pointList = new ArrayList<Point>();
	private String enterText = "";
	private String exitText = "";
	private EpicZone parent = null;
	private Map<String, EpicZone> children = new HashMap<String, EpicZone>();
	private Set<String> childrenNames = new HashSet<String>();
	
	public EpicZone(String zoneData)
	{

		String[] split = zoneData.split("\\|");

		if(split.length == 9)
		{
			this.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
			this.name = split[1];
			this.flags = buildFlags(split[2]);
			this.enterText = split[3];
			this.exitText = split[4];
			this.floor = Integer.valueOf(split[5]);
			this.ceiling = Integer.valueOf(split[6]);
			this.parent = null;
			this.children = null;
			this.pointList = buildPointList(split[8]);
			
			buildChildren(split[7]);
			
			System.out.println("Loaded Zone [" + this.name + "]");
		}

	}

	public String getTag(){return tag;}
	public String getName(){return name;}
	public ArrayList<SimpleEntry<String, String>> getFlags(){return flags;}
	public int getFloor(){return floor;}
	public int getCeiling(){return ceiling;}
	public ArrayList<Point> getPointList(){return pointList;}
	public String getEnterText(){return enterText;}
	public String getExitText(){return exitText;}
	public EpicZone getParent(){return parent;}
	public Map<String, EpicZone> getChildren(){return children;}
	public Set<String> getChildrenNames(){return childrenNames;}

	public void addChild(EpicZone childZone)
	{
		if(this.children == null){this.children = new HashMap<String, EpicZone>();}
		this.children.put(childZone.getTag(), childZone);
	}
	
//	public EpicZonePermission getPermission(String name)
//	{
//		EpicZonePermission result;
//		//System.out.println("Getting Permissons For:" + name + " in zone: " + this.name);
//		result = permissions.get(name);
//		//if(result == null)
//		//{System.out.println("No Permissions Found");}
//		//{System.out.println(permissions.toString());}
//		return result;
//	}

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

	
	private void buildChildren(String data)
	{
		Map<String, EpicZone> result = new HashMap<String, EpicZone>();

		if(data.length() > 0)
		{
			String[] dataList = data.split("\\s");

			for(int i = 0;i < dataList.length; i++)
			{
				this.childrenNames.add(dataList[0]);
			}
		}
		
	}
	
//	private Map<String,EpicZonePermission> buildPermissions(String data)
//	{
//		Map<String,EpicZonePermission> result = new HashMap<String,EpicZonePermission>();
//		if(data.length()>0)
//		{
//			String[] dataList = data.split(",");
//			EpicZonePermission permission;
//			
//			for(int i = 0;i < dataList.length; i++)
//			{
//				permission = new EpicZonePermission(dataList[i]);
//				result.put(permission.getPermissionObject(), permission);
//			}
//		}
//		return result;
//	}

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
