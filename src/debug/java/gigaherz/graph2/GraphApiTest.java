package gigaherz.graph2;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
@Mod(modid = GraphApiTest.MODID, version = GraphApiTest.VERSION)
public class GraphApiTest
{
    public static final String MODID = "graphlibtest";
    public static final String VERSION = Constants.API_VERSION;

    public static BlockRegistered networkTest;

    public static BlockRegistered networkTestConcurrent;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.MissingMappings<Block> event)
    {
        for(RegistryEvent.MissingMappings.Mapping mapping : event.getMappings())
            if (mapping.key.equals(new ResourceLocation(MODID, "blocknetworktest")))
                mapping.remap(networkTest);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                networkTest = new BlockNetworkTest("block_network_test"),
                networkTestConcurrent = new BlockNetworkTestConcurrent("block_network_test_concurrent")
        );

        GameRegistry.registerTileEntity(TileNetworkTest.class, networkTest.getRegistryName());
        GameRegistry.registerTileEntity(TileNetworkTestConcurrent.class, networkTestConcurrent.getRegistryName());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                networkTest.createItemBlock(),
                networkTestConcurrent.createItemBlock()
        );
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLInterModComms.sendMessage("waila", "register", "WailaProviders.callbackRegister");
    }
}
