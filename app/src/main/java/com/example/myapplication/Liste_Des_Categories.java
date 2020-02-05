package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CategroieAdpater;
import com.example.myapplication.Adapter.DoctorAdpater;
import com.example.myapplication.data.model.Categorie;
import com.example.myapplication.data.model.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Liste_Des_Categories extends AppCompatActivity {
    ListView listView;
    private static final int REQUEST_LOCATION = 1;
    ArrayList<Categorie> doctors;
    Button getlocationBtn;
    LocationManager locationManager;
    TextView mypos;
    private static CategroieAdpater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add permission

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        setContentView(R.layout.activity_liste__des__categories);
        listView = (ListView) findViewById(R.id.listcategories);
         getlocationBtn= findViewById(R.id.pos);
        mypos=findViewById(R.id.mypos);
        doctors = new ArrayList<>();
        /**********************************/
        String JSON_URL = "http://92.222.83.184:9999/api/Categorie";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Categorie> categories = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Categorie categorie = new Categorie();
                        categorie.setTitre(jsonObject.getString("TITRE_CATEGORIE"));

                        categories.add(categorie);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Volleyt", e.toString());
                    }
                }
                Log.e("Tab", String.valueOf(categories.size()));

                adapter = new CategroieAdpater(Liste_Des_Categories.this, categories);
                listView.setAdapter(adapter);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());

            }
        });

        requestQueue.add(jsonArrayRequest);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    Toast.makeText(Liste_Des_Categories.this,adapter.getItem(position).getTitre(),Toast.LENGTH_LONG).show();

                    //GPS is already On then
                  // Toast.makeText(Liste_Des_Categories.this,   mypos.getText().length(),Toast.LENGTH_LONG).show();

                       if(mypos.getText().length()!=0) {
                           Intent myIntent = new Intent(Liste_Des_Categories.this, Liste_Des_Doctors.class);
                           myIntent.putExtra("key_search", adapter.getItem(position).getTitre());
                           myIntent.putExtra("Adresse", mypos.getText().toString());
                           startActivity(myIntent);
                       }
                       else
                       {
                           Toast.makeText(Liste_Des_Categories.this, "Merci verifier", Toast.LENGTH_SHORT).show();
                       }




            }
        });

        getlocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Write Function To enable gps

                    OnGPS();
                } else {
                    mypos.setText(getLocation());
                    //GPS is already On then
                    //  Toast.makeText(Liste_Des_Categories.this, getLocation(),Toast.LENGTH_LONG).show();
                  //  Intent myIntent = new Intent(Liste_Des_Categories.this, Liste_Des_Doctors.class);
                    //myIntent.putExtra("key_search", adapter.getItem(position).getTitre());
                   // myIntent.putExtra("Adresse", getLocation());
                   // startActivity(myIntent);

                }
            }
        });
    }



    private String getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(Liste_Des_Categories.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Liste_Des_Categories.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return  "t";
        }
        else {
            Location LocationGps =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive =locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps != null) {
                double lat = LocationGps.getLatitude();
                double longi =LocationGps.getLongitude();
                // latitude=String.valueOf(lat);
                // longitude=String.valueOf(longi);
                String CityName = "";
                //---------------
                Geocoder geocoder = new Geocoder(
                        Liste_Des_Categories.this, Locale
                        .getDefault());
                List<Address> addresses;
                try {
                    //  Log.v("log_tag", "latitude" + latitude);
                    // Log.v("log_tag", "longitude" + longitude);
                    addresses = geocoder.getFromLocation(lat, longi, 1);
                    Log.v("log_tag", "addresses+)_+++" + addresses);
                    CityName = addresses.get(0).getAddressLine(0);
                    Log.v("log_tag", "CityName" + CityName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return CityName;
                //************************
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi =LocationNetwork.getLongitude();
                //   latitude=String.valueOf(lat);
//
                String CityName = "";
                //---------------
                Geocoder geocoder = new Geocoder(
                        Liste_Des_Categories.this, Locale
                        .getDefault());
                List<Address> addresses;
                try {
                    // Log.v("log_tag", "latitude" + latitude);
                    // Log.v("log_tag", "longitude" + longitude);
                    addresses = geocoder.getFromLocation(lat, longi, 1);
                    Log.v("log_tag", "addresses+)_+++" + addresses);
                    CityName = addresses.get(0).getAddressLine(0);
                    Log.v("log_tag", "CityName" + CityName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return CityName;
                //************************
            }
            //  showLocationTxt.setText("Votre localisation: " + CityName);}
            else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi =LocationPassive.getLongitude();
                // latitude=String.valueOf(lat);
                // longitude=String.valueOf(longi);
                String CityName = "";
                //---------------

                Geocoder geocoder = new Geocoder(
                        Liste_Des_Categories.this, Locale
                        .getDefault());
                List<Address> addresses;
                try {
                    //  Log.v("log_tag", "latitude" + latitude);
                    //  Log.v("log_tag", "longitude" + longitude);
                    addresses = geocoder.getFromLocation(lat, longi, 1);
                    Log.v("log_tag", "addresses+)_+++" + addresses);
                    CityName = addresses.get(0).getCountryName();
                    Log.v("log_tag", "CityName" + CityName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //************************
                return CityName;
            }

            else {
                double lat = 36.6338484; //LocationGps.getLatitude();
                double longi =10.570989; //LocationGps.getLongitude();
                // latitude=String.valueOf(lat);
                // longitude=String.valueOf(longi);
                String CityName = "";
                //---------------
                Geocoder geocoder = new Geocoder(
                        Liste_Des_Categories.this, Locale
                        .getDefault());
                List<Address> addresses;
                try {
                    //  Log.v("log_tag", "latitude" + latitude);
                    // Log.v("log_tag", "longitude" + longitude);
                    addresses = geocoder.getFromLocation(lat, longi, 1);
                    Log.v("log_tag", "addresses+)_+++" + addresses);
                    CityName = addresses.get(0).getAddressLine(0);

                    Log.v("log_tag", "CityName" + CityName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return CityName;
            }

        }

    }



    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Liste_Des_Categories.super.onBackPressed();
                    }


                }).create().show();
    }
}
