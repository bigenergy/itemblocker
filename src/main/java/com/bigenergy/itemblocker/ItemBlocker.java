package com.bigenergy.itemblocker;

import com.bigenergy.itemblocker.commands.ModCommands;
import com.bigenergy.itemblocker.config.ItemBlockerConfig;
import com.bigenergy.itemblocker.integration.curios.CuriosEventHandler;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemBlocker.MODID)
public class ItemBlocker {

    public static final String MODID = "itemblocker";
    public static final Logger LOGGER = LogManager.getLogger("ItemBlocker");
    public static File BANLIST;
    public static List<Item> BANNED_ITEMS = new ArrayList<>();

    public ItemBlocker() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                ItemBlockerConfig.COMMON_CONFIG, "itemblocker" + File.separator + "ItemBlocker.toml");

        MinecraftForge.EVENT_BUS.register(this);

        if (ModList.get().isLoaded("curios") && ItemBlockerConfig.integrations.enableCuriosIntegration.get()) {
            LOGGER.info("Curios found, integration enabled");
            MinecraftForge.EVENT_BUS.register(CuriosEventHandler.class);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Path modFolder = event.getServer().getWorldPath(new LevelResource("serverconfig"));
        LOGGER.info("Loading BannedItems list...");
        BANLIST = JsonUtils.initialize(modFolder, "serverconfig", "itemblocker.json");
        BANNED_ITEMS = JsonUtils.readItemsFromJson(BANLIST);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof ItemEntity) {
            if(shouldDelete(((ItemEntity) event.getEntity()).getItem())) {
                if (ItemBlockerConfig.general.enableLogs.get()) {
                    LOGGER.warn("[ItemSpawn] Deleted item: " + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(((ItemEntity) event.getEntity()).getItem().getItem())).toString() + " on pos: " + event.getEntity().getOnPos().toString());
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemPickup(EntityItemPickupEvent event) {
        if(shouldDelete(event.getItem().getItem())) {
            event.getItem().kill();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if(shouldDelete(event.getItemStack())) {
            if (ItemBlockerConfig.general.enableLogs.get()) {
                LOGGER.warn("[LeftClick] Deleted item: " + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey((event.getItemStack().getItem()))).toString() + " on pos: " + event.getEntity().getOnPos().toString() + " player: " + event.getEntity().getName().getString());
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if(shouldDelete(event.getItemStack())) {
            if (ItemBlockerConfig.general.enableLogs.get()) {
                LOGGER.warn("[RightClick] Deleted item: " + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey((event.getItemStack().getItem()))).toString() + " on pos: " + event.getEntity().getOnPos().toString() + " player: " + event.getEntity().getName().getString());
            }
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public void onPlayerContainerOpen(PlayerContainerEvent event) {
        for(int i = 0; i < event.getContainer().slots.size(); ++i) {
            if(shouldDelete(event.getContainer().getItems().get(i))) {
                event.getContainer().getItems().set(i, ItemStack.EMPTY);
            }
        }
    }

    public static boolean shouldDelete(ItemStack stack) {
        BanItemEvent event = new BanItemEvent(stack);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.getResult() == Event.Result.DEFAULT) return BANNED_ITEMS.contains(stack.getItem());
        else return event.getResult() == Event.Result.DENY;
    }

    public static String itemListToString(List<Item> itemList) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for(Item item: itemList) {
            builder.append(ForgeRegistries.ITEMS.getKey(item).toString()).append(", ");
        }
        if(itemList.size() > 0) builder.delete(builder.length() - 2, builder.length());
        builder.append(']');
        return builder.toString();
    }
}
