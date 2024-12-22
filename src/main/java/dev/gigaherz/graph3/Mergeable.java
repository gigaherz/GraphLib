package dev.gigaherz.graph3;

public interface Mergeable<T extends Mergeable<T>>
{
    T mergeWith(T other);
    T copy();

    /**
     * Split the context into multiple parts when a graph is split.
     * @param selfNodeCount The number of nodes in this part of the graph.
     * @param totalNodeCount The number of nodes in the graph before the split.
     * @param graphIndex This graph's index in the split (0 - `graphCount`)
     * @param graphCount The number of graphs that have been created as a result of the split.
     * @return A new context that represents the split part of the graph.
     */
    default T splitFor(int selfNodeCount, int totalNodeCount, int graphIndex, int graphCount) {
        return copy();
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
