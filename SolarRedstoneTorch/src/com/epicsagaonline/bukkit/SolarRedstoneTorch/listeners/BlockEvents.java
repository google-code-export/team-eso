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
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.epicsagaonline.bukkit.SolarRedstoneTorch.integration.PermissionsManager;
import com.epicsagaonline.bukkit.SolarRedstoneTorch.utility.*;
import com.epicsagaonline.bukkit.SolarRedstoneTorch.*;

public class BlockEvents extends BlockListener
{
	public BlockEvents(SolarRedstoneTorch instance)
	{}

	public @Override void onBlockBreak(BlockBreakEvent event)
	{
		if (!event.isCancelled())
		{
			if (event.getBlock().getType() == Material.REDSTONE_TORCH_ON || event.getBlock().getType() == Material.REDSTONE_TORCH_OFF)
			{
				if (PermissionsManager.hasPermission(event.getPlayer(), "solarredstonetorch.add"))
				{
					DAL.Torches.remove(event.getBlock().getLocation());
				}
				else
				{
					event.getPlayer().sendMessage("You do not have permission to remove light sensitive redstone torches.");
					event.setCancelled(true);
				}

			}
		}
	}

	public @Override void onBlockRedstoneChange(BlockRedstoneEvent event)
	{
		SolarTorch trch = DAL.getTorch(event.getBlock().getLocation());
		if (trch != null)
		{
			// If a redstone torch is trying to turn back on, when it should not
			// yet. Set the current back to 0.
			if (trch.BaseBlock.getLightLevel() > trch.Sensitivity)
			{
				event.setNewCurrent(0);
			}
		}
	}

	public @Override void onBlockPhysics(BlockPhysicsEvent event)
	{
		if (!event.isCancelled())
		{
			if (event.getType() == Type.BLOCK_PHYSICS)
			{
				Block blck = event.getBlock();
				if (blck.getType() == Material.REDSTONE_TORCH_OFF || blck.getType() == Material.REDSTONE_TORCH_ON)
				{
					SolarTorch trch = DAL.getTorch(blck.getLocation());
					if (trch != null)
					{
						// If a redstone torch is trying to turn back on, when
						// it should not yet. Cancle the physics of the block,
						// so that the power does not turn back on.
						if (trch.BaseBlock.getLightLevel() > trch.Sensitivity)
						{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

}