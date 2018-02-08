package com.entuition.wekend;

import org.junit.Test;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryukgoo on 2018. 1. 26..
 */

public class CreatingMockObjectUsingMockitoTest {

    public interface ITweet {
        String getMessage();
    }

    public class TwitterClient {
        public void sendTweet(ITweet tweet) {
            String message = tweet.getMessage();
        }
    }

    @Test
    public void testSendingTweet() {
        TwitterClient twitterClient = new TwitterClient();

        ITweet iTweet = mock(ITweet.class);

        when(iTweet.getMessage()).thenReturn("Using mockito is great");
        twitterClient.sendTweet(iTweet);

        verify(iTweet, atLeastOnce()).getMessage();
    }

}
