/*

        This file is part of SolarRedstoneTorch

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

package com.epicsagaonline.bukkit.SolarRedstoneTorch;

import org.bukkit.Location;
import org.bukkit.Material;

import com.epicsagaonline.bukkit.SolarRedstoneTorch.utility.*;

public class LightSensing implements Runnable
{

	public void run()
	{
		//System.out.println("[LightSensorSign] Sensing...");
		if (DAL.Torches != null)
		{
			//System.out.println("[LightSensorSign] " + DAL.Torches.size());
			for (Location loc : DAL.Torches.keySet())
			{
				SolarTorch trch = DAL.Torches.get(loc);
				//System.out.println("[LightSensorSign] Light Level: " + trch.BaseBlock.getLightLevel());
				if (trch.BaseBlock.getLightLevel() <= trch.Sensitivity && trch.BaseBlock.getType() == Material.REDSTONE_TORCH_OFF)
				{
					//System.out.println("[LightSensorSign] On!");
					trch.BaseBlock.setType(Material.REDSTONE_TORCH_ON);
				}
				else if (trch.BaseBlock.getLightLevel() > trch.Sensitivity && trch.BaseBlock.getType() == Material.REDSTONE_TORCH_ON)
				{
					//System.out.println("[LightSensorSign] Off!");
					trch.BaseBlock.setType(Material.REDSTONE_TORCH_OFF);
				}
			}
		}
	}
}
