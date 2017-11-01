package com.example.antonl.mushrooming;

import com.mushrooming.base.Position;
import com.mushrooming.base.Team;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TeamUnitTest {
    @Test
    public void teamManaging() throws Exception {
        Team team = new Team();

        team.updateUser(1, new Position(0,0));
        team.updateUser(2, new Position(0,0));


    }
}