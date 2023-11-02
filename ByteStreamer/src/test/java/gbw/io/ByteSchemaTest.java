package gbw.io;

import utils.FileEncoder;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

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
                .onError(System.out::println)
                .exec(intVal::set)
                .onEarlyOutAppend(System.out::println);

        ValErr<SchemaHandler,Exception> handler = SchemaHandler.of(encodeTestFile.val(), schema);
        if(handler.hasError()) throw handler.err();

        handler.val().run();

        assertEquals(127, intVal.get());
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