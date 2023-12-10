package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    RecordFragment recordFragment = new RecordFragment();
    FriendsFragment friendsFragment = new FriendsFragment();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Main activity");

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
        bottomNavigationView.setSelectedItemId(R.id.homeNavButton);
        Log.i("Navigation", String.valueOf(R.id.homeNavButton));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        System.out.println(user);
        if (user == null) {
            System.out.println("Moving to login activity");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            ((MyApplication) getApplication()).setUsername(user.getDisplayName());
            System.out.println(user.getDisplayName());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }
}