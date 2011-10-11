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

package com.epicsagaonline.bukkit.EpicGates.objects;

import java.util.Calendar;
import java.util.Date;

import com.epicsagaonline.bukkit.EpicGates.General;

public class EpicGatesPlayer
{

	private Date lastCheck = new Date();
	private int loopCount = 0;

	public Date getLastCheck()
	{
		return lastCheck;
	}

	public int getLoopCount()
	{
		return loopCount;
	}

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
