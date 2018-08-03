package gigaherz.graph2;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentGraph extends Graph
{
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @PublicApi
    public static void connect(GraphObject object1, GraphObject object2)
    {
        connect(object1, object2, null);
    }

    @PublicApi
    public static void connect(GraphObject object1, GraphObject object2, @Nullable ContextDataFactory contextDataFactory)
    {
        connect(object1, object2, ConcurrentGraph::new, contextDataFactory);
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
        integrate(object, neighbours, ConcurrentGraph::new, contextDataFactory);
    }

    /**
     * Returns the assigned context object.
     * @param <T> The expected type of the contained data
     * @return
     */
    @Override
    public <T extends Mergeable<T>> T getContextData()
    {
        readLock.lock();
        try
        {
            return super.getContextData();
        }
        finally
        {
            readLock.unlock();
        }
    }

    @Override
    public void setContextData(Mergeable contextData)
    {
        writeLock.lock();
        try
        {
            super.setContextData(contextData);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public void addNodeAndEdges(GraphObject object, Iterable<GraphObject> neighbours)
    {
        writeLock.lock();
        try
        {
            super.addNodeAndEdges(object, neighbours);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public void addDirectedEdges(GraphObject object, Iterable<GraphObject> neighbours)
    {
        writeLock.lock();
        try
        {
            super.addDirectedEdges(object, neighbours);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public void addSingleEdge(GraphObject object, GraphObject neighbour)
    {
        writeLock.lock();
        try
        {
            super.addSingleEdge(object, neighbour);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public void removeSingleEdge(GraphObject object, GraphObject neighbour)
    {
        writeLock.lock();
        try
        {
            super.removeSingleEdge(object, neighbour);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public void remove(GraphObject object)
    {
        writeLock.lock();
        try
        {
            super.remove(object);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Deprecated
    @Override
    public Collection<GraphObject> getObjects()
    {
        return super.getObjects();
    }

    public Collection<GraphObject> acquireObjects()
    {
        readLock.lock();
        return super.getObjects();
    }

    public void releaseObjects()
    {
        readLock.unlock();
    }

    @Override
    public Collection<GraphObject> getNeighbours(GraphObject object)
    {
        readLock.lock();
        try
        {
            return super.getNeighbours(object);
        }
        finally
        {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(GraphObject object)
    {
        readLock.lock();
        try
        {
            return super.contains(object);
        }
        finally
        {
            readLock.unlock();
        }
    }
}
