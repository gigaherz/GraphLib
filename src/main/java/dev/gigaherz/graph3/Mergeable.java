package dev.gigaherz.graph3;

public interface Mergeable<T extends Mergeable<T>>
{
    T mergeWith(T other);
    T splitFor(Graph<T> selfGraph, Graph<T> otherGraph);

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
        public Dummy splitFor(Graph<Dummy> selfGraph, Graph<Dummy> otherGraph) {
            return this;
        }
    }
}
