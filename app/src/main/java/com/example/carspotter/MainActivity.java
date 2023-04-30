package com.example.carspotter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.*;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    private Button btnWiki;
    private int navbarId;

    // TO DO: add code for the 2 recyclerviews to showcase latest submissions
    // newSpot & newEvent

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnItemSelectedListener(this);

        // Restore previous state
        if (savedInstanceState != null){
            navbarId = savedInstanceState.getInt("navbarId");
//            if (navbarId == R.id.item_1){
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.flFragment, homeFragment)
//                        .commit();
//            }
//            else if (navbarId == R.id.item_2){
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.flFragment, brandSelectFragment)
//                        .commit();
//            }
//            else if (navbarId == R.id.item_3){
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.flFragment, homeFragment)
//                        .commit();
//            }
        }

        if (navbarId == 0){
            bottomNavigationView.setSelectedItemId(R.id.item_1);
            navbarId = bottomNavigationView.getSelectedItemId();
        }

//        btnWiki = (Button) findViewById(R.id.btnWiki);
//
//        btnWiki.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.flFragment, brandSelectFragment)
//                        .commit();
//            }
//        });

    }
    BrandSelectFragment brandSelectFragment = new BrandSelectFragment();
    HomeFragment homeFragment = new HomeFragment();
//    ThirdFragment thirdFragment = new ThirdFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        int id = item.getItemId();
        navbarId = id;
        if (id == R.id.item_1){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
            return true;
        }
        else if (id == R.id.item_2){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, brandSelectFragment)
                    .commit();
            return true;
        }
        else if (id == R.id.item_3){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("navbarId", navbarId);
    }
}