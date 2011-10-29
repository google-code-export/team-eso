package com.epicsagaonline.bukkit.EpicZones.objects;

public class EpicZoneFire
{
    private boolean ignite = true;
    private boolean spread = false;

    public void setIgnite(boolean value)
    {
        this.ignite = value;
    }

    public boolean getIgnite()
    {
        return this.ignite;
    }

    public void setSpread(boolean value)
    {
        this.spread = value;
    }

    public boolean getSpread()
    {
        return this.spread;
    }

    public EpicZoneFire()
    {
        ignite = false;
        spread = false;
    }

    public EpicZoneFire(String value)
    {

        String[] split = null;
        if (value.contains(":"))
        {
            split = value.split(":");
        }
        else if (value.contains(" "))
        {
            split = value.split(" ");
        }

        if (split != null)
        {
            if (split.length > 0)
            {
                this.ignite = Boolean.valueOf(split[0]);
            }
            if (split.length > 1)
            {
                this.spread = Boolean.valueOf(split[1]);
            }
        }
    }

    public String toString()
    {
        return String.valueOf(this.ignite) + ":"
                + String.valueOf(this.spread);
    }
}
