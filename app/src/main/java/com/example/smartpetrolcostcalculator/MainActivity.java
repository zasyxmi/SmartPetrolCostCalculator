package com.example.smartpetrolcostcalculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect MainActivity.java to activity_main.xml
        setContentView(R.layout.activity_main);

        // Link BottomNavigationView from XML
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Show HomeFragment first when app starts
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_about) {
                loadFragment(new AboutFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        // Replace the FrameLayout content with selected fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}