package gigaherz.graph.api;

import javax.annotation.Nonnull;

public class Node
{
    @Nonnull
    Graph owner;

    // Thing(s) attached to this node
    final IGraphThing thing;

    public Graph getOwner() { return owner; }
    public IGraphThing getThing() { return thing; }

    public Node(Graph owner, IGraphThing thing)
    {
        this.owner = owner;
        this.thing = thing;
    }
}
