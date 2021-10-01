package dev.gigaherz.graph3;

public interface GraphObject<T extends Mergeable<T>>
{
    Graph<T> getGraph();

    void setGraph(Graph<T> g);
}
