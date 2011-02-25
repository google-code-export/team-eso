package com.epicsagaonline.bukkit.EpicGates;

import java.io.File;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.plugin.Plugin;

/**
 * EpicZones for Bukkit
 *
 * @author jblaske
 */
public class EpicGates extends JavaPlugin 
{

	private final EpicGatesPlayerListener playerListener = new EpicGatesPlayerListener(this);
	private static final String CONFIG_FILE = "config.yml";
	
	public static PermissionHandler permissions;

	public void onEnable() {

		
		
		File file = new File(this.getDataFolder() + File.separator + CONFIG_FILE);
		//General.config = new EpicGatesConfig(file);
		
		PluginDescriptionFile pdfFile = this.getDescription();

		try 
		{

			PluginManager pm = getServer().getPluginManager();
			
			pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener, Event.Priority.Normal, this);
			
			setupEpicGates();
			setupPermissions();
			
			System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled." );

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
		PluginDescriptionFile pdfFile = this.getDescription();	
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled." );
	}
	public void setupPermissions()
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		if(EpicGates.permissions == null)
		{
			if(test != null) 
			{
				if(!test.isEnabled())
				{
					getServer().getPluginManager().enablePlugin(test);
				}
				EpicGates.permissions = ((Permissions)test).getHandler();
			}
		}
	}

	public void setupEpicGates()
	{
		General.plugin = this;
		General.myGates.clear();
		General.myGateTags.clear();
		//General.config.load();
		//General.config.save();
		General.loadGates();
	}
}

