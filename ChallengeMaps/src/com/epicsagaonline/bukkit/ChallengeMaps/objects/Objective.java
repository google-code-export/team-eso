package com.epicsagaonline.bukkit.ChallengeMaps.objects;

import java.util.HashMap;

import com.epicsagaonline.bukkit.ChallengeMaps.Util;

public class Objective
{
	private String tag = "";
	private String text = "";
	private Integer scoreValue = 0;
	private Trigger trigger = null;

	@SuppressWarnings("unchecked") public Objective(String objTag, HashMap<String, Object> newObj)
	{
		this.tag = objTag;
		this.text = Util.getStringValueFromHashSet("Text", newObj);
		this.scoreValue = Util.getIntegerValueFromHashSet("ScoreValue", newObj);
		this.trigger = new Trigger((HashMap<String, Object>) Util.getObjectValueFromHashSet("Trigger", newObj));
	}

	public String getTag()
	{
		return this.tag;
	}

	public String getText()
	{
		return this.text;
	}

	public Integer getScoreValue()
	{
		return this.scoreValue;
	}

	public Trigger getTrigger()
	{
		return this.trigger;
	}

	public void setTag(String value)
	{
		this.tag = value;
	}

	public void setText(String value)
	{
		this.text = value;
	}

	public void setScoreValue(Integer value)
	{
		this.scoreValue = value;
	}

	public void setTrigger(Trigger value)
	{
		this.trigger = value;
	}
}
