# ByteStreamer
 Low memory, efficient decoding, parsing and handling generic binary encoded files.

 Aims to keep as little as possible in memory at a time, and lets you handle each value from the file immediately as they're retrieved - as intuitively as possible.


### How It Works
By constructing a generic "ByteSchema" - you can declaratively express exactly what should happen:
```java
ByteSchema schema = ByteSchema
    .first(<byteAmount>, Integer.class)
        .exec(i -> System.out.println("We got an int: " + i));
    .next(<byteAmount>, Double.class)
        .on(Events.BUFFER_PARSING_ERROR, () -> System.out.println("Double down"))
        .exec(double -> ...)
    .skip(<byteAmount>)
    .many(<howMany>, Float.class)
        .exec(float -> ...)
    .list(<length>, Integer.class)
        .exec(List<Integer> -> ...);
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
A lot of java.util.function, generics, extremely unsafe, explicit type casting and a couple of byte buffers.

## Currently Supports
Any amount of all primitive, signed types (int8, int16, int32, int64, float32, float64, null*) <br>
Arrays of all primitive, signed types - as long as their length is known beforehand <br>
*assumed to be 8 bits of "0"

## Assumptions / Conventions
It appears to mildly impossible to genericly parse *any* binary file without a little upfront knowledge about how its encoded. <br>
It does appear to be possible if we assume that any value encoded is in accordance with IEEE 754 <br>
AND <br>
It's signed (or the equivalent for floating point). The latter simply due to the reason that, some languages does not make a distinction 
between signed and unsigned or simply elects not to provide one or the other. (Intentionally not naming names here.) <br>
Meaning, any language that does make a clear distinction in the use of the language, will have no issue taking this into account when encoding
the data for the stream or other IO. However, languages that does not, simply lacks the ability to decode the file without further standardization / conventions. <br>
<br>

### Length or Termination Pattern
When dealing with lists / arrays of elements, the only way to know when that list ends is by either providing the lenght up front, or some kind of pattern to look for to know when the array har ended. <br>
Building on the assumption above that all values are signed, and that the first bit of the bytes is the negative one, it's possible to derive the array length or the termination pattern based on if its on since a length can't be negative anyways. After this check the first bit is set to 0 regardless: <br>
v sign bit <br>
1000 0000 ..8*"0".. 0000 0000 - sign bit is on so its a termination pattern, becoming the same as (i32) 0 when the sign bit is set to 0. <br>
0000 0000 ..8*"0".. 0001 0101 - sign bit is off so its a length, becoming a length of 21 <br>
The only actual difference in decoding them, is that an array length is always an i32 while the termination pattern has a byte length the same as each element. <br>
To reduce the complexity of creating a ByteSchema, it is expected that the termination pattern or length is preceeding the array itself at all times, effectively removing the need for providing any of them at the decoding side of things. <br> 

### Next Up
Byte pattern terminated arrays. (Collections of elements deemed to end only when some pattern of bits occurs - pattern exlusive) <br> 
"Length-up-front" arrays. Arrays which length is not known, but stored as an uint32 right before the start of the array <br>
Strings. Needn't say mo' 'bout that. (Adding charset and encoding support to the array operations as .string()) <br> 

```java
ByteSchema.list(<terminationPattern>, Class).exec(List<Class> -> ...);  //Until the terminationPatther, accumulate, then handle the gathered list.
ByteSchema.until(<terminationPattern>, Class).exec(Class -> ...);      //Until the terminationPattern "for each" each parsed element.
ByteSchema.string(<charLength>, Encoding).exec(String -> ...);         // How many characters of what encoding should be gathered, parsed and handed
ByteSchema.string(<terminationPattern>, Encoding).exec(String -> ...); //Requires standardized terminating pattern.
ByteSchema.map(<Type of Key>, <Type of Value>).exec(Map<T,R> -> ...)
```


## But... why?
Because we tend to use what is easy - not efficient. <br>
The fantastical endgame to this project would be generalized, efficient data deserializers for most languages making JSON obsolete for networking purposes, and reducing all package sizes with probably about 70%.  <br>
Furthermore, this approach supports streaming by default, but has no issue with a single, complete package. 


 
