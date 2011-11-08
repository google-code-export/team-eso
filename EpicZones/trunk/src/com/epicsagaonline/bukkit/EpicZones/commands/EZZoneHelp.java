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
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer.EpicZoneMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneHelp
{

    public enum ZoneCommand
    {
        CANCEL, CEILING, CHILD, CONFIRM, CREATE, DELETE, DRAW, EDIT, FLAG, FLOOR, HELP, INFO, LIST, MESSAGE, NAME, NONE, OWNER, PARENT, PERM, RADIUS, SAVE, WORLD, COMMAND
    }

    public EZZoneHelp(ZoneCommand command, CommandSender sender, EpicZonePlayer ezp)
    {
        if (ezp == null)
        {
            if (sender instanceof Player)
            {
                ezp = General.myPlayers.get(((Player) sender).getName());
            }
        }
        if (ezp != null)
        {
            Message.Send(sender, Message_ID.Help_01000);
            Message.Send(sender, Message_ID.Info_00010_Parameter_Description);
            if (ezp.getMode() == EpicZoneMode.ZoneEdit)
            {
                Message.Send(sender, Message_ID.Mode_00011_Edit);
                Message.Send(sender, Message_ID.Help_01001_Name);
                Message.Send(sender, Message_ID.Help_01002_Flag);
                Message.Send(sender, Message_ID.Help_01003_Flag_PVP);
                Message.Send(sender, Message_ID.Help_01004_Flag_Fire);
                Message.Send(sender, Message_ID.Help_01005_Flag_Explode);
                Message.Send(sender, Message_ID.Help_01006_Flag_Mobs);
                Message.Send(sender, Message_ID.Help_01007_Flag_Regen);
                Message.Send(sender, Message_ID.Help_01008_Flag_Sanctuary);
                Message.Send(sender, Message_ID.Help_01009_Flag_FireBunsMobs);
                Message.Send(sender, Message_ID.Help_01010_Floor);
                Message.Send(sender, Message_ID.Help_01011_Ceiling);
                Message.Send(sender, Message_ID.Help_01012_Child);
                Message.Send(sender, Message_ID.Help_01013_Owner);
                Message.Send(sender, Message_ID.Help_01014_Message);
                Message.Send(sender, Message_ID.Help_01015_World);
                Message.Send(sender, Message_ID.Help_01016_Draw);
                Message.Send(sender, Message_ID.Help_01017_Cancel);
                Message.Send(sender, Message_ID.Help_01018_Edit_Delete);
                Message.Send(sender, Message_ID.Help_01019_Edit_Save);
            }
            else if (ezp.getMode() == EpicZoneMode.ZoneDraw)
            {
                Message.Send(sender, Message_ID.Mode_00012_Draw);
                Message.Send(sender, Message_ID.Help_01020_Draw_Save);
                Message.Send(sender, Message_ID.Help_01022_DrawConfirm_Cancel);
            }
            else if (ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
            {
                Message.Send(sender, Message_ID.Mode_00013_DrawConfirm);
                Message.Send(sender, Message_ID.Help_01021_DrawConfirm_Confirm);
                Message.Send(sender, Message_ID.Help_01022_DrawConfirm_Cancel);
            }
            else if (ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
            {
                Message.Send(sender, Message_ID.Mode_00014_DeleteConfirm);
                Message.Send(sender, Message_ID.Help_01021_DrawConfirm_Confirm);
                Message.Send(sender, Message_ID.Help_01022_DrawConfirm_Cancel);
            }
            else
            {
                Message.Send(sender, Message_ID.Help_01024_Edit);
                Message.Send(sender, Message_ID.Help_01025_Create);
                Message.Send(sender, Message_ID.Help_01026_List);
                Message.Send(sender, Message_ID.Help_01027_Info);
            }
        }
        else
        {
            Message.Send(sender, Message_ID.Help_01026_List);
            Message.Send(sender, Message_ID.Help_01027_Info);
        }
    }
}