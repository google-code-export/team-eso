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

package com.epicsagaonline.bukkit.EpicZones;

import org.bukkit.entity.Player;

public class HeroChatIntegration 

{

	public static void joinChat(String zoneTag, EpicZonePlayer ezp, Player player)
	{
		if(General.config.enableHeroChat)
		{
			if(EpicZones.heroChat != null)
			{
				Zone theZone = General.myZones.get(zoneTag);
				while(EpicZones.heroChat.getChannel(theZone.getTag()) == null && theZone.hasParent())
				{
					theZone = General.myZones.get(theZone.getParent().getTag());
				}
				if(!ezp.getPreviousZoneTag().equals(theZone.getTag()))
				{
					EpicZones.heroChat.getChannel(theZone.getTag()).addPlayer(player);
					EpicZones.heroChat.setActiveChannel(player, EpicZones.heroChat.getChannel(zoneTag));
				}
			}
		}
	}

	public static void leaveChat(String zoneTag, Player player)
	{
		if(General.config.enableHeroChat)
		{
			if(EpicZones.heroChat != null)
			{
				if(EpicZones.heroChat.getChannel(zoneTag) != null)
				{
					EpicZones.heroChat.getChannel(zoneTag).removePlayer(player);
				}
			}
		}
	}

}
