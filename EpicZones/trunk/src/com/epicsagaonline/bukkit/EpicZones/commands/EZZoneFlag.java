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
import com.epicsagaonline.bukkit.EpicZones.Message;
import com.epicsagaonline.bukkit.EpicZones.Message.Message_ID;
import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneFlag
{
    public EZZoneFlag(String[] data, CommandSender sender)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            EpicZonePlayer ezp = General.getPlayer(player.getName());
            if (ezp.getAdmin()) //Owners are not allowed to edit flags on a zone.
            {
                if (ezp.getMode() == EpicZoneMode.ZoneEdit)
                {
                    if (data.length > 2 && data[1].length() > 0 && data[2].length() > 0)
                    {
                        String flag = data[1];
                        String value = "";
                        for (int i = 2; i < data.length; i++)
                        {
                            value = value + data[i] + " ";
                        }
                        if (SetFlag(flag.toLowerCase(), ezp, value))
                        {
                            Message.Send(sender, Message_ID.Info_00107_ZoneFlagUpdated_X_to_Y, new String[]{flag, value});
                        }
                        else
                        {
                            Message.Send(sender, Message_ID.Warning_00108_InvalidFlag, new String[]{flag});
                        }
                    }
                }
            }
            else
            {
                new EZZoneHelp(ZoneCommand.FLAG, sender, ezp);
            }
        }
    }

    private boolean SetFlag(String flag, EpicZonePlayer ezp, String data)
    {
        boolean result = true;
        if (flag.equals("pvp"))
        {
            SetPVP(ezp, data);
        }
        else if (flag.equals("mobs"))
        {
            SetMobs(ezp, data);
        }
        else if (flag.equals("regen"))
        {
            SetRegen(ezp, data);
        }
        else if (flag.equals("fire"))
        {
            SetFire(ezp, data);
        }
        else if (flag.equals("explode"))
        {
            SetExplode(ezp, data);
        }
        else if (flag.equals("sanctuary"))
        {
            SetSanctuary(ezp, data);
        }
        else if (flag.equals("fireburnsmobs"))
        {
            SetFireBurnsMobs(ezp, data);
        }
        else
        {
            result = false;
        }
        return result;
    }

    private void SetPVP(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setPVP(Boolean.valueOf((data).trim()));
    }

    private void SetMobs(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setMobs(data);
    }

    private void SetRegen(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setRegen(data);
    }

    private void SetFire(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setFire(data.trim());
    }

    private void SetExplode(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setExplode(data.trim());
    }

    private void SetSanctuary(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setSanctuary(Boolean.valueOf((data).trim()));
    }

    private void SetFireBurnsMobs(EpicZonePlayer ezp, String data)
    {
        ezp.getEditZone().setFireBurnsMobs(Boolean.valueOf((data).trim()));
    }
}
