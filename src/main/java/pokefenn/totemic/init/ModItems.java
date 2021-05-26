package pokefenn.totemic.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;
import pokefenn.totemic.Totemic;
import pokefenn.totemic.item.music.ItemFlute;
import pokefenn.totemic.item.music.ItemInfusedFlute;

@ObjectHolder(Totemic.MOD_ID)
public final class ModItems {
    public static final ItemFlute flute = null;
    public static final ItemInfusedFlute infused_flute = null;

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(new ItemFlute(new Properties().stacksTo(1).tab(Totemic.itemGroup)).setRegistryName("flute"),
                new ItemInfusedFlute(new Properties().stacksTo(1).tab(Totemic.itemGroup)).setRegistryName("infused_flute"));

        for(Block block: ModBlocks.getBlocksWithItemBlock())
            event.getRegistry().register(makeItemBlock(block));
    }

    private static Item makeItemBlock(Block block) {
        return new BlockItem(block, new Properties().tab(Totemic.itemGroup)).setRegistryName(block.getRegistryName());
    }
}
