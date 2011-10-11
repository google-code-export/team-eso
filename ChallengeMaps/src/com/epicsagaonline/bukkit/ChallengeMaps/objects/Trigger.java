package com.epicsagaonline.bukkit.ChallengeMaps.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.epicsagaonline.bukkit.ChallengeMaps.Util;

public class Trigger
{
	private TriggerTypes type;
	private HashSet<Integer> itemIDs = new HashSet<Integer>(); 
	private Integer quantity;
	private Integer parameter;

	@SuppressWarnings("unchecked") public Trigger(HashMap<String, Object> newTriggerData)
	{

		this.type = TriggerTypes.valueOf(Util.getStringValueFromHashSet("Type", newTriggerData));
		this.quantity = Util.getIntegerValueFromHashSet("Quantity", newTriggerData);
		this.parameter = Util.getIntegerValueFromHashSet("Parameter", newTriggerData);

		ArrayList<Integer> itemids = (ArrayList<Integer>) Util.getObjectValueFromHashSet("ItemID", newTriggerData);
		for (Integer item : itemids)
		{
			this.itemIDs.add(item);
		}

	}

	public Trigger(String newType, String newItemIDs, String newQuantity)
	{
		// Parse string data to object info.
	}

	public TriggerTypes getType()
	{
		return this.type;
	}

	public HashSet<Integer> getItemIDs()
	{
		return this.itemIDs;
	}

	public Integer getQuantity()
	{
		return this.quantity;
	}

	public Integer getParameter()
	{
		return this.parameter;
	}

	public void setType(TriggerTypes value)
	{
		this.type = value;
	}

	public void setItemIDs(HashSet<Integer> value)
	{
		this.itemIDs = value;
	}

	public void setQuantity(Integer value)
	{
		this.quantity = value;
	}

	public void setParameter(Integer value)
	{
		this.parameter = value;
	}
}
