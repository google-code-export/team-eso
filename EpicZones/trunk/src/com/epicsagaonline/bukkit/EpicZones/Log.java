package com.epicsagaonline.bukkit.EpicZones;

public class Log
{

    private static String PluginName;

    public static void Init(String pluginName)
    {
        PluginName = pluginName;
    }

    public static void Write(String message)
    {
        if (message != null)
        {
            System.out.println("[" + PluginName + "] " + message.trim());
        }
    }

}
