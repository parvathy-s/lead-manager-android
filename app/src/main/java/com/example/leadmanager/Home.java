package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView usr = (TextView) findViewById(R.id.usr);
        SessionManagement sessionManagement = new SessionManagement(Home.this);
        String isLoggedIn = sessionManagement.getSession();
        usr.setText(isLoggedIn);
    }

    public void onLogout(View view) {
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