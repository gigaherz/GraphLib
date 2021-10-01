package dev.gigaherz.graph3;

public interface Mergeable<T extends Mergeable<T>>
{
    T mergeWith(T other);
    T copy();
}
