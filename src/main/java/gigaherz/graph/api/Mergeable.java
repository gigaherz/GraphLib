package gigaherz.graph.api;

public interface Mergeable<T extends Mergeable>
{
    T mergeWith(T other);
    T copy();
}
