package com.entuition.wekend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 26..
 */

@RunWith(MockitoJUnitRunner.class)
public class MockitoTest3 {

    @Mock
    private List mockedList;

    @Test
    public void test() {

        //Iterator-style stubbing
        when(mockedList.get(anyInt()))
                .thenReturn("one", "two");

        System.out.println(mockedList.get(0));
        System.out.println(mockedList.get(1));
        System.out.println(mockedList.get(2));

        when(mockedList.get(anyInt()))
                .thenReturn("foo")
                .thenReturn("bar");
//                .thenThrow(new RuntimeException());

        System.out.println(mockedList.get(0));
        System.out.println(mockedList.get(1));
        System.out.println(mockedList.get(2));


        //Stubbing with callbacks
        when(mockedList.get(anyInt())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                List mock = (List) invocation.getMock();
                int result = (Integer) args[0] + 1;
                return result;
            }
        });

        System.out.println(mockedList.get(1));
    }
}
