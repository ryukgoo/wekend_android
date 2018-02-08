package com.entuition.wekend;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 25..
 */

public class MockitoTest1 {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<List<String>> captor;

    @Test
    public final void shouldContainCertainListItem() {
        List<String> asList = Arrays.asList("someElement_test", "someElement");
        final List<String> mockedList = mock(List.class);
        mockedList.addAll(asList);

        verify(mockedList).addAll(captor.capture());

        final List<String> capturedArgument = captor.getValue();

        System.out.print(capturedArgument);

        assertThat(capturedArgument, hasItem("someElement"));
    }

    @Test
    public final void answerTest() {
        List<String> asList = Arrays.asList("someElement_test", "someElement");
        final List<String> mockedList = mock(List.class);
        mockedList.addAll(asList);

//        doAnswer(returnsFirstArg()).when(mockedList).add(anyString());
//        when(mockedList.add(anyString())).thenAnswer(returnsFirstArg());
//        when(mockedList.add(anyString())).then(returnsFirstArg());
//
//        assertEquals("asdf", mockedList.add("asdf"));
    }

    @Test
    public final void callbackTest() {
        // Sample 1
//        ApiService service = mock(ApiService.class);
//        when(service.login(any(Callback.class))).thenAnswer(i -> {
//            Callback callback = i.getArgument(0);
//            callback.notify("Success");
//            return null;
//        });

        // Sample 2
//        List<User> userMap = new ArrayList<>();
//        UserDao dao = mock(UserDao.class);
//        when(dao.save(any(User.class))).thenAnswer(i -> {
//            User user = i.getArgument(0);
//            userMap.add(user.getId(), user);
//            return null;
//        });
//        when(dao.find(any(Integer.class))).thenAnswer(i -> {
//            int id = i.getArgument(0);
//            return userMap.get(id);
//        });
    }

    /**
     * For testing final class/method -> org.mockito.plugins.MockMaker("mock-maker-inline")
     * PowerMock -> org/powermock/extensions/configuration.properties("mockito.mock-maker-class=mock-maker-inline")
     */
    final class FinalClass {
        public final String finalMethod() { return "something"; }
    }

    @Test
    public void mockFinalClassTest() {
        FinalClass instance = new FinalClass();

        FinalClass mock = mock(FinalClass.class);
        when(mock.finalMethod()).thenReturn("that other thing");

        assertNotEquals(mock.finalMethod(), instance.finalMethod());
    }

    class DeepThought {
        int getAnswerFor(String answer) {
            return 0;
        }

        String otherMethod(String answer) {
            return "";
        }

        void someMethod() {}
    }

    @Test
    public void withoutStrictStubsTest() throws Exception {
        DeepThought deepThought = mock(DeepThought.class);

        when(deepThought.getAnswerFor("Ultimate Question of Life, The Universe and Everything")).thenReturn(42);
        when(deepThought.otherMethod("some mundane thing")).thenReturn(null);

        System.out.println(deepThought.getAnswerFor("Six by nine"));

        assertEquals(42, deepThought.getAnswerFor("Ultimate Question of Life, The Universe and Everything"));
        verify(deepThought, times(1)).getAnswerFor("Ultimate Question of Life, The Universe and Everything");
    }

    @Test
    public void withStrictStubsTest() throws Exception {
        DeepThought deepThought = mock(DeepThought.class);

        when(deepThought.getAnswerFor("Ultimate Question of Life, The Universe and Everything")).thenReturn(42);
        when(deepThought.otherMethod("some mundane thing")).thenReturn(null);

        deepThought.someMethod();

        assertEquals(42, deepThought.getAnswerFor("Ultimate Question of Life, The Universe and Everything"));
//        verifyNoMoreInteractions(deepThought);
    }

}
