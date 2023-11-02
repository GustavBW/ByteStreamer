# ByteStreamer
 Low memory, efficient decoding, parsing and handling generic binary encoded files.

 Aims to keep as little as possible in memory at a time, and lets you handle each value from the file immediately as they're retrieved - as intuitively as possible.


### How It Works
By constructing a generic "ByteSchema" - you can declaratively express exactly what should happen:
```java
ByteSchema schema = ByteSchema
        .first(4, Integer.class)
        .exec(i -> System.out.println("We got an int: " + i));
        .next(8, Double.class)
        .on(Events.BUFFER_PARSING_ERROR, () -> System.out.println("Double down"))
        .exec(...):
```

Error handling is explicitly declared with a multitude of hooks to allow for extensions and custom behaviour. <br>
The ByteSchema itself allows for the following hooks: <br>

```java
ByteSchema schema = ByteSchema.{<schema-entry-definitions>}
   .onSchemaOverflow(byte -> {...})        //Do for each byte pushed although the schema is complete
   .onSchemaComplete(() -> {...})          //When there's no entries left to parse and handle.
   .onSchemaIncomplete(Exception -> {...}) //If the schema was closed before it was incomplete.
   .appendOnPush(byte -> {...})            //For any byte before completion do.
   .onEarlyOutAppend(EarlyOut -> {...});   //If the handling function for the entry throws this type of error, the schema should also do ...
```
And each entry as constructed through the EntryConfigurator allows for handling the EarlyOut if any, <br>
aswell as some general handlers to run if the ByteSchema catches any exceptions when resolving the entry:
```java
EntryConfigurator
    .onEarlyOut(EarlyOut -> {...})         //If the handling function throws, do...
    .on(Events.BUFFER_PARSING_ERROR, () -> {...})
    .on(Events.CONSUMTION_ERROR, () -> {...});
``` 

### How it Works Behind The Hood
A lot of java.util.function, extremely unsafe, explicit type casting and a couple of byte buffers.

## Currently Supports
Any amount of all primitive types (int8, int16, int32, int64, float32, float64, null*)
*assumed to be 8 bits of "0"

### Next Up
Known-size arrays (an array where the length is known beforehand, either by standardization of a pre-cursing int to tell the length or manually given) <br>
Aswell as more dynamic/flexible approaches based on matching custom byte segments:
```java
ByteSchema.until(0, Integer.class).exec(List<Integer> -> {...}); //Until a byte that is 0, collect int32's into a variable size array.
ByteSchema.array(Float.class).exec(float[] -> {});  //Read the next 4 bytes as an int describing the length, then read that length as a float array.
ByteSchema.string(Encoding).exec(String -> {});     //Same as array, although Strings require a bit more guidance.
```


## But... why?
Because we tend to use what is easy - not efficient. <br>
The fantastical endgame to this project would be generalized, efficient data deserializers for most languages making JSON obsolete for networking purposes, and reducing all package sizes with probably about 70%.  <br>
Furthermore, this approach supports streaming by default, but has no issue with a single, complete package. Just an added benefit.


 
