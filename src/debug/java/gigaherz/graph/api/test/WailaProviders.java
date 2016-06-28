package gigaherz.graph.api.test;

import gigaherz.graph.api.IGraphThing;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.List;

public class WailaProviders
{
    public static void callbackRegister(IWailaRegistrar registrar)
    {
        {
            NetworkTestProvider instance = new NetworkTestProvider();
            registrar.registerBodyProvider(instance, TileNetworkTest.class);
            registrar.registerNBTProvider(instance, TileNetworkTest.class);
        }
    }

    @Optional.Interface(modid = "Waila", iface = "mcp.mobius.waila.api.IWailaDataProvider")
    public static class NetworkTestProvider implements IWailaDataProvider
    {
        @Override
        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            return null;
        }

        @Override
        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            return currenttip;
        }

        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            TileNetworkTest network = (TileNetworkTest) accessor.getTileEntity();
            IGraphThing thing = network.getNetworkHandler();

            NBTTagCompound tag = accessor.getNBTData();

            currenttip.add("The graph ID is only for debugging purposes,");
            currenttip.add("the numbers below are " + TextFormatting.WHITE + "SUPPOSED" + TextFormatting.GRAY + " to be different!");
            currenttip.add(String.format("Client Graph Instance ID: %s", thing == null ? -1 : thing.getGraph().getGraphUid()));
            currenttip.add(String.format("Server Graph Instance ID: %s", tag.getInteger("graphUid")));

            return currenttip;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
        {
            TileNetworkTest network = (TileNetworkTest) te;
            IGraphThing thing = network.getNetworkHandler();

            tag.setInteger("graphUid", thing == null ? -1 : thing.getGraph().getGraphUid());

            return tag;
        }
    }
}
