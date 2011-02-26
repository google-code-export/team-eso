package com.epicsagaonline.bukkit.EpicGates;

import java.util.Calendar;
import java.util.Date;

public class EpicGatesPlayer {

	private Date lastCheck = new Date();
	private int loopCount = 0;
	
	public Date getLastCheck(){return lastCheck;}
	public int getLoopCount(){return loopCount;}
	
	public void setLoopCount(int value)
	{
		this.loopCount = value;
	}
	
	public void Check()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, 100);
		this.lastCheck = cal.getTime();
	}
	
	public void Looped()
	{
		loopCount++;
	}
	
	public void Teleported()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, General.config.reteleportDelay * 1000);
		this.lastCheck = cal.getTime();
	}
	
	public boolean shouldCheck()
	{
		if (this.lastCheck.before(new Date()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
