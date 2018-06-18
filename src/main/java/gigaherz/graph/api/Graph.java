package gigaherz.graph.api;

import com.google.common.collect.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Graph
{
    // Graph data
    private final Set<Node> nodeList = Sets.newHashSet();
    private final Multimap<Node, Node> neighbours = HashMultimap.create();
    private final Multimap<Node, Node> reverseNeighbours = HashMultimap.create();
    private final Map<GraphObject, Node> objects = Maps.newHashMap();

    /**
     * Contains an object attached to the graph.
     * Persisting this information is the responsibility of the user.
     */
    private Mergeable contextData;

    /**
     * Returns the assigned context object.
     * @param <T> The expected type of the contained data
     * @return
     */
    @SuppressWarnings("unchecked")
    @PublicApi
    public <T extends Mergeable<T>> T getContextData()
    {
        return (T)contextData;
    }

    /**
     * Assigns a context object attached to the graph.
     * Persisting this information is the responsibility of the user.
     * @param contextData The context object
     */
    @PublicApi
    public void setContextData(Mergeable contextData)
    {
        this.contextData = contextData;
    }

    /**
     * Adds the object to a graph. It will reuse the neighbours' graph,
     * or create a new one if none found.
     * @param object The object to add into a graph
     * @param neighbours The neighbours it will connect to (directed)
     */
    @PublicApi
    public static void integrate(GraphObject object, List<GraphObject> neighbours)
    {
        integrate(object, neighbours, null);
    }

    /**
     * Adds the object to a graph. It will reuse the neighbours' graph,
     * or create a new one if none found.
     * @param object The object to add into a graph
     * @param neighbours The neighbours it will connect to (directed)
     * @param contextDataFactory A provider for the shared data object contained in the graph
     */
    @PublicApi
    public static void integrate(GraphObject object, List<GraphObject> neighbours, @Nullable ContextDataFactory contextDataFactory)
    {
        Set<Graph> otherGraphs = Sets.newHashSet();

        for (GraphObject neighbour : neighbours)
        {
            Graph otherGraph = neighbour.getGraph();
            if (otherGraph != null && !otherGraphs.contains(otherGraph))
                otherGraphs.add(otherGraph);
        }

        Graph target;
        if (otherGraphs.size() > 0)
        {
            target = otherGraphs.iterator().next();
        }
        else
        {
            target = new Graph();
            if (contextDataFactory != null)
                target.contextData = contextDataFactory.create(target);
        }

        target.add(object, neighbours);
    }

    /**
     * Adds an object to the graph, along with some directed edges.
     * @param object The object to add.
     * @param neighbours The objects the edges point toward.
     */
    @PublicApi
    public void add(GraphObject object, Iterable<GraphObject> neighbours)
    {
        if (object.getGraph() != null)
            throw new IllegalArgumentException("The object is already in another graph.");

        if (objects.containsKey(object))
            throw new IllegalStateException("The object is already in this graph.");

        Node node = new Node(this, object);

        object.setGraph(this);
        objects.put(object, node);

        nodeList.add(node);

        verify();

        addNeighours(object, neighbours);
    }

    /**
     * Adds some directed edges to a node.
     * @param object The object the edge originates from.
     * @param neighbours The objects the edges point toward.
     */
    @PublicApi
    public void addNeighours(GraphObject object, Iterable<GraphObject> neighbours)
    {
        Node node = objects.get(object);
        for (GraphObject neighbour : neighbours)
        {
            Graph g = neighbour.getGraph();

            if (g == null)
                throw new IllegalArgumentException("The neighbour object is not in a graph.");

            if (g != this)
                mergeWith(g);

            if (neighbour.getGraph() != this)
                throw new IllegalStateException("The graph merging didn't work as expected.");

            Node n = objects.get(neighbour);

            this.neighbours.put(node, n);
            reverseNeighbours.put(n, node);
        }

        verify();
    }

    /**
     * Adds a single directed edge.
     * @param object The object the edge originates from.
     * @param neighbour The object the edge points toward.
     */
    @PublicApi
    public void addNeighbour(GraphObject object, GraphObject neighbour)
    {
        Node node = objects.get(object);
        Node n = objects.get(neighbour);

        neighbours.put(node, n);
        reverseNeighbours.put(n, node);

        verify();
    }

    /**
     * Removes a single directed edge, if it exists..
     * @param object The object the edge originates from.
     * @param neighbour The object the edge points toward.
     */
    @PublicApi
    public void removeNeighbour(GraphObject object, GraphObject neighbour)
    {
        Node node = objects.get(object);
        Node other = objects.get(neighbour);

        neighbours.remove(node, other);
        reverseNeighbours.remove(other, node);

        verify();

        splitAfterRemoval();
    }

    /**
     * Removes a node from the graph, along with all the related edges.
     * @param object The object to remove.
     */
    @PublicApi
    public void remove(GraphObject object)
    {
        if (object.getGraph() != this)
            throw new IllegalArgumentException("The object is not of this graph.");

        object.setGraph(null);

        Node node = objects.get(object);
        if (node == null)
            throw new IllegalStateException("The graph is broken.");

        nodeList.remove(node);

        Set<Node> neighs = Sets.newHashSet(neighbours.get(node));
        neighs.addAll(reverseNeighbours.get(node));
        for (Object n : neighs)
        {
            neighbours.remove(n, node);
            reverseNeighbours.remove(node, n);

            neighbours.remove(node, n);
            reverseNeighbours.remove(n, node);
        }

        objects.remove(object);

        verify();

        splitAfterRemoval();
    }

    /**
     * Obtains the list of objects representing the nodes in the graph.
     * @return The objects from the graph.
     */
    @PublicApi
    public Collection<GraphObject> getObjects()
    {
        return ImmutableSet.copyOf(objects.keySet());
    }

    /**
     * Obtains the neighbouring objects that the object connects to.
     * @param object The object for which to get the neighbours.
     * @return The neighbouring objects.
     */
    @PublicApi
    public Collection<GraphObject> getNeighbours(GraphObject object)
    {
        Set<GraphObject> others = Sets.newHashSet();
        for (Node n : neighbours.get(objects.get(object)))
        {
            others.add(n.getObject());
        }
        return ImmutableSet.copyOf(others);
    }

    /**
     * Checks if the given object is part of the graph.
     * @param object The object.
     * @return
     */
    @PublicApi
    public boolean contains(GraphObject object)
    {
        Node node = objects.get(object);
        return node != null && nodeList.contains(node);
    }

    // ##############################################################################
    // ## Private helpers

    private void splitAfterRemoval()
    {
        if (nodeList.size() == 0)
            return;

        Set<Node> remaining = Sets.newHashSet(nodeList);
        Set<Node> seen = Sets.newHashSet();
        Queue<Node> succ = Queues.newArrayDeque();

        Node node = remaining.iterator().next();
        succ.add(node);
        seen.add(node);
        remaining.remove(node);

        // First mark the ones that will remain in this graph
        // so that there are only new graphs created if needed
        while (succ.size() > 0)
        {
            Node c = succ.poll();
            for (Object o : neighbours.get(c))
            {
                Node n = (Node) o;

                if (!seen.contains(n))
                {
                    seen.add(n);
                    succ.add(n);
                    remaining.remove(n);
                }
            }
            for (Object o : reverseNeighbours.get(c))
            {
                Node n = (Node) o;

                if (!seen.contains(n))
                {
                    seen.add(n);
                    succ.add(n);
                    remaining.remove(n);
                }
            }
        }

        // If anything remains unseen, it means it's on a disconnected subgraph
        while (remaining.size() > 0)
        {
            node = remaining.iterator().next();
            succ.add(node);
            seen.add(node);
            remaining.remove(node);

            Graph newGraph = new Graph();
            if (contextData != null)
                newGraph.contextData = contextData.copy();
            while (succ.size() > 0)
            {
                Node c = succ.poll();
                for (Object o : neighbours.get(c))
                {
                    Node n = (Node) o;

                    if (!seen.contains(n))
                    {
                        seen.add(n);
                        succ.add(n);
                        remaining.remove(n);
                    }
                }
                for (Object o : reverseNeighbours.get(c))
                {
                    Node n = (Node) o;

                    if (!seen.contains(n))
                    {
                        seen.add(n);
                        succ.add(n);
                        remaining.remove(n);
                    }
                }

                this.nodeList.remove(c);
                newGraph.nodeList.add(c);
                newGraph.neighbours.putAll(c, neighbours.get(c));
                newGraph.reverseNeighbours.putAll(c, reverseNeighbours.get(c));
                this.neighbours.removeAll(c);
                this.reverseNeighbours.removeAll(c);
                this.objects.remove(c.getObject());
                newGraph.objects.put(c.getObject(), c);
                c.owner = newGraph;
                c.getObject().setGraph(newGraph);
            }

            verify();
        }

        verify();
    }

    private void mergeWith(Graph graph)
    {
        nodeList.addAll(graph.nodeList);
        objects.putAll(graph.objects);
        neighbours.putAll(graph.neighbours);
        reverseNeighbours.putAll(graph.reverseNeighbours);

        for (Node n : graph.nodeList)
        { n.getObject().setGraph(this); }

        if (contextData != null && graph.contextData != null)
            contextData = contextData.mergeWith(graph.contextData);
        else if(graph.contextData != null)
            contextData = graph.contextData;

        verify();
    }

    private void verify()
    {
        for (Node node : nodeList)
        {
            for (Node other : neighbours.get(node))
            {
                if (!nodeList.contains(other))
                {
                    throw new IllegalStateException("Graph is broken!");
                }
            }

            if (!objects.containsKey(node.getObject()))
            {
                throw new IllegalStateException("Graph is broken!");
            }
        }

        for (Node other : objects.values())
        {
            if (!nodeList.contains(other))
            {
                throw new IllegalStateException("Graph is broken!");
            }
        }
    }

    private class Node
    {
        @Nonnull
        Graph owner;

        // Object attached to this node
        final GraphObject object;

        public Graph getOwner()
        {
            return owner;
        }

        public GraphObject getObject()
        {
            return object;
        }

        public Node(Graph owner, GraphObject object)
        {
            this.owner = owner;
            this.object = object;
        }
    }
}
