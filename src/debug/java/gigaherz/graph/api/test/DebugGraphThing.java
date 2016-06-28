package gigaherz.graph.api.test;

import gigaherz.graph.api.Graph;
import gigaherz.graph.api.IGraphThing;

import javax.vecmath.Vector3d;

public class DebugGraphThing implements IGraphThing
{
    private static int lastUid = 0;
    private final int uid = ++lastUid;

    private Graph graph = null;
    private Vector3d position = null;

    @Override
    public Graph getGraph()
    {
        return graph;
    }

    @Override
    public  void setGraph(Graph g)
    {
        graph = g;
    }

    public Vector3d getPosition()
    {
        return position;
    }

    public void setPosition(Vector3d position)
    {
        this.position = position;
    }

    public int getUid()
    {
        return uid;
    }
}
