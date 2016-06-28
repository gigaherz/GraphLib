package gigaherz.graph.api.test;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = GraphApiTest.MODID, version = GraphApiTest.VERSION)
public class GraphApiTest
{
    public static final String MODID = "GraphLib";
    public static final String VERSION = "1.0";

    public static BlockRegistered networkTest;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        networkTest = new BlockNetworkTest("blockNetworkTest");
        GameRegistry.register(networkTest);
        GameRegistry.register(networkTest.createItemBlock());
        GameRegistry.registerTileEntity(TileNetworkTest.class, "tileNetworkTest");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLInterModComms.sendMessage("Waila", "register", "WailaProviders.callbackRegister");
    }
}
