package com.epicsagaonline.bukkit.EpicGates;

public class Log
{

	public static void Write(String message)
	{
		if (message != null)
		{
			System.out.println("[EpicGates] " + message.trim());
		}
	}

}
