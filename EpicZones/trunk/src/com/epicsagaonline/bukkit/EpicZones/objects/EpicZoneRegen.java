package com.epicsagaonline.bukkit.EpicZones.objects;

public class EpicZoneRegen {

	private Integer amount = 0;
	private Integer delay = 0;
	private Integer interval = 500;
	private Integer maxRegen = 20;
	private Integer minDegen = 0;
	private Integer restDelay = 0;
	private Integer bedBonus = 0;

	public void setAmount(Integer value)
	{
		this.amount = value;	
	}
	public Integer getAmount()
	{
		return this.amount;
	}

	public void setDelay(Integer value)
	{
		this.delay = value;	
	}
	public Integer getDelay()
	{
		return this.delay;
	}

	public void setInterval(Integer value)
	{
		this.interval = value;	
	}
	public Integer getInterval()
	{
		return this.interval;
	}

	public void setMaxRegen(Integer value)
	{
		this.maxRegen = value;	
	}
	public Integer getMaxRegen()
	{
		return this.maxRegen;
	}

	public void setMinDegen(Integer value)
	{
		this.minDegen = value;	
	}
	public Integer getMinDegen()
	{
		return this.minDegen;
	}

	public void setRestDelay(Integer value)
	{
		this.restDelay = value;	
	}
	public Integer getRestDelay()
	{
		return this.restDelay;
	}

	public void setBedBonus(Integer value)
	{
		this.bedBonus = value;	
	}
	public Integer getBedBonus()
	{
		return this.bedBonus;
	}

	public EpicZoneRegen()
	{
		this.amount = 0;
		this.delay = 0;
		this.interval = 500;
		this.maxRegen = 20;
		this.minDegen = 0;
		this.restDelay = 0;
		this.bedBonus = 0;
	}

	public EpicZoneRegen(String value)
	{

		String[] split = null;
		if(value.contains(":"))
		{
			split = value.split(":");
		}
		else if(value.contains(" "))
		{
			split = value.split(" ");
		}
		
		if(split != null)
		{
			if (split.length > 0)
			{
				this.amount = Integer.valueOf(split[0]);
			}
			if (split.length > 1)
			{
				this.delay = Integer.valueOf(split[1]);
			}
			if (split.length > 2)
			{
				this.interval = Integer.valueOf(split[2]);
			}
			if (split.length > 3)
			{
				this.maxRegen = Integer.valueOf(split[3]);
			}
			if (split.length > 4)
			{
				this.minDegen = Integer.valueOf(split[4]);
			}
			if (split.length > 5)
			{
				this.restDelay = Integer.valueOf(split[5]);
			}
			if (split.length > 6)
			{
				this.bedBonus = Integer.valueOf(split[6]);
			}
		}
	}


	public String toString()
	{
		return String.valueOf(this.amount) + ":" 
		+ String.valueOf(this.delay) + ":"
		+ String.valueOf(this.interval) + ":"
		+ String.valueOf(this.maxRegen) + ":"
		+ String.valueOf(this.minDegen) + ":"
		+ String.valueOf(this.restDelay) + ":"
		+ String.valueOf(this.bedBonus);
	}
}
