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

public class EZZoneCommand
{
    public EZZoneCommand(String[] data, CommandSender sender)
    {
        EpicZonePlayer ezp;
        if (sender instanceof Player)
        {
            ezp = General.getPlayer(sender.getName());
        }
        else
        {
            ezp = General.getPlayer("console");
        }

        if (ezp.getMode() == EpicZoneMode.ZoneEdit)
        {
            if (data.length > 2)
            {
                String perm = data[1];
                String command = data[2].toLowerCase();

                if (ValidPerm(perm))
                {
                    if (perm.equals("allow"))
                    {
                        ezp.getEditZone().getDisallowedCommands().remove(command);
                        Message.Send(sender, Message_ID.Info_00131_CommandNotDenied, new String[]{command, ezp.getEditZone().getName()});
                    }
                    else
                    {
                        ezp.getEditZone().getDisallowedCommands().add(command);
                        Message.Send(sender, Message_ID.Info_00132_CommandDenied, new String[]{command, ezp.getEditZone().getName()});
                    }
                }
                else
                {
                    Message.Send(sender, Message_ID.Warning_00110_InvalidPermissionType, new String[]{perm});
                }
            }
        }
        else
        {
            new EZZoneHelp(ZoneCommand.COMMAND, sender, ezp);
        }

    }

    private static boolean ValidPerm(String perm)
    {
        return perm.equals("allow") || perm.equals("deny");
    }
}
