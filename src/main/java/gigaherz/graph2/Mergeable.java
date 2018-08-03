package gigaherz.graph2;

public interface Mergeable<T extends Mergeable>
{
    T mergeWith(T other);
    T copy();
}
