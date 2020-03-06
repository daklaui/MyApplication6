package com.example.myapplication;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.DoctorAdpater;
import com.example.myapplication.data.model.Doctor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Find_Doctors extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    private static final int PHONE_CALL_REQUEST=0;
    LocationManager locationManager;
    Circle mCircle;
    float Lait,Long;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        setContentView(R.layout.activity_find__doctors);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent myIntent = getIntent();
        // Add a marker in Sydney and move the camera
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        } else {
            circle();
            LatLng sydney = new LatLng(getLocation()[0], getLocation()[1]);
            LatLng sydney2 = new LatLng(36.6486671,10.5879552);


            mMap.addMarker(new MarkerOptions().position(sydney).title("My Position"));
          //  mMap.addMarker(new MarkerOptions().position(sydney2).title("Beni khaled").snippet("distence = "+result[0]));

            String JSON_URL = "http://92.222.83.184:9999/api/Doctor?id=" + myIntent.getStringExtra("key_search");
            final String adress=getAdresseName();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    ArrayList<Doctor> doctors = new ArrayList<>();
                    // Toast.makeText(Liste_Des_Doctors.this,adress,Toast.LENGTH_LONG).show();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            //  Toast.makeText(Liste_Des_Doctors.this,jsonObject.getString("VILLE_DOCTOR"),Toast.LENGTH_LONG).show();
                            if(adress.toLowerCase().contains(jsonObject.getString("VILLE_DOCTOR").toLowerCase())) {
                                Doctor doctor = new Doctor();
                                float[]result= new float[10];
                                doctor.setNom(jsonObject.getString("NOM_DOCTOR"));
                                doctor.setNumeroTel(jsonObject.getString("NUM_TEL_DOCTOR"));
                                doctors.add(doctor);
                                Location.distanceBetween(getLocation()[0],getLocation()[1],getLocationFromAddress(Find_Doctors.this,adress).latitude,getLocationFromAddress(Find_Doctors.this,adress).longitude,result);
                                Log.e("pos",adress);
                              //  Toast.makeText(Find_Doctors.this,adress,Toast.LENGTH_LONG).show();
                                if(result[0]<=3000)
                                {
                                    mMap.addMarker(new MarkerOptions().position(getLocationFromAddress(Find_Doctors.this,adress)).title("Beni khaled").snippet(doctor.getNumeroTel()));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    Log.e("Dec", String.valueOf(doctors.size()));
                    // adapter = new DoctorAdpater(Liste_Des_Doctors.this, doctors);
                    //  listView.setAdapter(adapter);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());

                }
            });

            requestQueue.add(jsonArrayRequest);


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney2,15.65f));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(Find_Doctors.this,marker.getSnippet(),Toast.LENGTH_LONG).show();
                    if (ContextCompat.checkSelfPermission(Find_Doctors.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(Find_Doctors.this, new String[]{android.Manifest.permission.CALL_PHONE},PHONE_CALL_REQUEST);
                    }
                    else
                    {
                        // Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + adapter.getItem(position).getNumeroTel()));
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + marker.getSnippet()));
                        startActivity(intent);
                    }
                return false;
                }
            });
        }

        /**********************************/


    }
    public void circle()
    {
        double radiusInMeters = 50.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        mCircle = mMap.addCircle (new CircleOptions()
                .center(new LatLng(getLocation()[0], getLocation()[1]))
                .radius(3000)
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(1));
    }
    private double[] getLocation() {

        //Check Permissions again
        double aDouble[] = new double[2];
        if (ActivityCompat.checkSelfPermission(Find_Doctors.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Find_Doctors.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return  null;
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
                aDouble[0]=lat;
                aDouble[1]=longi;
                String CityName = "";

                //---------------
                Geocoder geocoder = new Geocoder(
                        Find_Doctors.this, Locale
                        .getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(lat, longi, 1);
                    Log.v("log_tag", "addresses+)_+++" + addresses);
                    CityName = addresses.get(0).getAddressLine(0);
                    Log.v("log_tag", "CityName" + CityName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return aDouble;
                //************************
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi =LocationNetwork.getLongitude();
                //   latitude=String.valueOf(lat);
                aDouble[0]=lat;
                aDouble[1]=longi;
                String CityName = "";
                //---------------
                Geocoder geocoder = new Geocoder(
                        Find_Doctors.this, Locale
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
                return aDouble;
                //************************
            }
            //  showLocationTxt.setText("Votre localisation: " + CityName);}
            else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi =LocationPassive.getLongitude();
                aDouble[0]=lat;
                aDouble[1]=longi;
                String CityName = "";
                //---------------

                Geocoder geocoder = new Geocoder(
                        Find_Doctors.this, Locale
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
                return aDouble;
            }

            else {
                double lat = 36.6338484; //LocationGps.getLatitude();
                double longi =10.570989; //LocationGps.getLongitude();
                // latitude=String.valueOf(lat);
                // longitude=String.valueOf(longi);
                aDouble[0]=lat;
                aDouble[1]=longi;
                String CityName = "";
                //---------------
                Geocoder geocoder = new Geocoder(
                        Find_Doctors.this, Locale
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
                return aDouble;
            }

        }

    }



    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(Find_Doctors.this);

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



    private String getAdresseName() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(Find_Doctors.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Find_Doctors.this,
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
                        Find_Doctors.this, Locale
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
                        Find_Doctors.this, Locale
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
                        Find_Doctors.this, Locale
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
                        Find_Doctors.this, Locale
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

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}
