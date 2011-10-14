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

package com.epicsagaonline.bukkit.EpicZones.integration;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;

public class EpicSpout
{

	private static GenericLabel lblZoneName;
	private static GenericLabel lblXYZ;

	private static Spout spoutPlugin = null;

	public static void Init(Spout spout)
	{
		spoutPlugin = spout;

		lblZoneName = new GenericLabel(spoutPlugin.toString());
		lblZoneName.shiftYPos(2);
		lblZoneName.shiftXPos(-2);
		lblZoneName.setAuto(true);

		lblXYZ = new GenericLabel(spoutPlugin.toString());
		lblXYZ.shiftYPos(2);
		lblXYZ.setVisible(false);
		lblXYZ.setAuto(true);
	}

	public static void UpdatePlayerZone(EpicZonePlayer ezp, EpicZone zone)
	{
		if (General.SpoutEnabled)
		{
			SpoutPlayer sp = ezp.getSpoutPlayer();
			if (sp != null && zone != null)
			{
				if (sp.isSpoutCraftEnabled())
				{
					if (ezp.UI.getZoneLabel() == null)
					{
						ezp.UI.setZoneLabel((GenericLabel) lblZoneName.copy());
					}
					if (sp.getMainScreen().containsWidget(ezp.UI.getZoneLabel()))
					{
						ezp.UI.getZoneLabel().setText(zone.getName());
						ezp.UI.getZoneLabel().setAnchor(WidgetAnchor.TOP_RIGHT);
						ezp.UI.getZoneLabel().setAlign(WidgetAnchor.TOP_RIGHT);
						ezp.UI.getZoneLabel().setDirty(true);
					}
					else if (sp.getMainScreen().canAttachWidget(lblZoneName))
					{
						ezp.UI.getZoneLabel().setText(zone.getName());
						ezp.UI.getZoneLabel().setAnchor(WidgetAnchor.TOP_RIGHT);
						ezp.UI.getZoneLabel().setAlign(WidgetAnchor.TOP_RIGHT);
						sp.getMainScreen().attachWidget(General.plugin, ezp.UI.getZoneLabel());
					}
				}
			}
		}
	}

	public static void UpdatePlayerXYZ(Player player)
	{
		if (General.SpoutEnabled)
		{
			EpicZonePlayer ezp = General.getPlayer(player.getName());
			SpoutPlayer sp = ezp.getSpoutPlayer();
			if (sp != null)
			{
				if (sp.isSpoutCraftEnabled())
				{
					Location loc = player.getLocation();
					if (ezp.UI.getXYZLabel() == null)
					{
						ezp.UI.setXYZLabel((GenericLabel) lblXYZ.copy());
					}
					if (sp.getMainScreen().containsWidget(ezp.UI.getXYZLabel()))
					{
						ezp.UI.getXYZLabel().setText("X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
						ezp.UI.getXYZLabel().setAnchor(WidgetAnchor.TOP_CENTER);
						ezp.UI.getXYZLabel().setAlign(WidgetAnchor.TOP_CENTER);
						ezp.UI.getXYZLabel().setDirty(true);
					}
					else if (sp.getMainScreen().canAttachWidget(lblZoneName))
					{
						ezp.UI.getXYZLabel().setText("X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
						ezp.UI.getXYZLabel().setAnchor(WidgetAnchor.TOP_CENTER);
						ezp.UI.getXYZLabel().setAlign(WidgetAnchor.TOP_CENTER);
						sp.getMainScreen().attachWidget(General.plugin, ezp.UI.getXYZLabel());
					}
				}
			}
		}
	}

}
