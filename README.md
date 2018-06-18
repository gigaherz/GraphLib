# GraphLib
A simple generic graph library for use in Minecraft Mods' networks.

See [LICENSE.txt](LICENSE.txt) for the usage terms.

# How to use (Maven dependency with shading)

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version '1.2.3'
}
```

```gradle
repositories {
    maven {
        url 'http://dogforce-games.com/maven'
    }
}
```

```gradle
jar {
    classifier = 'slim'
}

shadowJar {
    classifier = ''
    dependencies {
        include(dependency(':GraphLib:'))
    }
}

reobf {
    shadowJar { mappingType = 'SEARGE' }
}

tasks.build.dependsOn reobfShadowJar

artifacts {
    archives shadowJar
    archives sourceJar
}

dependencies {
    deobfCompile "gigaherz.graph:GraphLib:1.4.0"
}
```