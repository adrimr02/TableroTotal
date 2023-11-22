package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    RecordFragment recordFragment = new RecordFragment();
    FriendsFragment friendsFragment = new FriendsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }
}