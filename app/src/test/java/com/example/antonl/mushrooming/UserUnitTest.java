package com.example.antonl.mushrooming;

import org.junit.Test;
import com.mushrooming.base.Position;
import com.mushrooming.base.User;
import java.lang.reflect.*;

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
        assertNotEquals(u1, "Meddle");
    }

    @Test
    public void userIsConnected()  throws Exception {
        User u = new User(1, new Position(1,1));
        assertTrue(u.isConnected());

        Field f = u.getClass().getDeclaredField("_lastUpdate");
        f.setAccessible(true);
        f.setLong(u, (long)f.get(u) - (long)10001);

        assertFalse(u.isConnected());

        u.update(new Position(5,5));
        assertTrue(u.isConnected());
    }

    @Test
    public void userPosAndID()  throws Exception {
        User u = new User(66, new Position(666,7734));
        assertEquals(66, u.getId());
        assertTrue(666 == u.getGpsPosition().getX());
        assertTrue(7734 ==  u.getGpsPosition().getY());

        u.update(new Position(59,42));
        assertTrue(59 == u.getGpsPosition().getX());
        assertTrue(42 ==  u.getGpsPosition().getY());
    }

}