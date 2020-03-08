package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.myapplication.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private static final int AUTO_HIDE_DELAY_MILLIS = 2 * 1000;
    private static final int UI_ANIMATION_DELAY = 300;
    private SharedPreferences sharedpreferences;
    private String MyPREFERENCES = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNextScreenAfterDelay();
    }


    private void checkNextScreenAfterDelay() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openMainActivity();

            }


        }, AUTO_HIDE_DELAY_MILLIS);

    }
    private boolean isConnected() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
       // Toast.makeText(MainActivity.this,sharedpreferences.getString("Inscription",""),Toast.LENGTH_LONG).show();
        if(sharedpreferences.getString("Inscription","")=="" )
        {
            return  true;
        }
        else
        {
            return false;
        }
    }

    private void openMainActivity() {

        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void openLoginActivity() {

        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

}
