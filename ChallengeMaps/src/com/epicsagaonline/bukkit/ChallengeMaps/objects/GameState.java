package com.epicsagaonline.bukkit.ChallengeMaps.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.epicsagaonline.bukkit.ChallengeMaps.Log;
import com.epicsagaonline.bukkit.ChallengeMaps.Util;
import com.epicsagaonline.bukkit.ChallengeMaps.DAL.Current;

public class GameState
{
	private Map map;
	private World world;
	private Player player;
	private String key;
	private Integer score;
	private Location entryPoint;
	private Boolean inChallenge = false;
	private Integer deathCount = 0;
	private Set<String> completedObjectives = new HashSet<String>();
	// <ItemID, Count>
	private HashMap<Integer, Integer> blocksBroken = new HashMap<Integer, Integer>();
	// <ItemID, Count>
	private HashMap<Integer, Integer> blocksPlaced = new HashMap<Integer, Integer>();
	// <Distance, <ItemID, Count>>
	private HashMap<Integer, HashMap<Integer, Integer>> blocksPlacedDistance = new HashMap<Integer, HashMap<Integer, Integer>>();
	// <ItemID, Count>
	private HashMap<Integer, Integer> itemsCrafted = new HashMap<Integer, Integer>();
	// <ItemID, Count> - Does not count the Initial start of the map.
	private HashMap<Integer, Integer> mapContents = new HashMap<Integer, Integer>();
	private HashMap<String, Object> inventoryBuffer = new HashMap<String, Object>();
	// <ItemID, Count>
	private HashMap<Integer, Integer> rewards = new HashMap<Integer, Integer>();

	public boolean PendingRemoval = false;

	public GameState(Map newMap, Player newPlayer)
	{
		this.map = newMap;
		this.player = newPlayer;
		this.key = newMap.getMapName() + "_" + newPlayer.getName();
		this.score = 0;
		this.world = LoadWorld();
	}

	@SuppressWarnings("unchecked") public GameState(HashMap<String, Object> data, Player newPlayer)
	{

		this.map = Current.Maps.get(Util.getStringValueFromHashSet("MapName", data).toLowerCase());
		this.player = newPlayer;
		this.key = map.getMapName() + "_" + newPlayer.getName();
		this.world = LoadWorld();
		this.score = Util.getIntegerValueFromHashSet("Score", data);
		this.deathCount = Util.getIntegerValueFromHashSet("DeathCount", data);
		this.blocksBroken = (HashMap<Integer, Integer>) Util.getObjectValueFromHashSet("BlocksBroken", data);
		this.blocksPlaced = (HashMap<Integer, Integer>) Util.getObjectValueFromHashSet("BlocksPlaced", data);
		this.blocksPlacedDistance = (HashMap<Integer, HashMap<Integer, Integer>>) Util.getObjectValueFromHashSet("BlocksPlacedDistance", data);
		this.itemsCrafted = (HashMap<Integer, Integer>) Util.getObjectValueFromHashSet("ItemsCrafted", data);
		this.mapContents = (HashMap<Integer, Integer>) Util.getObjectValueFromHashSet("MapContents", data);
		this.rewards = (HashMap<Integer, Integer>) Util.getObjectValueFromHashSet("Rewards", data);
		this.entryPoint = Util.GetLocationFromString(Util.getStringValueFromHashSet("EntryPoint", data));

		this.inventoryBuffer = (HashMap<String, Object>) Util.getObjectValueFromHashSet("InventoryBuffer", data);

		this.inChallenge = Util.getBooleanValueFromHashSet("InChallenge", data);

		HashSet<String> objSet = (HashSet<String>) Util.getObjectValueFromHashSet("CompletedObjectives", data);
		for (String objName : objSet)
		{
			this.completedObjectives.add(objName);
		}

		Log.Write("Loaded GameState: " + this.getKey() + " - " + this.completedObjectives.size() + " Completed Objective(s).");
	}

