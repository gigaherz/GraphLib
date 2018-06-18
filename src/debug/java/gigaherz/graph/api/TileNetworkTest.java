package gigaherz.graph.api;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.vecmath.Vector3d;
import java.util.List;

public class TileNetworkTest extends TileEntity implements ITickable
{
    DebugGraphObject networkHandler = new DebugGraphObject();

    public GraphObject getNetworkHandler()
    {
        return networkHandler;
    }

    boolean firstUpdate = true;

    @Override
    public void validate()
    {
        super.validate();

        if (!firstUpdate)
            init();
    }

    @Override
    public void update()
    {
        if (firstUpdate)
        {
            firstUpdate = false;
            init();
        }
    }

    private void init()
    {
        Graph.integrate(networkHandler, getNeighbours(), (graph) -> new GraphData());

        networkHandler.setPosition(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    public int getSharedUid()
    {
        return this.networkHandler.getGraph().<GraphData>getContextData().getUid();
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        Graph graph = networkHandler.getGraph();
        if (graph != null)
            graph.remove(networkHandler);
    }

    private List<GraphObject> getNeighbours()
    {
        List<GraphObject> neighbours = Lists.newArrayList();
        for (EnumFacing f : EnumFacing.VALUES)
        {
            TileEntity teOther = world.getTileEntity(pos.offset(f));
            if (!(teOther instanceof TileNetworkTest))
                continue;
            GraphObject thingOther = ((TileNetworkTest) teOther).getNetworkHandler();
            if (thingOther.getGraph() != null)
                neighbours.add(thingOther);
        }
        return neighbours;
    }

    public void updateNeighbours()
    {
        Graph graph = networkHandler.getGraph();
        if (graph != null)
        {
            graph.addNeighours(networkHandler, getNeighbours());
        }
    }

    public static class GraphData implements Mergeable<GraphData>
    {
        private static int sUid = 0;

        private final int uid;

        public GraphData()
        {
            uid = ++sUid;
        }

        public GraphData(int uid)
        {
            this.uid = uid;
        }

        @Override
        public GraphData mergeWith(GraphData other)
        {
            return new GraphData(uid + other.uid);
        }

        @Override
        public GraphData copy()
        {
            return new GraphData();
        }

        public int getUid()
        {
            return uid;
        }
    }
}
