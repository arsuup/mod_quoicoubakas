package fr.arsuup.quoicoubeh;

import fr.arsuup.quoicoubeh.commands.Cameraman;
import fr.arsuup.quoicoubeh.commands.Setspawn;
import fr.arsuup.quoicoubeh.commands.Spawn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Mod_quoicoubaka.MODID)
public class Mod_quoicoubaka {
    public static final String MODID = "mod_quoicoubaka";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mod_quoicoubaka"))
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
                output.accept(EXAMPLE_BLOCK.get());
            }).build());

    public Mod_quoicoubaka(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new TickHandler());
    }

    public class SpawnConfig {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private static final File CONFIG_FILE = new File("config/spawn.json");

        public static class SpawnPosition {
            public double x, y, z;

            public SpawnPosition() {}

            public SpawnPosition(double x, double y, double z) {
                this.x = x; this.y = y; this.z = z;
            }
        }

        private static SpawnPosition spawnPosition;

        public static SpawnPosition getSpawnPosition() {
            if (spawnPosition == null) load();
            return spawnPosition;
        }

        public static void setSpawnPosition(double x, double y, double z) {
            spawnPosition = new SpawnPosition(x, y, z);
            save();
        }

        public static void load() {
            if (!CONFIG_FILE.exists()) {
                spawnPosition = null;
                return;
            }
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                spawnPosition = GSON.fromJson(reader, SpawnPosition.class);
            } catch (IOException | JsonSyntaxException e) {
                spawnPosition = null;
            }
        }

        public static void save() {
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(spawnPosition, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Cameraman.register(event.getServer().getCommands().getDispatcher());
        Setspawn.register(event.getServer().getCommands().getDispatcher());
        Spawn.register(event.getServer().getCommands().getDispatcher());
    }

    public static class TickHandler {

        @SubscribeEvent
        public void onServerTick(ServerTickEvent.Post event) {
            MinecraftServer server = event.getServer();

            server.getPlayerList().getPlayers().forEach(player -> {
                String playerName = player.getName().getString();
                ServerLevel level = (ServerLevel) player.level();

                var source = server.createCommandSourceStack()
                        .withLevel(level)
                        .withSuppressedOutput();

                server.getCommands().performPrefixedCommand(
                        source,
                        "execute as @e[type=goatduloat:crazy_cameraman,name=" + playerName + "] at " + playerName + " run tp ~ ~-1.3 ~"
                );
            });
        }
    }
}
