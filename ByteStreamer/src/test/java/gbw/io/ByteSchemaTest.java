package gbw.io;

import gbw.bytestreamer.*;
import utils.FileEncoder;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteSchemaTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void first() throws Exception {
        ValErr<File, Exception> encodeTestFile = FileEncoder.encode("./tests/ByteSchemaFirst.bin", new byte[]{0,0,0,127});
        if(encodeTestFile.hasError()) throw encodeTestFile.err();

        final Ref<Integer> intVal = new Ref<>();
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(intVal::set);

        ValErr<SchemaHandler,Exception> handler = SchemaHandler.of(encodeTestFile.val(), schema);
        if(handler.hasError()) throw handler.err();

        handler.val().run();

        assertEquals((byte) 127, intVal.get());
    }



    @org.junit.jupiter.api.Test
    void next() {
    }

    @org.junit.jupiter.api.Test
    void push() {
    }

    @org.junit.jupiter.api.Test
    void onEarlyOutAppend() {
    }
}