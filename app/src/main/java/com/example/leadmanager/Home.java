package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private String isLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        drawer= findViewById(R.id.drawer_layout);
        NavigationView navigationView= findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SessionManagement sessionManagement = new SessionManagement(Home.this);
        isLoggedIn = sessionManagement.getSession();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        AccountFragment accountFragment = AccountFragment.newInstance(isLoggedIn);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,accountFragment).commit();
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    SessionManagement sessionManagement = new SessionManagement(Home.this);
                    String username = sessionManagement.getSession();
                    switch (item.getItemId()){
                        case R.id.nav_account:
                            selectedFragment = AccountFragment.newInstance(username);
                            break;
                        case R.id.nav_contact:
                            selectedFragment = new ContactFragment();
                            break;
                        case R.id.nav_opportunity:
                            selectedFragment = new OpportunityFragment();
                            break;
                        case R.id.nav_lead:
                            selectedFragment = new LeadFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).addToBackStack(null).commit();
                    return true;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                ProfileFragment profileFragment= ProfileFragment.newInstance(isLoggedIn);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profileFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_logout:
                onLogout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    private void onLogout() {
        SessionManagement sessionManagement= new SessionManagement(Home.this);
        sessionManagement.removeSession();
        openLogin();
    }

    private void openLogin() {
        Intent intent = new Intent(Home.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}