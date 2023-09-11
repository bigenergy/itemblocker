package com.bigenergy.itemblocker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemBlockerConfig {
    public static final General general;
    public static final ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER;

    // Don't judge me! It's because of auto formatting moving the order around!
    static {
        COMMON_BUILDER = new ForgeConfigSpec.Builder();

        general = new General();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static class General {

        public final ForgeConfigSpec.BooleanValue enableLogs;


        General() {
            COMMON_BUILDER.push("general");

            this.enableLogs = COMMON_BUILDER
                    .comment("Enable logs for removing items from inventories, drops and other events")
                    .define("enableLogs",false);

            COMMON_BUILDER.pop();
        }
    }

}

