package net.vanishmentor.tacz_engineer_asset_pack;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.vanishmentor.tacz_engineer_asset_pack.blocks.BlockRegistryHandler;
import net.vanishmentor.tacz_engineer_asset_pack.fluids.FluidRegistryHandler;
import net.vanishmentor.tacz_engineer_asset_pack.fluids.FluidTypeRegistryHandler;
import net.vanishmentor.tacz_engineer_asset_pack.items.ModCreativeModeTabs;
import net.vanishmentor.tacz_engineer_asset_pack.items.ItemRegistryHandler;
import net.vanishmentor.tacz_engineer_asset_pack.mobEffects.MobEffectRegistryHandler;
import org.slf4j.Logger;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.PathPackResources;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TaczEngineerAssetPack.MOD_ID)
public class TaczEngineerAssetPack {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "tacz_engineer_asset_pack";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public TaczEngineerAssetPack(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModCreativeModeTabs.register(modEventBus);
        ItemRegistryHandler.register(modEventBus);
        FluidTypeRegistryHandler.register(modEventBus);
        BlockRegistryHandler.register(modEventBus);
        FluidRegistryHandler.register(modEventBus);
        MobEffectRegistryHandler.register(modEventBus);

        // Resource pack registry
        modEventBus.addListener(this::addPackFinders);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
        var resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("resourcepacks/teap_override");
        var pack = Pack.readMetaAndCreate("builtin/add_pack_finders_test", Component.literal("TACZ IE Lang Override"), true,
                (path) -> new PathPackResources(path, resourcePath, true), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
        if (pack != null) {
            event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(FluidRegistryHandler.SOURCE_NITRIC_ACID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidRegistryHandler.FLOWING_NITRIC_ACID.get(), RenderType.translucent());
        }
    }

}


