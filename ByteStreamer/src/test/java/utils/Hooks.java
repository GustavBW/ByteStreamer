package utils;

import gbw.bytestreamer.schema.ByteSchema;

public class Hooks {

    public static void debugLogAll(ByteSchema schema){
        schema.onSchemaComplete(() -> System.out.println("Schema complete: " + schema))
            .appendOnPush(b -> System.out.println("Schema got pushed: " + b))
            .onSchemaIncomplete(e -> {
                System.out.println("Schema incomplete with exception");
                e.printStackTrace();
            }).onSchemaOverflow(b -> System.out.println("Schema overflown with byte: " + b))
                .onEarlyOutAppend(e -> System.out.println("Schema got an early out: " + e.getMessage()));
    }

}
