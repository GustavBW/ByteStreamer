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
        .exec(...)
    .skip(4)
    .array(4, Float.class)
        .exec(...);
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
    .on(Events.BUFFER_PARSING_ERROR, () -> {...})   //If an error happens when parsing the bytes to the expected type do...
    .on(Events.CONSUMPTION_ERROR, () -> {...})      //If an error happens when executing the handling function do...
    .on(Events.POST_ENTRY_COMPLETION, () -> {...}); //When the schema is ready to move on from this Entry do...    
``` 

### How it Works Behind The Hood
A lot of java.util.function, extremely unsafe, explicit type casting and a couple of byte buffers.

## Currently Supports
Any amount of all primitive, unsigned types (int8, int16, int32, int64, float32, float64, null*) <br>
Arrays of all primitive, unsigned types - as long as their length is known beforehand <br>
NB: Currently got an encoding / parsing error of signed types (thanks Java) - so uh. Hope you don't intend to use negative numbers. <br> 
*assumed to be 8 bits of "0"

### Next Up
Known-size arrays (an array where the length is known beforehand, either by standardization of a pre-cursing int to tell the length or manually given) <br>
Aswell as more dynamic/flexible approaches based on matching custom byte segments:
```java
ByteSchema.until(0, Integer.class).exec(List<Integer> -> {...}); //Until a byte that is 0, collect int32's into a variable size array.
ByteSchema.string(11, Encoding).exec(String -> {}); //.array(length,type) approach although the type is given by the dedicated method.
ByteSchema.string(Encoding).exec(String -> {});     //Same as until(0,Byte.class), although Strings require a bit more guidance.
```


## But... why?
Because we tend to use what is easy - not efficient. <br>
The fantastical endgame to this project would be generalized, efficient data deserializers for most languages making JSON obsolete for networking purposes, and reducing all package sizes with probably about 70%.  <br>
Furthermore, this approach supports streaming by default, but has no issue with a single, complete package. Just an added benefit.


 
