package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.NetworkError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CategroieAdpater;
import com.example.myapplication.Adapter.DoctorAdpater;
import com.example.myapplication.data.model.Categorie;
import com.example.myapplication.data.model.Doctor;
import com.example.myapplication.ui.login.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Liste_Des_Categories extends AppCompatActivity {
    ListView listView;

    ArrayList<Categorie> doctors;
    Button getlocationBtn;
    LocationManager locationManager;
    TextView mypos;
    private static CategroieAdpater adapter;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add permission


        setContentView(R.layout.activity_liste__des__categories);
        listView = (ListView) findViewById(R.id.listcategories);
        loadingDialog = new LoadingDialog(Liste_Des_Categories.this);
        //getlocationBtn= findViewById(R.id.pos);
        //mypos=findViewById(R.id.mypos);
        doctors = new ArrayList<>();
        /**********************************/
        loadingDialog.startLoadingDialog();
        GetApi();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(Liste_Des_Categories.this, listeDesGouvernerats.class);
                    myIntent.putExtra("key_search", adapter.getItem(position).getTitre());
                    startActivity(myIntent);

            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Les spécialités médicales");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      if(item.getItemId()==R.id.logout_apk)
      {
          new AlertDialog.Builder(this)
                  .setMessage("Êtes-vous sûr de vouloir sortir ?")
                  .setNegativeButton(android.R.string.no, null)
                  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                      @Override
                      public void onClick(DialogInterface dialog, int which) {

                          Intent intCloseApp = new Intent(Intent.ACTION_MAIN);
                          intCloseApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          intCloseApp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                          intCloseApp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                          intCloseApp.addCategory(Intent.CATEGORY_HOME);
                          intCloseApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          startActivity(intCloseApp);
                          finishAffinity();
                          System.exit(0);
                      }


                  }).create().show();
      }
      return  true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Êtes-vous sûr de vouloir sortir ?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Liste_Des_Categories.this.finish();
                        Intent intCloseApp = new Intent(Intent.ACTION_MAIN);
                        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intCloseApp.addCategory(Intent.CATEGORY_HOME);
                        intCloseApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intCloseApp);
                    }


                }).create().show();
    }
    private void GetApi()
    {
        String JSON_URL = "http://51.83.72.59:9999/api/Categorie";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                ArrayList<Categorie> categories = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Categorie categorie = new Categorie();
                        categorie.setTitre(jsonObject.getString("TITRE_CATEGORIE").toUpperCase());

                        categories.add(categorie);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Volleyt", e.toString());
                    }
                }
                Log.e("Tab", String.valueOf(categories.size()));

                adapter = new CategroieAdpater(Liste_Des_Categories.this, categories);
                listView.setAdapter(adapter);
                loadingDialog.fermer();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    loadingDialog.fermer();

                    int errorCode = 0;
                    if (error instanceof TimeoutError) {
                        errorCode = -7;

                    } else if (error instanceof NoConnectionError) {
                        errorCode = -1;
                    } else if (error instanceof AuthFailureError) {
                        errorCode = -6;
                    } else if (error instanceof ServerError) {
                        errorCode = 0;
                    } else if (error instanceof NetworkError) {
                        errorCode = -1;
                    } else if (error instanceof ParseError) {
                        errorCode = -8;
                    }
                    Toast.makeText(Liste_Des_Categories.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
                //
            }
        });

        requestQueue.add(jsonArrayRequest);
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
}

