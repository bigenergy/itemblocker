package com.bigenergy.itemblocker.integration.curios;

import com.bigenergy.itemblocker.ItemBlocker;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

public class CuriosEventHandler {

    @SubscribeEvent
    public void onCurioEquipEvent(CurioEquipEvent event) {
        if(ItemBlocker.shouldDelete(event.getStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCurioChangeEvent(CurioChangeEvent event) {
        if(ItemBlocker.shouldDelete(event.getTo())) {
            event.setCanceled(true);
        }
    }
}
