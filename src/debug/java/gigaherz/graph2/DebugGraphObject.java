package gigaherz.graph2;

import javax.vecmath.Vector3d;

public class DebugGraphObject implements GraphObject
{
    private Graph graph = null;
    private Vector3d position = null;

    @Override
    public Graph getGraph()
    {
        return graph;
    }

    @Override
    public void setGraph(Graph g)
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
}
