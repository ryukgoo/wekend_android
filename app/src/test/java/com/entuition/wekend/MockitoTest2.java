package com.entuition.wekend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 26..
 */

@RunWith(JUnit4.class)
public class MockitoTest2 {

    @Test
    public void mockingTest() throws Exception {
        List mockedList = mock(List.class);

        mockedList.add("one");
        mockedList.clear();

        verify(mockedList).add("one");
        verify(mockedList).clear();

        when(mockedList.get(anyInt())).thenReturn("int");
        when(mockedList.add(anyDouble())).thenReturn(true);
        when(mockedList.add(anyString())).thenReturn(true);

        System.out.println(mockedList.get(999));
        System.out.println(mockedList.add(3.3));
        System.out.println(mockedList.add("string"));

        verify(mockedList).get(anyInt());
        verify(mockedList).add(anyDouble());
        verify(mockedList).add(eq("string"));

        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        verify(mockedList, never()).add("never happened");

        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("three times");
        verify(mockedList, atMost(5)).add("three times");

        doThrow(new RuntimeException()).when(mockedList).clear();
//        mockedList.clear();

        try {
            mockedList.clear();
        } catch (RuntimeException e) {

        }
    }

    @Test
    public void verificationInOrderTest() {

        List singleMock = mock(List.class);

        singleMock.add("first");
        singleMock.add("second");

        InOrder inOrder = Mockito.inOrder(singleMock);

        inOrder.verify(singleMock).add("first");
        inOrder.verify(singleMock).add("second");

        //multiple mocks
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        firstMock.add("first");
        secondMock.add("second");

        InOrder multiOrder = Mockito.inOrder(firstMock, secondMock);

        multiOrder.verify(firstMock).add("first");
        multiOrder.verify(secondMock).add("second");
    }

    @Test
    public void verifyNoMoreInteractionsTest() {
        List mockedList = mock(List.class);

        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");
        verify(mockedList).add("two");

        verifyNoMoreInteractions(mockedList);
    }

}
