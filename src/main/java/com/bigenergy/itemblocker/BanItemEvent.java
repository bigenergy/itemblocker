package com.bigenergy.itemblocker;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired whenever an item stack is looked at for deletion
 */
@Event.HasResult
public class BanItemEvent extends Event {
    public final ItemStack stack;

    public BanItemEvent(ItemStack stack) {
        this.stack = stack;
    }
}
