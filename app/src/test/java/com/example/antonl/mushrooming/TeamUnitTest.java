package com.example.antonl.mushrooming;

import com.mushrooming.base.Position;
import com.mushrooming.base.Team;
import com.mushrooming.base.User;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TeamUnitTest {
    @Test
    public void simpleTeamManaging() throws Exception {
        Team team = new Team();

        team.updateUserPosition(1, new Position(0,0));
        team.updateUserPosition(2, new Position(0,0));

        assertEquals(2, team.getUsers().size());

        team.removeUser(1);
        team.removeUser(2);

        assertTrue(team.getUsers().isEmpty());
    }

    @Test
    public void teamManaging() throws Exception {
        Team team = new Team();
        team.updateUserPosition(42, new Position(4, 2));
        team.updateUserPosition(44, new Position(4, 4));
        for (User u : team.getUsers()) {
            assertTrue(u.getGpsPosition().getX() == 4);
        }

        team.updateUserPosition(42, new Position(2, 2));

        int count = 0;
        for (User u : team.getUsers()) {
            if (u.getGpsPosition().getX() == 4) {
                count++;
            }
        }

        assertEquals(1, count);

        team.updateUserPosition(44, new Position(3,3));

        for (User u : team.getUsers()) {
            assertFalse(u.getGpsPosition().getX() == 4);
        }

        team.removeUser(44);
        team.removeUser(42);
        team.removeUser(3);
        team.removeUser(14);

        assertTrue(team.getUsers().isEmpty());
    }

}