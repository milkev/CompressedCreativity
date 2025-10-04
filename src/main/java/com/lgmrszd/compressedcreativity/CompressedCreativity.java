package com.lgmrszd.compressedcreativity;

import com.lgmrszd.compressedcreativity.index.*;
import com.lgmrszd.compressedcreativity.index.CCItems;
import com.lgmrszd.compressedcreativity.index.recipe.CCSequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CompressedCreativity.MOD_ID)
public class CompressedCreativity
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "compressedcreativity";

//    private static final NonNullSupplier<CreateRegistrate> registrate = CreateRegistrate.lazy(CompressedCreativity.MOD_ID);
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CompressedCreativity.MOD_ID);


    public CompressedCreativity() {

        IEventBus eventBus = NeoForge.EVENT_BUS;
        REGISTRATE.registerEventListeners(eventBus);

        CCConfigHelper.init();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);
        if( FMLLoader.getDist() == Dist.CLIENT) {
                CCBlockPartials.init();
        }

        eventBus.addListener(this::postInit);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::serverStart);

        CCCreativeTabs.register(eventBus);
        CCItems.register(eventBus);
        CCBlocks.register();
        CCBlockEntities.register();

//        CCUpgrades.UPGRADES_DEFERRED.register(eventBus);
        CCUpgrades.init();



        eventBus.addListener(EventPriority.LOWEST, CompressedCreativity::gatherData);
    }


    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        CCCommonSetup.init(event);
//        CCHeatBehaviour.init(event);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        CCClientSetup.init(event);
        PonderIndex.addPlugin(new CCPonderPlugin());
    }

    private void serverStart(final ServerAboutToStartEvent event) {
//        CCHeatBehaviour.registerHeatBehaviour();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
//        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
    }


    public void postInit(FMLLoadCompleteEvent evt) {

    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            PonderIndex.addPlugin(new CCPonderPlugin());
            PonderIndex.getLangAccess().provideLang(MOD_ID, provider::add);
        });

        CCLangExtender.ExtendLang(REGISTRATE);
        gen.addProvider(true, new CCSequencedAssemblyRecipeGen(output));
    }
}
