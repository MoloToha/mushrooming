package com.example.antonl.mushrooming;

import org.junit.Test;
import com.mushrooming.base.Position;
import com.mushrooming.base.User;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserUnitTest {
    @Test
    public void user_equals() throws Exception {
        User u1 = new User(0, new Position(0,0));
        User u2 = new User(0, new Position(0,0));
        User u3 = new User(1, new Position(0,0));

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u2, u3);
    }
}