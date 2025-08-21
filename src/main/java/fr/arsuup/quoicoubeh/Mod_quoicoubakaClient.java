package fr.arsuup.quoicoubeh;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = Mod_quoicoubaka.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Mod_quoicoubaka.MODID, value = Dist.CLIENT)
public class Mod_quoicoubakaClient {
    public Mod_quoicoubakaClient(ModContainer container) {
        
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }
}
