package main.java.zenit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest<T1, T2> {
    private Tuple<T1, T2> tuple;
    private T1 t1;
    private T2 t2;

    @BeforeEach
    void setup(){
        tuple = new Tuple<T1, T2>();
    }

    @Test
    void testSet(){
        t1 = (T1) "t1";
        t2 = (T2) "t2";
        tuple.set(t1, t2);
        assertAll(
                () -> assertEquals(t1, tuple.fst()),
                () -> assertEquals(t2, tuple.snd())
        );
    }

    @Test
    void testFst(){
        t1 = (T1) "t1";
        tuple.set(t1, t2);
        assertEquals(t1, tuple.fst());
    }

    @Test
    void testSnd(){
        t2 = (T2) "t2";
        tuple.set(t1, t2);
        assertEquals(t2, tuple.snd());
    }

    @Test
    void testToString(){
        t1 = (T1) "t1";
        t2 = (T2) "t2";
        tuple.set(t1, t2);
        assertEquals("(t1, t2", tuple.toString());
    }

}