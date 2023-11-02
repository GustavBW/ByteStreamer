# ByteStreamer
 Low memory, efficient decoding, parsing and handling generic binary encoded files.

 Aims to keep as little as possible in memory at a time, and lets you handle each value from the file immediately as they're retrieved - as intuitively as possible.


### How It Works
By constructing a generic "ByteSchema" - you can declaratively express exactly what should happen:
```
ByteSchema schema = ByteSchema
        .first(4, Integer.class)
        .exec(i -> System.out.println("We got an int: " + i));
        .next(8, Double.class)
        .onErrorDo(e -> System.out.println("Double down"))
        .exec(...):
```

### How it Works Behind The Hood
It doesn't. At least not yet. But should prove for some awesome reactivity down the line.


 
