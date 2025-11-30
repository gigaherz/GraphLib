package dev.gigaherz.graph3.bench;

import com.google.common.base.Stopwatch;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Benchmarks
{
    public static void main(String... args)
    {
        Random rnd = new Random();

        Stopwatch sw = Stopwatch.createStarted();

        var go1 = new GO();
        var go2 = new GO();

        Graph.integrate(go1, List.of());
        Graph.integrate(go2, List.of());

        for(int i = 0; i< 1000; i++)
        {
            List<GraphObject<GOData>> others = new ArrayList<>();

            if (go1.getGraph() != null)
            {
                var existing = go1.getGraph().getObjects();
                for (var node : existing)
                {
                    if (0.1f >= rnd.nextFloat())
                        others.add(node);
                }
            }
            else
            {
                others.add(go1);
            }

            Graph.integrate(new GO(), others);
        }

        for(int i = 0; i< 1000; i++)
        {
            List<GraphObject<GOData>> others = new ArrayList<>();

            if(go2.getGraph() != null)
            {
                var existing = go2.getGraph().getObjects();
                for (var node : existing)
                {
                    if (0.1f >= rnd.nextFloat())
                        others.add(node);
                }
            }
            else
            {
                others.add(go2);
            }

            Graph.integrate(new GO(), others);
        }

        sw.stop();

        System.out.println("Populate graph: " + sw.elapsed());

        sw.reset();
        sw.start();

        Graph.connect(go1, go2);

        sw.stop();

        System.out.println("Connect graphs: " + sw.elapsed());

        sw.reset();
        sw.start();

        go1.getGraph().removeNonDirectionalSingleEdge(go1,go2);

        sw.stop();

        System.out.println("Disconnect graphs: " + sw.elapsed());

    }

    private static class GO implements GraphObject<GOData>
    {
        private Graph<GOData> graph;

        @Override
        public Graph<GOData> getGraph()
        {
            return graph;
        }

        @Override
        public void setGraph(Graph<GOData> g)
        {
            graph = g;
        }
    }

    private static class GOData implements Mergeable<GOData>
    {
        @SuppressWarnings("removal")
        @Override
        public GOData mergeWith(GOData other)
        {
            return this;
        }

        @SuppressWarnings("removal")
        @Override
        public GOData copy()
        {
            return new GOData();
        }
    }
}
