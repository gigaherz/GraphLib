package gigaherz.graph.api;

public class GraphThing implements IGraphThing
{
    private Graph graph = null;

    @Override
    public Graph getGraph() { return graph; }

    @Override
    public  void setGraph(Graph g) { graph = g; }
}
