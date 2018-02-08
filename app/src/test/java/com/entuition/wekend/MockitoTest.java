package com.entuition.wekend;

/**
 * Created by ryukgoo on 2018. 1. 25..
 */

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MockitoTest {

    @Test
    public void testMoreThanOneReturnValue() {
        Iterator<String> i = mock(Iterator.class);
        when(i.next()).thenReturn("Mockito").thenReturn("rocks");
        String result = i.next() + " " + i.next();

        assertEquals("Mockito rocks", result);
    }

    @Test
    public void testReturnValueDependentOnMethodParameter() {
        Comparable<String> c = mock(Comparable.class);
        when(c.compareTo("Mockito")).thenReturn(1);
        when(c.compareTo("Eclipse")).thenReturn(2);

        assertEquals(1, c.compareTo("Mockito"));
    }

    @Test
    public void testReturnValueInDependentOnMethodParameter() {
        Comparable<Integer> c = mock(Comparable.class);
        when(c.compareTo(anyInt())).thenReturn(-1);

        assertEquals(-1, c.compareTo(9));
    }

    @Test
    public void testReturnValueInDependentOnParameter2() {
        Comparable<Todo> c = mock(Comparable.class);
        when(c.compareTo(isA(Todo.class))).thenReturn(0);

        assertEquals(0, c.compareTo(new Todo(1)));
    }

    @Test
    public void testReturnThrowsException() {
        Properties properties = mock(Properties.class);

        when(properties.get("Anddroid")).thenThrow(new IllegalArgumentException(""));

        try {
            properties.get("Anddroid");
            fail("Anddroid is misspelled");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testDoReturnWhen() {
        Properties properties = new Properties();
        Properties spyProperties = spy(properties);

        doReturn("42").when(spyProperties).get("shoeSize");
        String value = (String) spyProperties.get("shoeSize");

        assertEquals("42", value);
    }

    @Test
    public void testLinkedListSpyCorrect() {
        List<String> list = new LinkedList<>();
        List<String> spy = spy(list);

        doReturn("foo").when(spy).get(0);

        assertEquals("foo", spy.get(0));
    }

    @Test
    public void testVerify() {

        MyClass test = Mockito.mock(MyClass.class);
        when(test.getUniqueId()).thenReturn(43);

        test.testing(12);
        test.getUniqueId();
        test.getUniqueId();

        verify(test).testing(ArgumentMatchers.eq(12));
        verify(test, times(2)).getUniqueId();

        verify(test, never()).someMethod("never called");
//        verify(test, atLeastOnce()).someMethod("called at least once");
//        verify(test, atLeast(2)).someMethod("called at least twice");
//        verify(test, times(5)).someMethod("called five times");
//        verify(test, atMost(3)).someMethod("called at most 3 times");
//
//        verifyNoMoreInteractions(test);
    }

    private class MyClass {
        public int getUniqueId() { return 0; }
        public void testing(int id) {}
        public void someMethod(String message) { }
    }

    private class Todo {
        Todo(int count) {}
    }
}