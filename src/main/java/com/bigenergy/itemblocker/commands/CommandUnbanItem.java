package com.bigenergy.itemblocker.commands;

import com.bigenergy.itemblocker.ItemBlocker;
import com.bigenergy.itemblocker.JsonUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandUnbanItem implements Command<CommandSourceStack> {
    private static final CommandUnbanItem CMD = new CommandUnbanItem();
    private static final UnbanAll CMD_ALL = new UnbanAll();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        return Commands
                .literal("unban")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item(context))
                        .executes(CMD))
                .then(Commands.literal("all")
                        .executes(CMD_ALL));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            JsonUtils.removeItemFromJson(ItemBlocker.BANLIST, ItemArgument.getItem(context, "item").getItem());
            context.getSource().sendSuccess(Component.literal("Item " + ForgeRegistries.ITEMS.getKey(ItemArgument.getItem(context, "item").getItem()).toString() + " unbanned!"), false);
        } catch(IndexOutOfBoundsException e) {
            context.getSource().sendFailure(Component.literal("Failed to unban item"));
        }
        return 0;
    }

    public static class UnbanAll implements Command<CommandSourceStack>{
        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            JsonUtils.removeAllItemsFromJson(ItemBlocker.BANLIST);
            context.getSource().sendSuccess(Component.literal("All items are unbanned!"), false);
            return 0;
        }
    }
}
