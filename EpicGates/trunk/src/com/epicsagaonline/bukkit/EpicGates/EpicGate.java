package com.epicsagaonline.bukkit.EpicGates;

import org.bukkit.Location;

public class EpicGate {

	private String tag = "";
	private Location location = null;
	private String targetTag = "";
	private EpicGate target = null;
	private Location landing = null;
	private char direction = 'N';

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

			if(x > 0){x += 0.5;}else{x -= 0.5;}
			if(z > 0){z += 0.5;}else{z -= 0.5;}

			this.location = new Location(General.plugin.getServer().getWorld(split[1].trim()), x, y, z);
		}

		this.targetTag=split[5].trim();

		if(split.length > 6)
		{
			if(!IsNumeric(split[6]))
			{
				this.direction = split[6].toUpperCase().trim().charAt(0);
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

	public void setTarget(EpicGate value)
	{
		this.target = value;
	}

	private boolean IsNumeric(String data)
	{
		if (data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){return true;}
		else {return false;} 
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
