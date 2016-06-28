package gigaherz.graph.api.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNetworkTest extends BlockRegistered
{
    public BlockNetworkTest(String name)
    {
        super(name, Material.WOOD);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileNetworkTest();
    }

    @Deprecated
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        super.neighborChanged(state, worldIn, pos, blockIn);
        TileNetworkTest teSelf = (TileNetworkTest)worldIn.getTileEntity(pos);
        teSelf.updateNeighbours();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(world, pos, neighbor);
        TileNetworkTest teSelf = (TileNetworkTest)world.getTileEntity(pos);
        teSelf.updateNeighbours();
    }

    /*
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity teSelf = worldIn.getTileEntity(pos);
        IGraphThing thingSelf = ((TileNetworkTest)teSelf).getNetworkHandler();

        List<Graph> otherGraphs = Lists.newArrayList();

        List<IGraphThing> neighbours = Lists.newArrayList();
        for(EnumFacing f : EnumFacing.VALUES)
        {
            TileEntity teOther = worldIn.getTileEntity(pos.offset(f));
            if(!(teOther instanceof TileNetworkTest))
                continue;
            IGraphThing thingOther = ((TileNetworkTest)teOther).getNetworkHandler();
            neighbours.add(thingOther);
        }

        Graph target;
        if(otherGraphs.size() > 0)
        {
            target = otherGraphs.get(0);
        }
        else
        {
            target = new Graph();
        }

        target.addThing(thingSelf, neighbours);
    }
*/
}
