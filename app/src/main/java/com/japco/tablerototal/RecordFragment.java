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

public class RecordFragment extends Fragment {

    List<Match> matchList;
    RecyclerView recordView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fillMatchList();
        recordView = (RecyclerView)view.findViewById(R.id.recyclerViewRecord);
        recordView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recordView.setLayoutManager(layoutManager);
        MatchListAdapter matchAdapter= new MatchListAdapter(matchList,
                new MatchListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Match item) {
                        clickOnItem(item);
                    }
                });
        recordView.setAdapter(matchAdapter);
    }

    public void clickOnItem(Match match){
        Log.i("Click adapter","Item Clicked "+match.getGame());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();
        //Intent intent=new Intent (MainRecycler.this, MainActivity.class);
        //intent.putExtra(MATCH_SELECTED, match);
        //Poner algo de transacciones ???
        //startActivity(intent);
    }

    private void fillMatchList(){
        matchList = new ArrayList<Match>();
        matchList.add(new Match("12/10/2023 10:33","Piedra, Papel o Tijera", "Reih1", R.drawable.piedrapapeltijera));
        matchList.add(new Match("18/10/2023 11:04","Tres en Raya", "SamuDestroyer", R.drawable.tresenraya));
        matchList.add(new Match("21/10/2023 20:23","Pares o Nones", "GodAtarash1", R.drawable.paresnones));
        matchList.add(new Match("24/10/2023 11:11","Piedra, Papel o Tijera", "Depredador07", R.drawable.piedrapapeltijera));
    }
}