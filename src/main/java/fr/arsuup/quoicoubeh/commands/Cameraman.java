package fr.arsuup.quoicoubeh.commands;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;

public class Cameraman {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("cameraman")
                .requires(source -> source.hasPermission(4))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    ServerLevel level = source.getLevel();
                    Vec3 pos = source.getPosition();
                    String playerName = source.getPlayerOrException().getName().getString();
                    ServerPlayer player = source.getPlayerOrException();

                    ResourceLocation id = ResourceLocation.tryParse("goatduloat:crazy_cameraman");
                    if (id == null) {
                        source.sendFailure(Component.literal("Identifiant d'entité invalide."));
                        return 0;
                    }

                    EntityType<?> crazyType = BuiltInRegistries.ENTITY_TYPE.get(id);
                    if (crazyType == null) {
                        source.sendFailure(Component.literal("Nécessite le mod \"goatduloat\" (" + id + ")"));
                        return 0;
                    }

                    List<? extends Entity> existing = level.getEntities(crazyType, entity ->
                            playerName.equals(entity.getName().getString())
                    );

                    if (existing.isEmpty()) {
                        Entity entity = crazyType.create(level);
                        if (entity == null) {
                            source.sendFailure(Component.literal("Erreur A1."));
                            return 0;
                        }

                        entity.setCustomName(Component.literal(playerName));
                        entity.setCustomNameVisible(false);
                        entity.setPos(pos.x, pos.y, pos.z);
                        entity.setNoGravity(true);
                        entity.setInvulnerable(true);

                        level.addFreshEntity(entity);
                        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION, 255, false, false));
                        source.sendSuccess(() -> Component.literal("Cameraman créé pour " + playerName), false);

                        level.getServer().getCommands().performPrefixedCommand(
                                source.withSuppressedOutput(),
                                "team join NoCollisions @e[type=goatduloat:crazy_cameraman,name=" + playerName + "]"
                        );

                    } else {
                        for (Entity entity : existing) {entity.discard();}
                        player.removeEffect(MobEffects.INVISIBILITY);
                        source.sendSuccess(() -> Component.literal("Cameraman supprimé pour " + playerName), false);
                    }

                    return 1;
                })
        );
    }
}