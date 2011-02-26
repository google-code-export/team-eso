package com.epicsagaonline.bukkit.EpicGates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class General {

	public static Map<String, EpicGate> myGates = new HashMap<String, EpicGate>();
	public static ArrayList<String> myGateTags = new ArrayList<String>();
	public static Map<String, EpicGatesPlayer> myPlayers = new HashMap<String, EpicGatesPlayer>();
	
	public static EpicGatesConfig config;
	public static final String NO_PERM_ENTER = "You do not have permission to enter ";
	public static final String NO_PERM_BORDER = "You have reached the border of the map.";
	public static EpicGates plugin;

	private static final String GATE_FILE = "gates.txt";

	public static void loadGates()
	{
		String line;
		File file = new File(plugin.getDataFolder() + File.separator + GATE_FILE);

		try
		{
			Scanner scanner = new Scanner(file);
			myGates.clear();
			myGateTags.clear();
			try {
				while(scanner.hasNext())
				{
					EpicGate newGate;
					line = scanner.nextLine().trim();
					if(line.startsWith("#") || line.isEmpty()){continue;}
					newGate = new EpicGate(line);;
					General.myGates.put(newGate.getTag(), newGate);
					General.myGateTags.add(newGate.getTag());
				}

			}
			finally {
				scanner.close();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		LinkGates();

	}

	private static void LinkGates()
	{
		
		for(String gateTag: myGateTags)
		{
			EpicGate gate = myGates.get(gateTag);
			if(gate.getTargetTag().length() > 0)
			{
				myGates.get(gateTag).setTarget(myGates.get(gate.getTargetTag()));
			}
		}
	}
	
	public static void saveGates()
	{
		File file = new File(plugin.getDataFolder() + File.separator + GATE_FILE);

		try 
		{
			String data = BuildGateData();
			Writer output = new BufferedWriter(new FileWriter(file, false));
			try 
			{
				output.write(data);
			}
			finally {
				output.close();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static String BuildGateData()
	{
		String result = "#Gate Tag|World|X|Y|Z|Target Tag\n";
		String line = "";

		for(String gateTag: myGateTags)
		{
			EpicGate gate = myGates.get(gateTag);
			
			line = gate.getTag() + ",";
			line = line + gate.getLocation().getWorld().getName() + ",";
			line = line + gate.getLocation().getBlockX() + ",";
			line = line + gate.getLocation().getBlockY() + ",";
			line = line + gate.getLocation().getBlockZ() + ",";
			line = line + gate.getTargetTag() + ",";
			line = line + gate.getDirection() + "\n";
			result = result + line;
		}
		return result;
	}
	
	public static void addPlayer(String name)
	{
		myPlayers.put(name, new EpicGatesPlayer());
	}

	public static void removePlayer(String name)
	{
		myPlayers.remove(name);
	}

}
