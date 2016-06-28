package gigaherz.graph.api;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = GraphApiTest.MODID, version = GraphApiTest.VERSION)
public class GraphApiTest
{
    public static final String MODID = "GraphLib";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "ClientProxy", serverSide = "Proxy")
    public static Proxy proxy;

    public static BlockRegistered networkTest;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        networkTest = new BlockNetworkTest("blockNetworkTest");
        GameRegistry.register(networkTest);
        GameRegistry.register(networkTest.createItemBlock());
        GameRegistry.registerTileEntity(TileNetworkTest.class, "tileNetworkTest");

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();

        FMLInterModComms.sendMessage("Waila", "register", "WailaProviders.callbackRegister");
    }
}
