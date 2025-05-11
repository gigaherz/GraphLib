package dev.gigaherz.graph3;

import java.util.Collection;

public interface Mergeable<T extends Mergeable<T>>
{
    /**
     * Called when two graphs become joined by a new node or edge.
     * @param other The data stored in the other graph.
     * @return The data to be stored in the graph after the two graphs are combined.
     */
    T mergeWith(T other);

    /**
     * Makes a copy of the mergeable data object.
     * For data objects which contain an identifier, the copy might contain a brand-new identifier.
     * @return The copy.
     */
    T copy();

    /**
     * Allows attached data to customize which data goes to each subgraph after a split.
     * By default, it copies the data to each new graph.
     * @param self The instance of the data object (same as {@code this} but without needing an unsafe cast).
     * @param original The graph this mergeable is attached to.
     * @param newGraphs The graphs corresponding to each additional subgraph.
     */
    default void setContextAfterSplit(T self, Graph<T> original, Collection<Graph<T>> newGraphs) {
        for(Graph<T> graph : newGraphs)
        {
            graph.setContextData(copy());
        }
    }

    @PublicApi
    Dummy DUMMY = new Dummy();

    class Dummy implements Mergeable<Dummy>
    {
        private Dummy(){}

        @Override
        public Dummy mergeWith(Dummy other)
        {
            return this;
        }

        @Override
        public Dummy copy() {
            return this;
        }
    }
}
