## Benchmarks for Streams 
 
Experimenting with streams and collections of scala and dotty.

### Usage
```sbtshell
test
jmh:run -i 15 -wi 15 -f 3 -gc true .*
```

### Contents

Currently all collections are boxed (no specialization) and sequential:

1. baseline in loop-based code
1. `scala.collection.mutable.IndexedSeqView` from scala collections
1. `strawman.collection.ArrayView` of collection-strawman
1. a minimal pull-based implementation
1. a minimal reference push-based implementation

### References
1. Using [JMH](http://openjdk.java.net/projects/code-tools/jmh/) + [sbt-jmh](https://github.com/ktoso/sbt-jmh)
1. More on [collection-strawman](https://github.com/scala/collection-strawman) + [Dotty Issue](https://github.com/lampepfl/dotty/issues/818)
1. [Two styles for On-Demand Processing](https://biboudis.github.io/papers/dissertation.pdf#page=54)
