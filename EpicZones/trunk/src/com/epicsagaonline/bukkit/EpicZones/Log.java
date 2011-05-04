package com.epicsagaonline.bukkit.EpicZones;

public class Log {

	public static void Write(String message)
	{
		if(message != null)
		{
			System.out.println("[EpicZones] " + message.trim());
		}
	}

}
