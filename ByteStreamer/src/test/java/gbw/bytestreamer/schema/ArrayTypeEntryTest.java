package gbw.bytestreamer.schema;

import gbw.bytestreamer.util.Ref;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTypeEntryTest {

    @Test
    void shinyStuff(){
        //Testing for reflection purposes and other type-checking
        ByteSchemaEntry<Integer> testInstanceOf = new ArrayTypeEntry<>(1,Integer.class, new Ref<>());
        assertTrue(testInstanceOf instanceof IOnEntryHandlingDoFirst);
        assertTrue(testInstanceOf instanceof ByteSchemaEntry<Integer>);
        assertTrue(testInstanceOf instanceof ByteSchemaEntry);
    }
    @Test
    void amount() {
    }

    @Test
    void length() {
    }

    @Test
    void as() {
    }

    @Test
    void exec() {
    }

    @Test
    void getOnExecAcceptDoFirst() {
    }

    @Test
    void setHandlerOf() {
    }

    @Test
    void getHandlerOf() {
    }
}