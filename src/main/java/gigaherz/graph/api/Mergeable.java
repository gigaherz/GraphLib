package gigaherz.graph.api;

public interface Mergeable
{
    Mergeable mergeWith(Mergeable other);
    Mergeable copy();
}
