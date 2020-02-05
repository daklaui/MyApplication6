package com.example.myapplication.ui.login;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.DoctorAdpater;
import com.example.myapplication.Creation_Compte;
import com.example.myapplication.Liste_Des_Categories;
import com.example.myapplication.Liste_Des_Doctors;
import com.example.myapplication.R;
import com.example.myapplication.data.model.Doctor;
import com.example.myapplication.ui.login.LoginViewModel;
import com.example.myapplication.ui.login.LoginViewModelFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    JSONObject jsonObject;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonObject= new JSONObject();
                try {

                    jsonObject.put("EMAIL_CLIENT", usernameEditText.getText().toString());
                    jsonObject.put("MDP_CLIENT", passwordEditText.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("tab",jsonObject.toString());
                String JSON_URL = "http://92.222.83.184:9999/api/Connect";
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String r="";
                                try {
                                    r=response.getString("results");
                                }
                                catch (JSONException e){e.printStackTrace();}

                               // Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();

                                if(r.contains("bien"))
                                {
                                    startActivity(new Intent(LoginActivity.this, Liste_Des_Categories.class));
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "Merci de verifier votre login", Toast.LENGTH_SHORT).show();
                                }
                                // sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                //  SharedPreferences.Editor editor = sharedpreferences.edit();
                                // editor.putString("Inscription","true");
                                //  editor.commit();


                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Tableau",jsonObject.toString());

                            }
                        });
                requestQueue.add(objectRequest);
            }
        });

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
           super.finish();
        System.exit(0);
    }
    }


