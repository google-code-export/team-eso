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

package com.epicsagaonline.bukkit.SolarRedstoneTorch;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.epicsagaonline.bukkit.SolarRedstoneTorch.listeners.BlockEvents;
import com.epicsagaonline.bukkit.SolarRedstoneTorch.listeners.PlayerEvents;
import com.epicsagaonline.bukkit.SolarRedstoneTorch.utility.*;

public class SolarRedstoneTorch extends JavaPlugin
{

	private final BlockEvents blockListener = new BlockEvents(this);
	private final PlayerEvents playerListener = new PlayerEvents(this);
	private final LightSensing sence = new LightSensing();
	
	public void onEnable() {
		
		PluginDescriptionFile pdfFile = this.getDescription();

		try 
		{

			PluginManager pm = getServer().getPluginManager();
			
			pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Lowest, this);
			pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.blockListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.BLOCK_PHYSICS, this.blockListener, Event.Priority.Monitor, this);
			
			pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
			
			DAL.LoadTorches(this);
			
			getServer().getScheduler().scheduleAsyncRepeatingTask(this, sence, 100, 25);
			
			System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled." );

		} 
		catch (Throwable e) 
		{
			System.out.println( "["+pdfFile.getName()+"]" + " error starting: "+
					e.getMessage() +" Disabling plugin" );
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() 
	{
		getServer().getScheduler().cancelTasks(this);
		DAL.SaveTorches();
		PluginDescriptionFile pdfFile = this.getDescription();	
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled." );
	}
}
