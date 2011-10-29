package com.epicsagaonline.bukkit.EpicZones.objects;

import org.getspout.spoutapi.gui.GenericLabel;

public class EpicZonePlayerUI
{
    private GenericLabel zoneLabel = null;
    private GenericLabel xyzLabel = null;
    private boolean displayXYZ = false;

    public GenericLabel getZoneLabel()
    {
        return zoneLabel;
    }

    public GenericLabel getXYZLabel()
    {
        return xyzLabel;
    }

    public boolean getDisplayXYZ()
    {
        return displayXYZ;
    }

    public void setZoneLabel(GenericLabel value)
    {
        this.zoneLabel = value;
    }

    public void setXYZLabel(GenericLabel value)
    {
        this.xyzLabel = value;
    }

    public void setDisplayXYZ(boolean value)
    {
        this.displayXYZ = value;
        this.xyzLabel.setVisible(!value);
    }
}
