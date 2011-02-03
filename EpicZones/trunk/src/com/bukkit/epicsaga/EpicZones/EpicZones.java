package com.bukkit.epicsaga.EpicZones;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.bukkit.jblaske.EpicZones.General;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;

/**
 * EpicZones for Bukkit
 *
 * @author jblaske
 */
public class EpicZones extends JavaPlugin {
    private final EpicZonesPlayerListener playerListener = new EpicZonesPlayerListener(this);
    private final EpicZonesBlockListener blockListener = new EpicZonesBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final String CONFIG_FILE = "config.yml";
           
    public static PermissionHandler permissions;
    
    public EpicZones(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        File file = new File(folder+File.separator+CONFIG_FILE);
        General.config = new EpicZonesConfig(file);
    }

    public void onEnable() {

    	// Register events
    	PluginManager pm = getServer().getPluginManager();
           
    	setupPermissions();
    	
    	try {
			checkConfigDir();
			General.config.load();
			General.config.save();
			General.loadZones(this.getDataFolder());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.PLAYER_ITEM , this.playerListener, Event.Priority.Normal, this);
    	
    	pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Normal, this);
    	pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Normal, this);
    	
    	
    	PluginDescriptionFile pdfFile = this.getDescription();
    	System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled." );
        
    }
    public void onDisable() {
    	PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled." );
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
    private void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
    	if(test != null) {
    	// make sure Permissions gets enabled first
    	if(!test.isEnabled()){getServer().getPluginManager().enablePlugin(test);}
    	permissions = ((Permissions)test).getHandler();
    	}
    	else {
    	//throw new EnableError("Permission plugin not available.");
    	}
    	}

    private void checkConfigDir() throws Exception
    {
        File dir = this.getDataFolder();
         
        if(!dir.isDirectory() && !dir.mkdirs()) {
            throw new Exception( "Could not make configuration directory "+
                    dir.getPath());
        }
         
    }
}

