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

package com.epicsagaonline.bukkit.SolarRedstoneTorch.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.epicsagaonline.bukkit.SolarRedstoneTorch.utility.*;
import com.epicsagaonline.bukkit.SolarRedstoneTorch.*;

public class PlayerEvents extends PlayerListener
{
	public PlayerEvents(SolarRedstoneTorch instance)
	{}

	public @Override void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!event.isCancelled())
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Block blk = event.getClickedBlock();
				if (blk.getType() == Material.REDSTONE_TORCH_ON || blk.getType() == Material.REDSTONE_TORCH_OFF)
				{
					DAL.addTorch(blk, event.getPlayer());
					// Cancel the event so items don't get built next to the
					// torch when users are trying to configure torches.
					event.setCancelled(true);
				}
			}
		}
	}
}
