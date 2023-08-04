package pokefenn.totemic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import pokefenn.totemic.advancements.ModCriteriaTriggers;
import pokefenn.totemic.api.TotemicAPI;
import pokefenn.totemic.api.music.MusicAcceptor;
import pokefenn.totemic.apiimpl.registry.RegistryApiImpl;
import pokefenn.totemic.client.CeremonyHUD;
import pokefenn.totemic.client.ModModelLayers;
import pokefenn.totemic.data.TotemicBlockStateProvider;
import pokefenn.totemic.data.TotemicBlockTagsProvider;
import pokefenn.totemic.data.TotemicEntityTypeTagsProvider;
import pokefenn.totemic.data.TotemicItemTagsProvider;
import pokefenn.totemic.data.TotemicLootTableProvider;
import pokefenn.totemic.data.TotemicRecipeProvider;
import pokefenn.totemic.handler.ClientInitHandlers;
import pokefenn.totemic.handler.ClientInteract;
import pokefenn.totemic.handler.ClientRenderHandler;
import pokefenn.totemic.handler.PlayerInteract;
import pokefenn.totemic.init.ModBlockEntities;
import pokefenn.totemic.init.ModBlocks;
import pokefenn.totemic.init.ModContent;
import pokefenn.totemic.init.ModEntityTypes;
import pokefenn.totemic.init.ModItems;
import pokefenn.totemic.init.ModMobEffects;
import pokefenn.totemic.init.ModSounds;
import pokefenn.totemic.item.music.JingleDressItem;
import pokefenn.totemic.network.NetworkHandler;

@Mod(TotemicAPI.MOD_ID)
public final class Totemic {
    public static final Logger logger = LogManager.getLogger(Totemic.class);

    public static final CreativeModeTab creativeTab = new CreativeModeTab(TotemicAPI.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.tipi.get());
        }
    };

    public Totemic() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(this::gatherData);

        ModBlocks.REGISTER.register(modBus);
        ModItems.REGISTER.register(modBus);
        ModMobEffects.REGISTER.register(modBus);
        ModBlockEntities.REGISTER.register(modBus);
        ModEntityTypes.REGISTER.register(modBus);
        ModSounds.REGISTER.register(modBus);

        modBus.register(ModBlocks.class);
        modBus.register(ModItems.class);
        modBus.register(ModEntityTypes.class);
        modBus.register(RegistryApiImpl.class);
        modBus.register(ModContent.class);

        if(FMLEnvironment.dist.isClient()) {
            modBus.addListener(this::clientSetup);

            modBus.register(ClientInitHandlers.class);
            modBus.register(ModModelLayers.class);
        }

        ModConfig.register(ModLoadingContext.get());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModBlocks.setFireInfo();
            ModBlocks.addCedarSignToSignBlockEntityType();
            ModCriteriaTriggers.init();
        });

        NetworkHandler.init();
        //PatchouliIntegration.init();

        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(PlayerInteract.class);
    }

    @SuppressWarnings("null")
    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModItems.baykok_bow.get().registerItemProperties();
            ModItems.medicine_bag.get().registerItemProperties();
            Sheets.addWoodType(ModBlocks.CEDAR_WOOD_TYPE);
        });

        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(ClientInteract.class);
        eventBus.register(ClientRenderHandler.class);

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.cedar_sapling.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.potted_cedar_sapling.get(), RenderType.cutout());

        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "ceremony_hud", CeremonyHUD.INSTANCE);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(MusicAcceptor.class);
        event.register(JingleDressItem.ChargeCounter.class);
    }

    private void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var efh = event.getExistingFileHelper();
        if(event.includeServer()) {
            var blockTP = new TotemicBlockTagsProvider(gen, efh);
            gen.addProvider(blockTP);
            gen.addProvider(new TotemicItemTagsProvider(gen, blockTP, TotemicAPI.MOD_ID, efh));
            gen.addProvider(new TotemicEntityTypeTagsProvider(gen, TotemicAPI.MOD_ID, efh));
            gen.addProvider(new TotemicLootTableProvider(gen));
            gen.addProvider(new TotemicRecipeProvider(gen));
        }
        if(event.includeClient()) {
            gen.addProvider(new TotemicBlockStateProvider(gen, efh));
        }
    }

    public static ResourceLocation resloc(String path) {
        return new ResourceLocation(TotemicAPI.MOD_ID, path);
    }
}
