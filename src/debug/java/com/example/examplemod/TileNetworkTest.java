package com.example.examplemod;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gigaherz.graph.api.Graph;
import gigaherz.graph.api.GraphThing;
import gigaherz.graph.api.IGraphThing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import java.util.List;
import java.util.Set;

public class TileNetworkTest extends TileEntity implements ITickable
{
    IGraphThing networkHandler = new GraphThing();

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
