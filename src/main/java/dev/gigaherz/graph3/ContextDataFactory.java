package dev.gigaherz.graph3;

@FunctionalInterface
public interface ContextDataFactory<T extends Mergeable<T>>
{
    T create(Graph<T> target);
}
