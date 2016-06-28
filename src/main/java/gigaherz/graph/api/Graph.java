package gigaherz.graph.api;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Collectors;

public class Graph
{
    private static int lastUid = 0;
    private final int graphUid = ++lastUid;

    int getGraphUid()
    {
        return graphUid;
    }

    final Set<Node> nodeList = Sets.newHashSet();
    final Multimap<Node, Node> neighbours = HashMultimap.create();
    final Multimap<Node, Node> reverseNeighbours = HashMultimap.create();
    final Map<IGraphThing, Node> things = Maps.newHashMap();

    public void addThing(IGraphThing thing, Iterable<IGraphThing> neighbours)
    {
        if (thing.getGraph() != null)
            throw new IllegalArgumentException("The thing is already in another graph.");

        if (things.containsKey(thing))
            throw new IllegalStateException("The thing is already in this graph.");

        Node node = new Node(this, thing);

        thing.setGraph(this);
        things.put(thing, node);

        nodeList.add(node);

        verify();

        addNeighours(thing, neighbours);
    }

    public void addNeighours(IGraphThing thing, Iterable<IGraphThing> others)
    {
        Node node = things.get(thing);
        for (IGraphThing neighbour : others)
        {
            Graph g = neighbour.getGraph();

            if (g == null)
                throw new IllegalArgumentException("The neighbour thing is not in a graph.");

            if (g != this)
                mergeWith(g);

            if (neighbour.getGraph() != this)
                throw new IllegalStateException("The graph merging didn't work as expected.");

            Node n = things.get(neighbour);

            neighbours.put(node, n);
            reverseNeighbours.put(n, node);
        }

        verify();
    }

    public void addNeighbour(IGraphThing thing, IGraphThing neighbour)
    {
        Node node = things.get(thing);
        Node n = things.get(neighbour);

        neighbours.put(node, n);
        reverseNeighbours.put(n, node);

        verify();
    }

    public void removeNeighbour(IGraphThing thing, IGraphThing neighbour)
    {
        Node node = things.get(thing);
        Node other = things.get(neighbour);

        neighbours.remove(node, other);
        reverseNeighbours.remove(other, node);

        verify();

        splitAfterRemoval();
    }

    public void removeThing(IGraphThing thing)
    {
        if (thing.getGraph() != this)
            throw new IllegalArgumentException("The thing is not of this graph.");

        thing.setGraph(null);

        Node node = things.get(thing);
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

        things.remove(thing);

        verify();

        splitAfterRemoval();
    }

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
                this.things.remove(c.getThing());
                newGraph.things.put(c.getThing(), c);
                c.owner = newGraph;
                c.getThing().setGraph(newGraph);
            }

            verify();
        }

        verify();
    }

    private void mergeWith(Graph graph)
    {
        nodeList.addAll(graph.nodeList);
        things.putAll(graph.things);
        neighbours.putAll(graph.neighbours);
        reverseNeighbours.putAll(graph.reverseNeighbours);

        for (Node n : graph.nodeList)
        { n.getThing().setGraph(this); }

        verify();
    }

    public static void integrate(IGraphThing thing, List<IGraphThing> neighbours)
    {
        Set<Graph> otherGraphs = Sets.newHashSet();

        for (IGraphThing neighbour : neighbours)
        {
            Graph otherGraph = neighbour.getGraph();
            if (otherGraph != null && !otherGraphs.contains(otherGraph))
                otherGraphs.add(otherGraph);
        }

        Graph target;
        if (otherGraphs.size() > 0)
            target = otherGraphs.iterator().next();
        else
            target = new Graph();

        target.addThing(thing, neighbours);
    }

    public Collection<IGraphThing> getThings()
    {
        return things.keySet();
    }

    public Collection<IGraphThing> getNeighbours(IGraphThing thing)
    {
        return neighbours.get(things.get(thing)).stream()
                .map(Node::getThing)
                .collect(Collectors.toSet());
    }

    public boolean containsThing(IGraphThing other)
    {
        Node node = things.get(other);
        return node != null && nodeList.contains(node);
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

            if (!things.containsKey(node.getThing()))
            {
                throw new IllegalStateException("Graph is broken!");
            }
        }

        for (Node other : things.values())
        {
            if (!nodeList.contains(other))
            {
                throw new IllegalStateException("Graph is broken!");
            }
        }
    }
}
