package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.ui.login.LoginActivity;

public class Menu extends AppCompatActivity {
CardView Appel,Dec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Appel=findViewById(R.id.Appele_Doc);
        Dec=findViewById(R.id.Dec);
        Appel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Menu.this,Liste_Des_Categories.class);
                startActivity(in);
            }
        });

        Dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Menu.this, LoginActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intCloseApp = new Intent(Intent.ACTION_MAIN);
        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intCloseApp.addCategory(Intent.CATEGORY_HOME);
        intCloseApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intCloseApp);
    }
}
