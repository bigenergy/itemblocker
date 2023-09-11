package com.bigenergy.itemblocker.commands;

import com.bigenergy.itemblocker.ItemBlocker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandBanList implements Command<CommandSourceStack> {
    private static final CommandBanList CMD = new CommandBanList();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        return Commands
                .literal("list")
                .requires(cs -> cs.hasPermission(2))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().getPlayerOrException().displayClientMessage(
                Component.literal("List of blocked items: ")
                        .append(ItemBlocker.itemListToString(ItemBlocker.BANNED_ITEMS)),
                false);
        return 0;
    }
}
