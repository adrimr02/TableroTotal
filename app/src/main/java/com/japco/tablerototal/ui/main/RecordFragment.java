package com.japco.tablerototal.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.japco.tablerototal.MyApplication;
import com.japco.tablerototal.R;
import com.japco.tablerototal.adapters.MatchListAdapter;
import com.japco.tablerototal.model.AuthUser;
import com.japco.tablerototal.model.Match;
import com.japco.tablerototal.repositories.FirestoreRepository;
import com.japco.tablerototal.util.Dialogs;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    List<Match> matchList;
    RecyclerView recordView;

    FirestoreRepository repo = new FirestoreRepository();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fillMatchList();
        recordView = view.findViewById(R.id.recyclerViewRecord);
        recordView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recordView.setLayoutManager(layoutManager);
    }

    public void clickOnItem(Match match){
        Log.i("Click adapter","Item Clicked "+match.getGame());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();
        //Intent intent=new Intent (MainRecycler.this, MainActivity.class);
        //intent.putExtra(MATCH_SELECTED, match);
        //Poner algo de transacciones ???
        //startActivity(intent);
    }

    private void fillMatchList() {
        AuthUser user = ((MyApplication) requireActivity().getApplication()).getUser();
        repo.getHistory(user.getUserId(), new FirestoreRepository.OnFirestoreTaskComplete<List<Match>>() {
            @Override
            public void onSuccess(List<Match> matches) {
                for (Match match : matches) {
                    System.out.println(match);
                }

                // TODO añadir el historial al recycler y corregir el adapter y layout
                matchList = matches;
                MatchListAdapter matchAdapter= new MatchListAdapter(matchList, RecordFragment.this::clickOnItem);
                recordView.setAdapter(matchAdapter);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Dialogs.showInfoDialog(requireActivity(), R.string.record_fetching_error);
            }
        });
    }
}