package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.japco.tablerototal.model.User;

import java.util.ArrayList;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    List<User> connectedUsers;
    RecyclerView connectedUsersView;
    String roomCode;
    String game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        this.roomCode = getIntent().getStringExtra("roomCode");
        this.game = getIntent().getStringExtra("game");

        fillMatchList();
        connectedUsersView = (RecyclerView)findViewById(R.id.rcylConnectedUsers);
        connectedUsersView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        connectedUsersView.setLayoutManager(layoutManager);
        UsersListAdapter usersAdapter= new UsersListAdapter(connectedUsers,
                new UsersListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(User item) {
                        clickOnItem(item);
                    }
                });
        connectedUsersView.setAdapter(usersAdapter);

    }

    public void clickOnItem(User user){
        Log.i("Click adapter","Item Clicked "+user.getNickname());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();
        //Intent intent=new Intent (MainRecycler.this, MainActivity.class);
        //intent.putExtra(MATCH_SELECTED, match);
        //Poner algo de transacciones ???
        //startActivity(intent);
    }

    private void fillMatchList(){
        connectedUsers = new ArrayList<User>();
        connectedUsers.add(new User("Reih1", "Ready", null));
        connectedUsers.add(new User("SamuDestroyer","Not Ready", null));
        connectedUsers.add(new User("GodAtarash1", "Not Ready", null));
        connectedUsers.add(new User("Depredador07", "Not Ready", null));
    }
}