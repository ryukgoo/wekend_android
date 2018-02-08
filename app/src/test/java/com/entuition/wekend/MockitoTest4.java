package com.entuition.wekend;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 26..
 */

public class MockitoTest4 {

    @Test
    public void spyTest() {
        List list = new LinkedList();
        List spy = spy(list);

        when(spy.size()).thenReturn(100);

        spy.add("one");
        spy.add("two");

        System.out.println(spy.get(0));
        System.out.println(spy.size());

        verify(spy).add("one");
        verify(spy).add("two");

//        when(spy.get(10)).thenReturn("foo");

        doReturn("foo").when(spy).get(0);
    }

    class ListOfElements implements ArgumentMatcher<List> {

        @Override
        public boolean matches(List argument) {
            List list = argument;
            return list.size() == 2;
        }
    }

    @Test
    public void argumentMatcherTest() {
        List mock = mock(List.class);
        when(mock.addAll(argThat(new ListOfElements()))).thenReturn(true);

        mock.addAll(Arrays.asList("one", "two"));

        verify(mock).addAll(argThat(new ListOfElements()));
    }

    @Test
    public void argumentCaptorTest() {
        List mock = mock(List.class);
        mock.addAll(Arrays.asList("one", "two"));

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(mock).addAll(argument.capture());
        assertTrue(argument.getValue().size() == 2);
    }

    @Test
    public void timeoutTest() {
        List mock = mock(List.class);

        mock.size();
//        mock.size();

        // ?
        verify(mock, timeout(100)).size();
//        verify(mock, timeout(100).times(2)).size();
//        verify(mock, timeout(100).atLeast(2)).size();
    }

    @Test
    public void oneLinerStubsTest() {
        List mock = when(mock(List.class).get(0)).thenReturn(1).thenReturn(2).getMock();

        System.out.println(mock.get(0));
        verify(mock).get(anyInt());
    }

    @Test
    public void mockingDetailTest() {
        List list = new LinkedList();
        List spy = spy(list);
        List mock = mock(List.class);

        mockingDetails(mock).isMock();
        mockingDetails(spy).isSpy();
    }

}
