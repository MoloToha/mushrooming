package com.example.antonl.mushrooming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mushrooming.base.App;
import com.mushrooming.base.Position;
import com.mushrooming.base.Team;
import com.mushrooming.base.User;

import java.util.ArrayList;
import java.util.Random;

public class DisplayUsersActivity extends AppCompatActivity {
    private ArrayList<String> textBasedUserList;
    private ListView userListView;
    private Team _team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        _team = App.instance().getTeam();
        userListView = (ListView) findViewById(R.id.list_users);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User itemClicked = _team.getUsers().get(position);

                Intent intent = new Intent(DisplayUsersActivity.this, DisplayUserDataActivity.class);
                intent.putExtra("ID", itemClicked.getId());
                intent.putExtra("PosX", itemClicked.getGpsPosition().getX());
                intent.putExtra("PosY", itemClicked.getGpsPosition().getY());

                startActivity(intent);
            }
        });

        Button button = (Button) findViewById(R.id.button_reloadList);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadUserList();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void reloadUserList() {
        updateTextBasedUserList();
        userListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, textBasedUserList));
    }

    public Team createTeamExample(int userCount) {
        Random generator = new Random();
        Team team = new Team();

        for (int i = 0; i < userCount; ++i) {
            //Position mockup
            Position pos = new Position(generator.nextDouble() * 10, generator.nextDouble() * 10);
            User u = new User(i, pos);

            team.addUser(u);
        }

        return team;
    }

    public void updateTextBasedUserList() {
        textBasedUserList = new ArrayList<String>();
        String textUserData;

        for (User user: _team.getUsers()) {
            textUserData = "ID: " + user.getId();
                    //+ " xPos: " + user.getPosition().getX() + " yPos: " + user.getPosition().getY();

            textBasedUserList.add(textUserData);
        }
    }
}
