# GraphLib
A simple generic graph library for use in Minecraft Mods' networks.

See [LICENSE.txt](LICENSE.txt) for the usage terms.

# How to add to your `build.gradle`

Include my maven repository in your gradle
```gradle
repositories {
    maven {
        url 'http://dogforce-games.com/maven'
    }
}
```

And add the library as a dependency (no need for `deobfCompile` since it doesn't use obfuscated names) 
```gradle
dependencies {
    compile "gigaherz.graph:GraphLib2:2.0.0"
}
```

# How to embed into a jar to be used by Forge's Dependency Extraction
This is the **preferred** method in 1.12.2 and onward.

Create a new configuration, I like to call it 'embed'
```gradle
configurations {
    // configuration that holds jars to embed inside the jar
    embed
    embed.transitive = false;
}
```

Add the dependency also to the embed configuration
```gradle
dependencies {
    compile "gigaherz.graph:GraphLib2:2.0.0"
    embed "gigaherz.graph:GraphLib2:2.0.0"
}
```

Tell gradle to put the embed dependencies in your jar
```gradle
jar {
    into('/META-INF/libraries') {
        from configurations.embed
    }

    manifest {
        attributes([
            "ContainedDeps": configurations.embed.collect { it.getName() }.join(' '),
            "Maven-Artifact":"${project.group}:${project.archivesBaseName}:${project.version}",
            'Timestamp': System.currentTimeMillis()
        ])
    }
}
```

# How to shade into a mod
Don't do this if you are embedding using the dependency extraction system described above.

If you don't already do this, include the shadow plugin
```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version '1.2.3'
}
```

Make your jar task have a classifier. The shadow task will be the one without classifier 
```gradle
jar {
    classifier = 'slim'
}
```

Create a shadowJar task without classifier (this will be the final jar) 
```gradle
shadowJar {
    classifier = ''
    dependencies {
        include(dependency(':GraphLib:'))
    }
}
```

Tell ForgeGradle to reobfuscate the shadowJar task 
```gradle
reobf {
    shadowJar { mappingType = 'SEARGE' }
}

```

Make your build task run the shadow task first
```gradle
tasks.build.dependsOn reobfShadowJar
```

Finally, tell gradle that you want an artifact for the shadowJar placed in the build output
```gradle
artifacts {
    archives shadowJar
}
```