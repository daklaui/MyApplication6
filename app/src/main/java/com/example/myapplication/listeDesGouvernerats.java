package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.Adpater_ListeDesCandidats;
import com.example.myapplication.ui.login.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class listeDesGouvernerats extends AppCompatActivity {
    private RecyclerView viewr;
    private List<String> cities = new ArrayList<>();
    LoadingDialog loadingDialog;

    String URL="";
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_des_gouvernerats);
URL=getString(R.string.urlConnection)+"/api/GetListeDesGouvernerats";
        Toolbar toolbar = findViewById(R.id.toolbargouvernerat);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Liste des gouvernorats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog=new LoadingDialog(listeDesGouvernerats.this);
         GetListeDesGouv();
        viewr=findViewById(R.id.listedesgouvernerats);
        viewr.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void GetListeDesGouv()
    {
        final Intent myIntent1 = getIntent();
        final ArrayList<String> listDesGouv= new ArrayList<>();
        loadingDialog.startLoadingDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(listeDesGouvernerats.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {

                    //  Toast.makeText(Liste_Des_Doctors.this,jsonObject.getString("VILLE_DOCTOR"),Toast.LENGTH_LONG).show();
                    try {
                        listDesGouv.add(response.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                viewr.setAdapter(new Adpater_ListeDesCandidats(listDesGouv));



                viewr.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                     GestureDetector gestureDetector = new GestureDetector(listeDesGouvernerats.this,new GestureDetector.SimpleOnGestureListener() {
                        @Override public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }
                    });

                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent motionEvent) {
                        View child = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                        if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                            int position = rv.getChildLayoutPosition(child);
                            String name = listDesGouv.get(position);
                            Intent myIntent = new Intent(listeDesGouvernerats.this, ListeDesDoctorsParVille.class);
                            myIntent.putExtra("Gouvernerat",name.trim());
                            myIntent.putExtra("Catego",myIntent1.getStringExtra("key_search"));
                            startActivity(myIntent);

                        }
                        return false;
                    }
                        @Override
                        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent motionEvent) {

                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                        }
                    });



                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                loadingDialog.fermer();
            }
        });
        Log.e("ArraryTazou",listDesGouv.toString());


        requestQueue.add(jsonArrayRequest);

        loadingDialog.fermer();


    }

    public void openPostion(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //    Toast.makeText(Liste_Des_Categories.this,adapter.getItem(position).getTitre(),Toast.LENGTH_LONG).show();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(listeDesGouvernerats.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(listeDesGouvernerats.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
            }


            final Intent myIntent1 = getIntent();
            Intent myIntent = new Intent(listeDesGouvernerats.this, FindByPosition.class);
            myIntent.putExtra("Catego",myIntent1.getStringExtra("key_search"));
            startActivity(myIntent);

        }
    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Ouvrir GPS").setCancelable(false).setPositiveButton("OUI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
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


        else
        {
            onBackPressed();
        }
        return  true;
    }
}
