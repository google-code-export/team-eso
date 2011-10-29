package com.epicsagaonline.bukkit.EpicZones.listeners;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.integration.EpicSpout;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;

public class SpoutEvents extends SpoutListener
{
    public
    @Override
    void onSpoutCraftEnable(SpoutCraftEnableEvent event)
    {
        EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getName());
        EpicSpout.UpdatePlayerZone(ezp, ezp.getCurrentZone());
    }

    // public @Override void onServerTick(ServerTickEvent event)
    // {
    //
    // }

}
