package com.example.myapplication.ui.login;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
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
import com.example.myapplication.Creation_CPT;
import com.example.myapplication.Creation_Compte;
import com.example.myapplication.Liste_Des_Categories;
import com.example.myapplication.Liste_Des_Doctors;
import com.example.myapplication.Menu;
import com.example.myapplication.R;
import com.example.myapplication.data.model.Doctor;
import com.example.myapplication.ui.login.LoginViewModel;
import com.example.myapplication.ui.login.LoginViewModelFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    JSONObject jsonObject;
    private LoginViewModel loginViewModel;
SharedPreferences sharedpreferences;
TextView register_now;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register_now=findViewById(R.id.register_now);

        register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Creation_CPT.class));
            }
        });
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        sharedpreferences = getSharedPreferences("client", Context.MODE_PRIVATE);
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        if(sharedpreferences.getBoolean("Connect",false))
        {
            startActivity(new Intent(LoginActivity.this,Liste_Des_Categories.class));
        }

final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                String strAccount = usernameEditText.getText().toString();
                String strPassword = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(strAccount.trim())) {
                    loadingDialog.fermer();
                    usernameEditText.setError("Ce nom d \\'utilisateur n\\'est pas valide");
                    Toast.makeText(LoginActivity.this, R.string.error_username, Toast.LENGTH_SHORT).show();

                    return;
                }
                else if (TextUtils.isEmpty(strPassword.trim())) {
                    loadingDialog.fermer();
                    passwordEditText.setError("Ce mot de passe n\\'est pas valide");
                    Toast.makeText(LoginActivity.this, R.string.error_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(strAccount.length()!=8){
                    usernameEditText.setError("Veuillez saisir un numero contient 8 chiffres" );
                    loadingDialog.fermer();
                }
                else  if(passwordEditText.length()<6)
                {
                    passwordEditText.setError("Veuillez saisir au moins 6 caractères" );
                    loadingDialog.fermer();
                }
                else if (InternetConnection()) {
                    jsonObject= new JSONObject();
                    try {

                        jsonObject.put("NUM_TEL", usernameEditText.getText().toString());
                        jsonObject.put("MDP_CLIENT", passwordEditText.getText().toString());

                    } catch (JSONException e) {
                        loadingDialog.fermer();
                        e.printStackTrace();
                    }

                    String JSON_URL = "http://51.83.72.59:9999/api/Connect";
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String r="";
                                    try {
                                        r=response.getString("results");
                                    }
                                    catch (JSONException e){e.printStackTrace(); loadingDialog.fermer();}

                                    // Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();

                                    if(!r.contains("Not"))
                                    {
                                        loadingDialog.fermer();

                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                         editor.putInt("Id_client",Integer.parseInt(r));
                                        editor.putBoolean("Connect",true);
                                          editor.commit();
                                        startActivity(new Intent(LoginActivity.this, Liste_Des_Categories.class));
                                    }
                                    else
                                    {
                                        loadingDialog.fermer();
                                        Toast.makeText(LoginActivity.this, "Merci de verifier votre login", Toast.LENGTH_SHORT).show();
                                        usernameEditText.setError("Merci de verifier votre login");
                                        passwordEditText.setError("Merci de verifier votre mot de passe" );
                                    }
                                    //  sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                    //  SharedPreferences.Editor editor = sharedpreferences.edit();
                                    //  editor.putString("Inscription","true");
                                    //  editor.commit();


                                }
                            },
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loadingDialog.fermer();
                                    usernameEditText.setError("Merci de verifier votre login");
                                    passwordEditText.setError("Merci de verifier votre mot de passe" );

                                    Log.e("Tableau",jsonObject.toString());

                                }
                            });
                    requestQueue.add(objectRequest);

                }
                else
                {
                    loadingDialog.fermer();
                    Toast.makeText(LoginActivity.this, R.string.error_connection_internet, Toast.LENGTH_SHORT).show();
                }
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
    public boolean InternetConnection()
    {
        boolean flag=false;
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        flag=true;

                    }

        }
        if(flag==true)
        {
           return  true;
        }
        else
        {
          return  false;
        }


    }

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    }


