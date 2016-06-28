package com.example.examplemod;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
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
        FMLInterModComms.sendMessage("Waila", "register", "com.example.examplemod.WailaProviders.callbackRegister");
    }
}
