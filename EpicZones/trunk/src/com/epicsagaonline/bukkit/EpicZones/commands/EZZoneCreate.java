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

package com.epicsagaonline.bukkit.EpicZones.commands;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.Log;
import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.Message.Message_ID;
import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZone;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneCreate
{
    public EZZoneCreate(String[] data, CommandSender sender)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            EpicZonePlayer ezp = General.getPlayer(player.getName());
            if (ezp.getAdmin()) // Only admins can create zones
            {
                if (ezp.getMode() == EpicZoneMode.None)
                {
                    if (data.length > 1 && data[1].length() > 0)
                    {
                        String tag = data[1].replaceAll("[^a-zA-Z0-9_]", "");
                        if (General.myZones.get(tag) == null)
                        {

                            EpicZone zone = new EpicZone();

                            zone.setTag(tag);
                            zone.setName(tag);
                            zone.setWorld(player.getWorld().getName());
                            Log.Write(player.getWorld().getName());
                            Log.Write(General.myGlobalZones.get(player.getWorld().getName().toLowerCase()).getName());
                            zone.setDefaults(General.myGlobalZones.get(player.getWorld().getName().toLowerCase()));
                            ezp.setEditZone(zone);
                            ezp.setMode(EpicZoneMode.ZoneDraw);
                            Message.Send(sender, Message_ID.Mode_00020_Draw_StartAfterNew);
                        }
                        else
                        {
                            Message.Send(sender, Message_ID.Warning_00103_Zone_X_Exists, new String[]{tag});
                        }
                    }
                    else
                    {
                        new EZZoneHelp(ZoneCommand.CREATE, sender, ezp);
                    }
                }
            }
            else
            {
                new EZZoneHelp(ZoneCommand.CREATE, sender, ezp);
            }
        }
    }
}
