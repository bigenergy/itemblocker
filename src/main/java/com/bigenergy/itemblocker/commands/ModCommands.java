package com.bigenergy.itemblocker.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralCommandNode<CommandSourceStack> cmdTut = dispatcher.register(
                Commands.literal("itemblocker")
                        .then(CommandBanItem.register(dispatcher, context))
                        .then(CommandUnbanItem.register(dispatcher, context))
                        .then(CommandBanList.register(dispatcher, context))
        );

        dispatcher.register(Commands.literal("ib").redirect(cmdTut));
    }
}
