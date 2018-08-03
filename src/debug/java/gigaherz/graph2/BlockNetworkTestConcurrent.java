package gigaherz.graph2;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNetworkTestConcurrent extends BlockRegistered
{
    public BlockNetworkTestConcurrent(String name)
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
        return new TileNetworkTestConcurrent();
    }

    @Deprecated
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        TileNetworkTestConcurrent teSelf = (TileNetworkTestConcurrent) worldIn.getTileEntity(pos);
        teSelf.updateNeighbours();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(world, pos, neighbor);
        TileNetworkTestConcurrent teSelf = (TileNetworkTestConcurrent) world.getTileEntity(pos);
        teSelf.updateNeighbours();
    }

}
