# GraphLib
GraphLib is a simple graph library that helps maintain networks of nodes, joining and splitting graphs as necessary.
It was designed for use in Minecraft Mods' networks, but it can be used for any other purpose.

See [LICENSE.txt](LICENSE.txt) for the usage terms.

# Gradle

Include my maven repository in your gradle
```gradle
repositories {
    maven {
        url 'http://dogforce-games.com/maven'
    }
}
```

And add the library as a dependency (no need for deobf) 
```gradle
dependencies {
    implementation "dev.gigaherz.graph:GraphLib3:3.0.5"
}
```

The library can be [shaded](https://github.com/johnrengelman/shadow) (with relocation) or embedded through jar-in-jar. Refer to the corresponding documentation on the embedding system of your platform for details.

# Usage

GraphLib starts with the idea of GraphObjects. These are the nodes in the graph. 

To build a graph, just connect GraphObjects to other GraphObjects. `Graph.connect` lets you create a single edge, while `Graph.integrate` lets you connect a node to a number of neighbours all at once.

The `ConcurrentGraph` class does the same, but using thread-safe logic.

A graph can optionally contain additional data belonging to the network. When present, this data must implement the `Mergeable` interface, which will be used when two networks are joined together.

