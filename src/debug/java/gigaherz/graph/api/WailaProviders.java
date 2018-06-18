package gigaherz.graph.api;

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

    @Optional.Interface(modid = "waila", iface = "mcp.mobius.waila.api.IWailaDataProvider")
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
            NBTTagCompound tag = accessor.getNBTData();
            currenttip.add(String.format("Shared UID: %s", tag.getInteger("sharedUid")));
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
            GraphObject object = network.getNetworkHandler();
            tag.setInteger("sharedUid", object == null ? -1 : object.getGraph().<DebugGraphData>getContextData().getUid());
            return tag;
        }
    }
}
