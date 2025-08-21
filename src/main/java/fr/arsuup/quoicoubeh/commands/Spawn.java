package fr.arsuup.quoicoubeh.commands;

import com.mojang.brigadier.CommandDispatcher;
import fr.arsuup.quoicoubeh.Mod_quoicoubaka;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class Spawn {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("spawn")
                        .requires(source -> source.hasPermission(0))
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrException();
                            Mod_quoicoubaka.SpawnConfig.SpawnPosition pos = Mod_quoicoubaka.SpawnConfig.getSpawnPosition();

                            if (pos == null) {
                                source.sendFailure(Component.literal("Le spawn n'a pas encore été défini !"));
                                return 0;
                            }

                            player.teleportTo(pos.x + 0.5, pos.y, pos.z + 0.5);
                            source.sendSuccess(() -> Component.literal("Téléporté au spawn !"), false);

                            return 1;
                        })
        );
    }
}
