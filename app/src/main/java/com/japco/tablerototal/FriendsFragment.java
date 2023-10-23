package com.japco.tablerototal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    List<Friend> friendsList;
    RecyclerView recordView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fillMatchList();
        recordView = (RecyclerView)view.findViewById(R.id.recyclerViewFriends);
        recordView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recordView.setLayoutManager(layoutManager);
        FriendListAdapter friendsAdapter= new FriendListAdapter(friendsList,
                new FriendListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Friend item) {
                        clickOnItem(item);
                    }
                });
        recordView.setAdapter(friendsAdapter);
    }

    public void clickOnItem(Friend friend){
        Log.i("Click adapter","Item Clicked "+friend.getName());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();
        //Intent intent=new Intent (MainRecycler.this, MainActivity.class);
        //intent.putExtra(MATCH_SELECTED, match);
        //Poner algo de transacciones ???
        //startActivity(intent);
    }

    private void fillMatchList(){
        friendsList = new ArrayList<Friend>();
        friendsList.add(new Friend("Reih1", "15/10/2023 12:21"));
        friendsList.add(new Friend("SamuDestroyer","23/10/2023 12:34"));
        friendsList.add(new Friend("GodAtarash1", "21/10/2023 19:33"));
        friendsList.add(new Friend("Depredador07", "24/10/2023 10:07"));
    }
}