package gigaherz.graph.api.test;

import com.google.common.collect.Lists;
import gigaherz.graph.api.Graph;
import gigaherz.graph.api.IGraphThing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.vecmath.Vector3d;
import java.util.List;

public class TileNetworkTest extends TileEntity implements ITickable
{
    DebugGraphThing networkHandler = new DebugGraphThing();

    public IGraphThing getNetworkHandler() { return networkHandler; }

    boolean firstUpdate = true;

    @Override
    public void validate()
    {
        super.validate();

        if(!firstUpdate)
            init();
    }

    @Override
    public void update()
    {
        if(firstUpdate)
        {
            firstUpdate = false;
            init();
        }
    }

    private void init()
    {
        List<IGraphThing> neighbours = Lists.newArrayList();
        for(EnumFacing f : EnumFacing.VALUES)
        {
            TileEntity teOther = worldObj.getTileEntity(pos.offset(f));
            if(!(teOther instanceof TileNetworkTest))
                continue;
            IGraphThing thingOther = ((TileNetworkTest)teOther).getNetworkHandler();
            if(thingOther.getGraph() != null)
                neighbours.add(thingOther);
        }

        Graph.integrate(networkHandler, neighbours);

        networkHandler.setPosition(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        Graph graph = networkHandler.getGraph();
        if (graph != null)
            graph.removeThing(networkHandler);
    }

    public void updateNeighbours()
    {
        Graph graph = networkHandler.getGraph();
        if (graph != null)
        {
            List<IGraphThing> neighbours = Lists.newArrayList();
            for (EnumFacing f : EnumFacing.VALUES)
            {
                TileEntity teOther = worldObj.getTileEntity(pos.offset(f));
                if (!(teOther instanceof TileNetworkTest))
                    continue;
                IGraphThing thingOther = ((TileNetworkTest) teOther).getNetworkHandler();
                if(thingOther.getGraph() != null)
                    neighbours.add(thingOther);
            }

            graph.addNeighours(networkHandler, neighbours);
        }
    }
}
