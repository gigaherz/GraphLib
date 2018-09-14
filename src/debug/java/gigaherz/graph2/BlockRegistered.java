package gigaherz.graph2;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public class BlockRegistered extends Block
{

    public BlockRegistered(String name, Material materialIn)
    {
        super(materialIn);
        setRegistryName(name);
        setTranslationKey(GraphApiTest.MODID + "." + name);
    }

    public ItemBlock createItemBlock()
    {
        return (ItemBlock) new ItemBlock(this).setRegistryName(getRegistryName());
    }
}
