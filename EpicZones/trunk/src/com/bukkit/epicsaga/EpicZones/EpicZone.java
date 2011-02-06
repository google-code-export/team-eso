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
	private boolean hasChildrenFlag = false;
	private boolean hasParentFlag = false;

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

			System.out.println("Created Zone [" + this.name + "]");
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
	public Set<String> getChildrenTags(){return childrenNames;}
	public boolean hasChildren(){return hasChildrenFlag;}
	public boolean hasParent(){return hasParentFlag;}
	
	public void addChild(EpicZone childZone)
	{
		if(this.children == null){this.children = new HashMap<String, EpicZone>();}
		this.children.put(childZone.getTag(), childZone);
	}

	/**
	 *
	 * thrown when the the test point is definitely inside the polygon, and
	 * no side counting is neccessary.
	 *
	 * @author _sir_maniac
	 *
	 */
	@SuppressWarnings("serial")
	private class Inside extends Exception {
	};

	/**
	 * Return true if the line segment (last, cur) is to the right of test.
	 *
	 * Based on PNPOLY algorithm by W. Randolph Franklin, modified to include
	 *  most edges on trailing x and y sides.
	 *
	 *
	 * @param test
	 * @param last
	 * @param cur
	 * @param next
	 * @throws Inside when test is the same as last or cur, which is always
	 *         inside, so no more counting is needed.
	 *
	 * @see http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 * @see http://alienryderflex.com/polygon/
	 *
	 */
	private boolean sideIsRightOf(Point test, Point last, Point cur, Point next)
	throws Inside
	{
		//System.out.print("test: "+pStr(test)+" last:"+pStr(last)+" cur:"+pStr(cur)+" next:"+pStr(next));

		double xIntersect;
		// point.y is in range of current segment
		if ((cur.y > test.y) != (last.y > test.y))
		{
			xIntersect = (double)(last.x-cur.x) *
			(double)(test.y-cur.y) /
			(double)(last.y-cur.y) +
			cur.x;
		}
		// count upper points only if ajoining segments don't
		else if (((cur.y == test.y || last.y == test.y)) &&
				(cur.y >= last.y) && (cur.y >= next.y) && (last.y != next.y))
		{
			if(cur.x == test.x) // matches a point, definitely inside
			{
				throw new Inside();
			}
			xIntersect = cur.x;
		}
		else
		{
			//System.out.println("\t\t\t***nocount***");
			return false;
		}

		//System.out.print("\txIntersect:"+xIntersect);


		if(test.x < xIntersect ||
				//count right ends, only if the next segment doesn't
				(test.x == Math.ceil(xIntersect) && (next.x < cur.x)))
		{
			//System.out.println("  --count--");
			return true;
		}

		//System.out.println("  ***nocount***");
		return false;
	}

	/**
	 * Determines if a point is inside a polygon.  Guaranteed to include all
	 *   points within a square, but accuracy isn't guaranteed for more complex
	 *   shapes.
	 *
	 * Based on PNPOLY algorithm by W. Randolph Franklin, modified to include
	 *  most edges on trailing x and y sides.
	 *
	 * @param point
	 * @return true if point is on inside.
	 *
	 * @see http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 * @see http://alienryderflex.com/polygon/
	 */
	public boolean pointWithin(Point point)
	{
		boolean inside = false;

		int npoints = pointList.size();
		Point prev, cur, next;


		cur = pointList.get(npoints-1);
		next = pointList.get(0);

		try {
			for (int i = 1; i < npoints; i++)
			{
				prev = cur;
				cur = next;
				next = pointList.get(i);

				if(sideIsRightOf(point, prev, cur, next))
					inside = !inside;
			}

			prev = cur;
			cur = next;
			next = pointList.get(0);
			if(sideIsRightOf(point, prev, cur, next))
				inside = !inside;

			return inside;
		}
		catch(Inside i)
		{
			return true;
		}

	}

	public void setParent(EpicZone parent)
	{
		this.parent = parent;
		this.hasParentFlag = true;
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
