package gbw.bytestreamer.schema;

import gbw.bytestreamer.schema.exceptions.EarlyOut;
import gbw.bytestreamer.util.Ref;
import gbw.bytestreamer.util.ValErr;
import utils.FileEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ByteSchemaTest {

    private final List<File> deleteOnExit = new ArrayList<>();
    private static final String testDir = "./tests/ByteSchema";
    private static final byte[] EMPTY_BUFFER = {};

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        deleteOnExit.forEach(File::delete);
    }

    private File getEncodedTestFile(String name, byte[] buffer) throws Exception {
        ValErr<File, Exception> encodeTestFile = FileEncoder.encode(name, buffer);
        if(encodeTestFile.hasError()) throw encodeTestFile.err();
        deleteOnExit.add(encodeTestFile.val());
        return encodeTestFile.val();
    }

    private SchemaHandler getHandlerFor(ByteSchema schema, File file) throws Exception {
        ValErr<SchemaHandler,Exception> handler = SchemaHandler.of(file, schema);
        if(handler.hasError()) throw handler.err();
        return handler.val();
    }

    @org.junit.jupiter.api.Test
    void first() throws Exception {
        File testFile = getEncodedTestFile(testDir + "/first.bin", new byte[]{0,0,0,127});

        final Ref<Integer> intVal = new Ref<>();
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(intVal::set);

        getHandlerFor(schema, testFile).run();

        assertEquals(127, intVal.get());
    }

    @org.junit.jupiter.api.Test
    void next() throws Exception {
        File testFile = getEncodedTestFile(testDir + "/next.bin", new byte[]{0,0,0,127,69});
        //Read first the 127 as an int, then the last byte as a ... byte

        final Ref<Integer> intVal = new Ref<>();
        final Ref<Byte> byteVal = new Ref<>();
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(intVal::set)
                .next(Byte.class)
                .exec(byteVal::set);

        getHandlerFor(schema, testFile).run();

        assertEquals(127, intVal.get());
        assertEquals((byte) 69, byteVal.get());
    }

    @org.junit.jupiter.api.Test
    void array() throws Exception {
        File testFile = getEncodedTestFile(testDir + "/array.bin", new byte[]{0,0,0,127,0,0,0,127,0,0,0,127,0,0,0,127,0,0,0,126,0,0,0,126,0,0,0,126,0,0,0,-128});
        final List<Integer> expectedList1 = List.of(127,127,127,127);
        final List<Integer> expectedList2 = List.of(126,126,126,126);

        List<Integer> resultList1 = new ArrayList<>();
        List<Integer> resultList2 = new ArrayList<>();
        ByteSchema schema = ByteSchema.create()
                .many(4,Integer.class)
                .exec(resultList1::add)
                .many(4, Integer.class)
                .exec(resultList2::add);

        getHandlerFor(schema, testFile).run();
        //List deep equals
        assertEquals(expectedList1.size(), resultList1.size());
        for(int i = 0; i < expectedList1.size(); i++){
            assertEquals(expectedList1.get(i), resultList1.get(i));
        }
        assertEquals(expectedList2.size(), resultList2.size());
        for(int i = 0; i < expectedList2.size(); i++){
            assertEquals(expectedList2.get(i), resultList2.get(i));
        }
    }

    @org.junit.jupiter.api.Test
    void knownSizeList() throws Exception{
        File testFile = getEncodedTestFile(testDir + "/knownSizeList.bin", new byte[]{0,0,0,127,0,0,0,127,0,0,0,127,0,0,0,127});
        List<Integer> expectedList = List.of(127,127,127,127);

        Ref<List<Integer>> listRef = new Ref<>();

        ByteSchema schema = ByteSchema.create()
                .list(4, Integer.class)
                .exec(listRef::set);

        getHandlerFor(schema, testFile).run();

        List<Integer> asObtainedFromRef = listRef.get();
        assertNotNull(asObtainedFromRef);
        assertEquals(4, asObtainedFromRef.size());
        for(int i = 0; i < asObtainedFromRef.size(); i++){
            assertEquals(expectedList.get(i), asObtainedFromRef.get(i));
        }
    }

    @org.junit.jupiter.api.Test
    void push() throws Exception {

    }
    @org.junit.jupiter.api.Test
    void onEarlyOutAppend() throws Exception {
        File file = getEncodedTestFile(testDir + "/onEarlyOutAppend.bin", new byte[4]);

        String expectedMessage = "There is a message!";
        Ref<String> msg = new Ref<>("No message...");
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .onEarlyOut(e -> msg.set(e.getMessage()))
                .exec(i -> {
                    throw new EarlyOut(expectedMessage);
                });

        getHandlerFor(schema, file).run();

        assertEquals(expectedMessage, msg.get());
    }
    @org.junit.jupiter.api.Test
    void appendOnPush() throws Exception {
        File file = getEncodedTestFile(testDir + "/appendOnPush.bin", new byte[]{1,2,3,4});

        List<Byte> pushedBytes = new ArrayList<>();
        Ref<Integer> intVal = new Ref<>();
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(intVal::set)
                .appendOnPush(pushedBytes::add);

        getHandlerFor(schema, file).run();

        assertEquals(16909060, intVal.get());
        for(byte expected = 1; expected < 5; expected++){
            assertEquals(expected, pushedBytes.get(expected -1));
        }
    }
    @org.junit.jupiter.api.Test
    void onSchemaComplete() throws Exception {
        File file = getEncodedTestFile(testDir + "/onSchemaComplete.bin", new byte[]{0,0,0,127});

        //Check if it works in general:
        String expectedMsg = "This, not the other one.";
        Ref<String> msg = new Ref<>("Not this!");
        Ref<Integer> intVal = new Ref<>();
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(intVal::set)
                .onSchemaComplete(() -> msg.set(expectedMsg));

        getHandlerFor(schema, file).run();

        assertEquals(127, intVal.get());
        assertEquals(expectedMsg, msg.get());

        //Check if it only executes exactly once.
        Ref<Integer> counter = new Ref<>(0);
        ByteSchema schema2 = ByteSchema
                .first(4, Integer.class)
                .exec(i -> {})
                .onSchemaComplete(() -> counter.set(counter.get() + 1));

        getHandlerFor(schema2, file).run();

        assertEquals(1, counter.get());
    }
    @org.junit.jupiter.api.Test
    void onSchemaIncomplete() throws Exception {
        File file = getEncodedTestFile(testDir + "/onSchemaIncomplete.bin", new byte[]{1,2});

        String expected = "Schema left envying a satisfying resolution";
        Ref<String> actual = new Ref<>("All is great.");
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(ignored -> {})
                .onSchemaIncomplete(e -> actual.set(expected));

        getHandlerFor(schema, file).run();

        assertEquals(expected, actual.get());
    }
    @org.junit.jupiter.api.Test
    void onSchemaOverflow() throws Exception {
        int fileByteLength = 10;
        File file = getEncodedTestFile(testDir + "/onSchemaOverflow.bin", new byte[fileByteLength]);

        List<Byte> overflown = new ArrayList<>();
        ByteSchema schema = ByteSchema
                .first(4, Integer.class)
                .exec(ignored -> {})
                .onSchemaOverflow(overflown::add);

        getHandlerFor(schema, file).run();

        //We expect 6 bytes to overflow and end in the list
        assertEquals(fileByteLength - 4, overflown.size());
    }
    @org.junit.jupiter.api.Test
    void close() throws Exception {



    }



}