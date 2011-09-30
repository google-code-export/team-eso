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

package com.epicsagaonline.bukkit.SolarRedstoneTorch.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.epicsagaonline.bukkit.SolarRedstoneTorch.*;
import com.epicsagaonline.bukkit.SolarRedstoneTorch.integration.PermissionsManager;

public class DAL
{

	private static final String DATA_STORE = "Torches.dat";
	private static SolarRedstoneTorch plugin = null;
	public static HashMap<Location, SolarTorch> Torches = null;

	public static void LoadTorches(SolarRedstoneTorch plgin)
	{
		if (plugin == null && plgin != null)
		{
			plugin = plgin;
		}

		File file = new File(plugin.getDataFolder() + File.separator + DATA_STORE);

		Torches = new HashMap<Location, SolarTorch>();

		if (file.exists())
		{
			try
			{

				Scanner scanner = new Scanner(file);
				String line;
				Torches.clear();

				try
				{
					while (scanner.hasNext())
					{
						SolarTorch trch;
						Block blk;

						line = scanner.nextLine().trim();
						if (line.startsWith("#") || line.isEmpty())
						{
							continue;
						}
						String[] data = line.split(":");
						if (data.length == 5)
						{
							blk = plugin.getServer().getWorld(data[0]).getBlockAt(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
							trch = new SolarTorch(blk, Integer.parseInt(data[4]));
							Torches.put(blk.getLocation(), trch);
						}
					}
					Log.Write(Torches.size() + " light sensitive torches configured.");
				}
				finally
				{
					scanner.close();
				}
			}
			catch (Exception e)
			{
				Log.Write(e.getMessage());
			}
		}
	}

	public static void SaveTorches()
	{
		File file = new File(plugin.getDataFolder() + File.separator + DATA_STORE);

		if (!plugin.getDataFolder().exists())
		{
			plugin.getDataFolder().mkdir();
		}

		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}

			String data = BuildTorchData();
			Writer output = new BufferedWriter(new FileWriter(file, false));
			try
			{
				output.write(data);
			}
			finally
			{
				output.close();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static String BuildTorchData()
	{
		String result = "#World:X:Y:Z:Sensitivity\n";
		String line = "";

		for (Location loc : Torches.keySet())
		{
			SolarTorch trch = Torches.get(loc);
			line = trch.BaseBlock.getWorld().getName() + ":";
			line = line + trch.BaseBlock.getLocation().getBlockX() + ":";
			line = line + trch.BaseBlock.getLocation().getBlockY() + ":";
			line = line + trch.BaseBlock.getLocation().getBlockZ() + ":";
			line = line + trch.Sensitivity + "\n";
			result = result + line;
		}
		return result;
	}

	public static SolarTorch getTorch(Location loc)
	{
		if (Torches == null)
		{
			LoadTorches(null);
		}
		return Torches.get(loc);
	}

	public static void addTorch(Block block, Player player)
	{
		if (Torches == null)
		{
			LoadTorches(null);
		}

		SolarTorch trch = Torches.get(block.getLocation());
		if (trch != null)
		{
			if (PermissionsManager.hasPermission(player, "solarredstonetorch.edit"))
			{
				trch.IncrementSensitivity();
				player.sendMessage("Sensitivty Level: " + trch.Sensitivity);
			}
			else
			{
				player.sendMessage("You do not have permission to modify redstone light sensitivity levels.");
			}
		}
		else
		{
			if (PermissionsManager.hasPermission(player, "solarredstonetorch.add"))
			{
				Torches.put(block.getLocation(), new SolarTorch(block, 7));
				player.sendMessage("Torch Added! Sensitivty Level: 7");
			}
			else
			{
				player.sendMessage("You do not have permission to make redstone torches light sensitive.");
			}
		}

	}
}
