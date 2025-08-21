package fr.arsuup.quoicoubeh.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import fr.arsuup.quoicoubeh.Mod_quoicoubaka;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class Setspawn {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setspawn")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    int x = IntegerArgumentType.getInteger(context, "x");
                                                    int y = IntegerArgumentType.getInteger(context, "y");
                                                    int z = IntegerArgumentType.getInteger(context, "z");

                                                    Mod_quoicoubaka.SpawnConfig.setSpawnPosition(x, y, z);

                                                    context.getSource().sendSuccess(() -> Component.literal("Spawn défini en " + x + ", " + y + ", " + z), true);

                                                    return 1;
                                                }))))
        );
    }
}
