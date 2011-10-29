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

import com.epicsagaonline.bukkit.EpicZones.commands.EZZoneHelp.ZoneCommand;
import org.bukkit.command.CommandSender;

public class EZZone implements CommandHandler
{
    public boolean onCommand(String command, CommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
            String subCommand = args[0].toLowerCase().trim();
            if (subCommand.equals("create"))
            {
                new EZZoneCreate(args, sender);
            }
            else if (subCommand.equals("save"))
            {
                new EZZoneSave(sender);
            }
            else if (subCommand.equals("flag"))
            {
                new EZZoneFlag(args, sender);
            }
            else if (subCommand.equals("radius"))
            {
                new EZZoneRadius(args, sender);
            }
            else if (subCommand.equals("floor"))
            {
                new EZZoneFloor(args, sender);
            }
            else if (subCommand.equals("ceiling"))
            {
                new EZZoneCeiling(args, sender);
            }
            else if (subCommand.equals("child"))
            {
                new EZZoneChild(args, sender);
            }
            else if (subCommand.equals("parent"))
            {
                new EZZoneParent(args, sender);
            }
            else if (subCommand.equals("owner"))
            {
                new EZZoneOwner(args, sender);
            }
            else if (subCommand.equals("name"))
            {
                new EZZoneName(args, sender);
            }
            else if (subCommand.equals("message"))
            {
                new EZZoneMessage(args, sender);
            }
            else if (subCommand.equals("draw"))
            {
                new EZZoneDraw(args, sender);
            }
            else if (subCommand.equals("confirm"))
            {
                new EZZoneConfirm(sender);
            }
            else if (subCommand.equals("edit"))
            {
                new EZZoneEdit(args, sender);
            }
            else if (subCommand.equals("world"))
            {
                new EZZoneWorld(args, sender);
            }
            else if (subCommand.equals("cancel"))
            {
                new EZZoneCancel(sender);
            }
            else if (subCommand.equals("delete"))
            {
                new EZZoneDelete(sender);
            }
            else if (subCommand.equals("list"))
            {
                new EZZoneList(sender);
            }
            else if (subCommand.equals("info"))
            {
                new EZZoneInfo(args, sender);
            }
            else if (subCommand.equals("perm"))
            {
                new EZZonePerm(args, sender);
            }
            else if (subCommand.equals("debug"))
            {
                new EZZoneDebug(sender);
            }
            else
            {
                new EZZoneHelp(ZoneCommand.NONE, sender, null);
            }
        }
        else
        {
            new EZZoneHelp(ZoneCommand.NONE, sender, null);
        }
        return true;
    }
}