	public Map getMap()
	{
		return this.map;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public String getKey()
	{
		return this.key;
	}

	public Integer getScore()
	{
		return this.score;
	}

	public Integer getDeathCount()
	{
		return this.deathCount;
	}

	public World getWorld()
	{
		return this.world;
	}

	public Location getEntryPoint()
	{
		return this.entryPoint;
	}

	public Set<String> getCompletedObjectives()
	{
		return this.completedObjectives;
	}

	public HashMap<Integer, Integer> getBlocksBroken()
	{
		return this.blocksBroken;
	}

	public HashMap<Integer, Integer> getBlocksPlaced()
	{
		return this.blocksPlaced;
	}

	public HashMap<Integer, HashMap<Integer, Integer>> getBlocksPlacedDistance()
	{
		return this.blocksPlacedDistance;
	}

	public HashMap<Integer, Integer> getItemsCrafted()
	{
		return this.itemsCrafted;
	}

	public HashMap<Integer, Integer> getMapContents()
	{
		return this.mapContents;
	}

	public HashMap<Integer, Integer> getRewards()
	{
		return this.rewards;
	}

	public HashMap<String, Object> getInventoryBuffer()
	{
		return this.inventoryBuffer;
	}

	public Boolean getInChallenge()
	{
		return this.inChallenge;
	}

	public void setEntryPoint(Location value)
	{
		this.entryPoint = value;
	}

	public void setInChallenge(Boolean value)
	{
		this.inChallenge = value;
	}

	public boolean canRespawn()
	{
		boolean result = true;

		if (this.map.getNumberOfLives() < 0)
		{
			result = true;
		}
		else
		{
			result = (this.deathCount <= this.map.getNumberOfLives());
		}
		
		return result;
	}

	public void addBlockBreak(Integer itemID)
	{
		Integer value;
		if (blocksBroken.containsKey(itemID))
		{
			value = blocksBroken.get(itemID);
			value++;
		}
		else
		{
			value = 1;
		}
		blocksBroken.put(itemID, value);
	}

	public void addBlockPlace(Integer itemID)
	{
		Integer value;
		if (blocksPlaced.containsKey(itemID))
		{
			value = blocksPlaced.get(itemID);
			value++;
		}
		else
		{
			value = 1;
		}
		blocksPlaced.put(itemID, value);
	}

	public void addBlockPlaceDistance(Integer itemID, Location loc)
	{
		HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
		int value;
		double xsquared = loc.getBlockX() * loc.getBlockX();
		double ysquared = loc.getBlockY() * loc.getBlockY();
		double distanceFromCenter = Math.sqrt(xsquared + ysquared);
		int distance = (int) distanceFromCenter;

		if (blocksPlacedDistance.containsKey(distance))
		{
			items = blocksPlacedDistance.get(distance);
			if (items.containsKey(itemID))
			{
				value = items.get(itemID);
				value++;
			}
			else
			{
				value = 1;
			}
		}
		else
		{
			value = 1;
		}
		items.put(itemID, value);
		blocksPlacedDistance.put(distance, items);
	}

	public void addItemCrafted(Integer itemID)
	{
		Integer value;
		if (itemsCrafted.containsKey(itemID))
		{
			value = itemsCrafted.get(itemID);
			value++;
		}
		else
		{
			value = 1;
		}
		itemsCrafted.put(itemID, value);
	}

	public void addMapContents(Integer itemID)
	{
		Integer value;
		if (mapContents.containsKey(itemID))
		{
			value = mapContents.get(itemID);
			value++;
		}
		else
		{
			value = 1;
		}
		mapContents.put(itemID, value);
	}

	public void removeMapContents(Integer itemID)
	{
		Integer value;
		if (mapContents.containsKey(itemID))
		{
			value = mapContents.get(itemID);
			value--;
		}
		else
		{
			value = -1;
		}
		mapContents.put(itemID, value);
	}

	public void addCompleteObjective(Objective obj)
	{
		if (!this.completedObjectives.contains(obj.getTag()))
		{
			this.completedObjectives.add(obj.getTag());
			this.score += obj.getScoreValue();
		}
	}

	public void addDeath()
	{
		this.deathCount++;
		this.score = this.score + this.map.getDeathPenalty();
	}

	public void toggleInventory()
	{
		if (!this.inChallenge)
		{
			HashMap<String, Object> tempInv = GetInventoryBuffer(this.player.getInventory());
			GetInventory(this.getPlayer().getInventory());
			this.inventoryBuffer = tempInv;
		}
	}

	private HashMap<String, Object> GetInventoryBuffer(PlayerInventory srcInv)
	{

		HashMap<String, Object> result = new HashMap<String, Object>();

		result.clear();
		result.put("Boots", Util.getStringFromItemStack(srcInv.getBoots()));
		result.put("Chestplate", Util.getStringFromItemStack(srcInv.getChestplate()));
		result.put("Helmet", Util.getStringFromItemStack(srcInv.getHelmet()));
		result.put("Leggings", Util.getStringFromItemStack(srcInv.getLeggings()));

		HashSet<String> contents = new HashSet<String>();
		if (srcInv.getContents() != null)
		{
			for (ItemStack is : srcInv.getContents())
			{
				if (is != null)
				{
					contents.add(Util.getStringFromItemStack(is));
				}
			}
		}
		result.put("Contents", contents);

		return result;

	}

	@SuppressWarnings("unchecked") private void GetInventory(PlayerInventory result)
	{
		String value;

		result.clear();
		result.setArmorContents(null);

		value = Util.getStringValueFromHashSet("Boots", this.inventoryBuffer);
		if (value != null && value.length() > 0)
		{
			ItemStack is = Util.getItemStackFromString(value);
			if (is != null)
			{
				result.setBoots(is);
			}
		}
		value = Util.getStringValueFromHashSet("Chestplate", this.inventoryBuffer);
		if (value != null && value.length() > 0)
		{
			ItemStack is = Util.getItemStackFromString(value);
			if (is != null)
			{
				result.setChestplate(is);
			}
		}
		value = Util.getStringValueFromHashSet("Helmet", this.inventoryBuffer);
		if (value != null && value.length() > 0)
		{
			ItemStack is = Util.getItemStackFromString(value);
			if (is != null)
			{
				result.setHelmet(is);
			}
		}
		value = Util.getStringValueFromHashSet("Leggings", this.inventoryBuffer);
		if (value != null && value.length() > 0)
		{
			ItemStack is = Util.getItemStackFromString(value);
			if (is != null)
			{
				result.setLeggings(is);
			}
		}
		HashSet<String> contents = (HashSet<String>) Util.getObjectValueFromHashSet("Contents", this.inventoryBuffer);

		if (contents != null)
		{
			for (String contentItem : contents)
			{
				ItemStack is = Util.getItemStackFromString(contentItem);
				if (is != null)
				{
					result.addItem(is);
				}
			}
		}
	}
	
	private World LoadWorld()
	{
		World result;
		
		result = Current.Plugin.getServer().getWorld(this.key);
		if(result == null)
		{
			result = Current.LoadWorld(this, this.player);
		}
		return result;
	}
}
