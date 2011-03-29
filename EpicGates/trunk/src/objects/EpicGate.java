/*

        This file is part of EpicGates

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

package objects;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.EpicGates.EpicGates;
import com.epicsagaonline.bukkit.EpicGates.General;

public class EpicGate {

	private String tag = "";
	private Location location = null;
	private String targetTag = "";
	private EpicGate target = null;
	private Location landing = null;
	private char direction = 'N';
	private ArrayList<String> allowed = new ArrayList<String>();
	private ArrayList<String> notAllowed = new ArrayList<String>();

	public EpicGate(String tag, Location loc, float yaw)
	{
		this.tag = tag;
		this.location = loc;
		this.direction = getDirection(yaw);
		setLanding();	
	}

	public EpicGate(String data)
	{

		String[] split = data.split(",");
		double x;
		double y;
		double z;

		this.tag = split[0].trim();

		if(IsNumeric(split[1]))
		{

			x = Double.parseDouble(split[1].trim());
			y =	Double.parseDouble(split[2].trim());
			z =	Double.parseDouble(split[3].trim());

			if(x > 0){x += 0.5;}else{x -= 0.5;}
			if(z > 0){z += 0.5;}else{z -= 0.5;}

			this.location = new Location(General.plugin.getServer().getWorlds().get(0), x, y, z);
		}
		else
		{

			x = Double.parseDouble(split[2].trim());
			y =	Double.parseDouble(split[3].trim());
			z =	Double.parseDouble(split[4].trim());

			//if(x > 0){x += 0.5;}else{x -= 0.5;}
			//if(z > 0){z += 0.5;}else{z -= 0.5;}
			x += 0.5;
			z += 0.5;
			this.location = new Location(General.plugin.getServer().getWorld(split[1].trim()), x, y, z);
		}

		this.targetTag = split[5].trim();

		if(split.length > 6)
		{
			if(!IsNumeric(split[6]))
			{
				this.direction = split[6].toUpperCase().trim().charAt(0);
			}

			if(split.length > 7)
			{
				BuildAllowed(split[7]);
			}

			if(split.length > 8)
			{
				BuildNotAllowed(split[8]);
			}
		}

		setLanding();

		System.out.println("Gate Loaded [" + this.tag + "]");// X:" + this.location.getX() + " Y:" + this.location.getY() + " Z:" + this.location.getZ());

	}

	public String getTag(){return tag;}
	public Location getLocation(){return location;}
	public String getTargetTag(){return targetTag;}
	public EpicGate getTarget(){return target;}
	public Location getLanding(){return landing;}
	public char getDirection(){return direction;}

	public boolean isAllowed(Player player)
	{

		boolean result = false;
		String playerName = player.getName();
		ArrayList<String> groupNames = EpicGates.permissions.getGroupNames(player);

		if(!notAllowed.contains(playerName) && !groupMatch(notAllowed, groupNames))
		{	
			if(allowed.size() == 0)
			{
				result = true;
			}
			else if(allowed.contains(playerName) || groupMatch(allowed, groupNames))
			{
				result = true;
			} 
		}

		return result;
	}

	public boolean groupMatch(ArrayList<String> list1, ArrayList<String> list2)
	{
		boolean result = false;
		
		for(String item2: list2)
		{
			if(list1.contains(item2))
			{
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public void setTarget(EpicGate value)
	{
		this.target = value;
		if(value != null)
		{
			this.targetTag = value.getTag();
		}
		else
		{
			this.targetTag = "";
		}
	}

	public void setLocation(Location value)
	{
		this.location = value;
		setLanding();
	}

	public void setDirection(String value)
	{

		char dir;

		if(value.equalsIgnoreCase("n") || value.equalsIgnoreCase("north"))
		{
			dir = 'N';
		}
		else if(value.equalsIgnoreCase("e") || value.equalsIgnoreCase("east"))
		{
			dir = 'E';
		}
		else if(value.equalsIgnoreCase("s") || value.equalsIgnoreCase("south"))
		{
			dir = 'S';
		}
		else if(value.equalsIgnoreCase("w") || value.equalsIgnoreCase("west"))
		{
			dir = 'W';
		}
		else
		{
			dir = 'N';
		}

		this.direction = dir;
		setLanding();
	}

	private void BuildAllowed(String data)
	{
		String[] split = data.split(" ");
		this.allowed = new ArrayList<String>();

		if(split != null && split.length > 0)
		{
			for(String member: split)
			{
				this.allowed.add(member);
			}
		}
	}

	private void BuildNotAllowed(String data)
	{
		String[] split = data.split(" ");
		this.notAllowed = new ArrayList<String>();

		if(split != null && split.length > 0)
		{
			for(String member: split)
			{
				this.notAllowed.add(member);
			}
		}
	}

	private boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
	}

	private char getDirection(float yaw)
	{
		yaw = Math.abs(yaw);
		yaw = (float) (yaw - (360 * Math.abs(Math.floor(yaw / 360))));
		if( yaw >= 45 && yaw <= 105)
		{
			return 'N';
		}
		else if( yaw >= 105 && yaw <= 195)
		{
			return 'E';
		}
		else if( yaw >= 195 && yaw <= 285)
		{
			return 'S';
		}
		else
		{
			return 'W';
		}
	}

	private void setLanding()
	{
		this.landing = this.location.clone();
		if(this.direction == 'N') //X - 1
		{
			this.landing.setX(this.landing.getX()-1);
			this.landing.setYaw(90);
		}
		if(this.direction == 'E') //Z - 1
		{
			this.landing.setZ(this.landing.getZ()-1);
			this.landing.setYaw(180);
		}
		if(this.direction == 'S') //X + 1
		{
			this.landing.setX(this.landing.getX()+1);
			this.landing.setYaw(270);
		}
		if(this.direction == 'W') //Z + 1
		{
			this.landing.setZ(this.landing.getZ()+1);
			this.landing.setYaw(0);
		}
	}

}
