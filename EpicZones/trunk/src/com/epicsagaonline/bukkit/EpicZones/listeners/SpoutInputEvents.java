package com.epicsagaonline.bukkit.EpicZones.listeners;

import com.epicsagaonline.bukkit.EpicZones.General;
import com.epicsagaonline.bukkit.EpicZones.integration.EpicSpout;
import com.epicsagaonline.bukkit.EpicZones.objects.EpicZonePlayer;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

public class SpoutInputEvents extends InputListener
{

    public
    @Override
    void onKeyPressedEvent(KeyPressedEvent event)
    {
        if (event.getKey() == Keyboard.KEY_F4)
        {
            EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getName());
            ezp.UI.setDisplayXYZ(!ezp.UI.getDisplayXYZ());
            EpicSpout.UpdatePlayerXYZ(event.getPlayer());
        }
    }

    // public @Override void onKeyReleasedEvent(KeyReleasedEvent event)
    // {}
    //
    // public @Override void onRenderDistanceChange(RenderDistanceChangeEvent
    // event)
    // {}
}
