package com.epicsagaonline.bukkit.ChallengeMaps;

public class Log {

	public static void Write(String message)
	{
		if(message != null)
		{
			System.out.println("[ChallengeMaps] " + message.trim());
		}
	}

}
