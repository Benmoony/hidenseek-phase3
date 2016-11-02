package com.cascadia.hidenseek;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class JoinUnitTest {
    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mMockContext;

    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
        /*when(mMockContext.getString(R.string.hello_word))
                .thenReturn(FAKE_STRING);
        JoinLogin myObjectUnderTest = new JoinLogin();

        // ...when the string is returned from the object under test...
        myObjectUnderTest.joinMatch();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));*/
    }
    @Test
    public void joinMatch_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}