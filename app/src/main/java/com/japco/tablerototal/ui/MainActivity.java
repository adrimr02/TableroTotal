package com.japco.tablerototal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.japco.tablerototal.MyApplication;
import com.japco.tablerototal.R;
import com.japco.tablerototal.model.AuthUser;
import com.japco.tablerototal.repositories.FirestoreRepository;
import com.japco.tablerototal.ui.main.FriendsFragment;
import com.japco.tablerototal.ui.main.HomeFragment;
import com.japco.tablerototal.ui.main.RecordFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    final HomeFragment homeFragment = new HomeFragment();
    final RecordFragment recordFragment = new RecordFragment();
    final FriendsFragment friendsFragment = new FriendsFragment();

    final FirestoreRepository repository = new FirestoreRepository();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.i("Navigation", String.valueOf(item.getItemId()));
            if (item.getItemId() == R.id.homeNavButton) {
                replaceFragment(homeFragment);
                return true;
            } else if (item.getItemId() == R.id.recordNavButton) {
                replaceFragment(recordFragment);
                return true;
            } else if (item.getItemId() == R.id.friendsNavButton) {
                replaceFragment(friendsFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        System.out.println(user);
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            repository.getUser(user.getUid(), new FirestoreRepository.OnFirestoreTaskComplete<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot result) {
                    AuthUser user = result.toObject(AuthUser.class);
                    if (user == null || user.getUsername() == null) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return;
                    }
                    ((MyApplication) getApplication()).setUser(user);
                    System.out.println("user loaded");
                    bottomNavigationView.setSelectedItemId(R.id.homeNavButton);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }
}