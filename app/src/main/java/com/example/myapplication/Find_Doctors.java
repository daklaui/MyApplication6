package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.myapplication.data.model.Doctor;
import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.BubbleIconFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Find_Doctors extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    private static final int PHONE_CALL_REQUEST = 0;
    Location curLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE= 101;
    LocationManager locationManager;
    boolean IsSave = false;
    Circle mCircle;
    JSONObject jsonObject;
    float Lait, Long;
    SharedPreferences sharedPreferences;
LocationListener locationListener;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__doctors);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar=findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        fetchlaslocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



    }
private  void fetchlaslocation()
{
    if (ActivityCompat.checkSelfPermission(Find_Doctors.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Find_Doctors.this,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        return ;
    }
    Task<Location> task=fusedLocationProviderClient.getLastLocation();
    task.addOnSuccessListener(new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if(location!=null)
            {
                curLocation=location;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(Find_Doctors.this);
            }
        }
    });

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
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("Êtes-vous sûr de vouloir sortir ?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           Find_Doctors.this.finish();
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
        return  true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent myIntent = getIntent();
        // Add a marker in Sydney and move the camera


            circle();
            final LatLng sydney = new LatLng(curLocation.getLatitude(), curLocation.getLongitude());


            mMap.addMarker(new MarkerOptions().position(sydney).title("My Position"));

            String JSON_URL = "http://51.83.72.59:9999/api/Doctor?id=" + myIntent.getStringExtra("key_search");
           // final String adress=getAdresseName();
             final HashMap<Marker, Doctor> markerIdMapping = new HashMap<>();
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
                          //  if(adress.toLowerCase().contains(jsonObject.getString("VILLE_DOCTOR").toLowerCase())) {
                                Doctor doctor = new Doctor();
                                float[]result= new float[10];
                                doctor.setId(Integer.parseInt(jsonObject.getString("ID_DOCTOR")));
                                doctor.setNom(jsonObject.getString("NOM_DOCTOR"));
                                doctor.setPrenom(jsonObject.getString("PREN_DOCTOR"));
                                doctor.setGen(jsonObject.getString("GENRE_DOCTOR"));
                                doctor.setNumeroTel(jsonObject.getString("NUM_TEL_DOCTOR"));
                                doctor.setAdress(jsonObject.getString("CP_DOCTOR")+" "+jsonObject.getString("VILLE_DOCTOR")+" "+jsonObject.getString("ADRESSE_DOCTOR"));
                                doctor.setDescription(jsonObject.getString("DESCRIPTION"));
                                doctor.setSpecialite(jsonObject.getString("ID_CATEGORIE_DOCTOR"));
                                doctor.setFacebook(jsonObject.getString("URL_FACEBOOK"));
                                doctors.add(doctor);
                              //  Log.e("POSITIONMOATAZ",jsonObject.getString("ADRESSE_DOCTOR"));
                                Location.distanceBetween(curLocation.getLatitude(),curLocation.getLongitude(),getLocationFromAddress(Find_Doctors.this,jsonObject.getString("ADRESSE_DOCTOR")).latitude,getLocationFromAddress(Find_Doctors.this,jsonObject.getString("ADRESSE_DOCTOR")).longitude,result);

                              //  Toast.makeText(Find_Doctors.this,adress,Toast.LENGTH_LONG).show();
   if(result[0]<=20000) {

       markerIdMapping.put(mMap.addMarker(new MarkerOptions().position(getLocationFromAddress(Find_Doctors.this, jsonObject.getString("ADRESSE_DOCTOR"))).icon(BitmapDescriptorFactory.fromBitmap(createStoreMarker(doctor.getNom()+" "+doctor.getPrenom())))), doctor);
   }

                           // }
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









            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,13.65f));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    try {
                        final Doctor dc = markerIdMapping.get(marker);


                        AlertDialog.Builder builder = new AlertDialog.Builder(Find_Doctors.this);
                        View mview = getLayoutInflater().inflate(R.layout.markerdetaille, null);
                        final TextView textView = mview.findViewById(R.id.Nom_Doctor);
                        final TextView spec = mview.findViewById(R.id.Specialite);
                        final TextView Desc = mview.findViewById(R.id.Desc);
                        final TextView facebook = mview.findViewById(R.id.facebook);

                        Button confirme = mview.findViewById(R.id.buttonOk);
                        Button iter = mview.findViewById(R.id.iter);
                        TextView num_tel = mview.findViewById(R.id.Num_Tel);
                        TextView sexe = mview.findViewById(R.id.Sexe2);
                        textView.setText("Nom et Prenom : " + dc.getNom() + " " + dc.getPrenom());
                        spec.setText(dc.getSpecialite());
                        Desc.setText(dc.getDescription());
                        if(dc.getFacebook().length()>0 && dc.getFacebook()!=null&& dc.getFacebook()!="null"&& !dc.getFacebook().contains("null"))
                        {
                            facebook.setText(dc.getFacebook());
                        }
                        else
                        {
                            facebook.setVisibility(View.GONE);
                        }


                        num_tel.setText("Numero de téléphone : " + dc.getNumeroTel());
                        sexe.setText("Sexe : " + dc.getGen());
                        builder.setView(mview);
                        AlertDialog dialog = builder.create();
                        //loadingDialog.fermer();
                        dialog.show();

                        confirme.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sharedPreferences = getSharedPreferences("client", Context.MODE_PRIVATE);
                                jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("ID_CLIENT_DEMANDE", sharedPreferences.getInt("Id_client", 0));
                                    Doctor dc = markerIdMapping.get(marker);
                                    jsonObject.put("ID_DOCTOR_DEMANDE", dc.getId());
                                    jsonObject.put("ADRESSE_CLIENT_GPS", getAdresseName());


                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }

                                String URL = "http://51.83.72.59:9999/api/values";
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getString("results").contains("bien")) {
                                                        Log.e("MoatazMessage", response.getString("results"));
                                                        boolean isNumber = false;
                                                        try {
                                                            int x = Integer.parseInt(dc.getNumeroTel());
                                                            isNumber = true;
                                                        } catch (Exception e) {
                                                            isNumber = false;
                                                        }
                                                        if (isNumber) {

                                                            if (ContextCompat.checkSelfPermission(Find_Doctors.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                             //   Log.e("MoatazMessage", marker.getSnippet());
                                                                ActivityCompat.requestPermissions(Find_Doctors.this, new String[]{android.Manifest.permission.CALL_PHONE}, PHONE_CALL_REQUEST);
                                                                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + marker.getSnippet()));
                                                                //startActivity(intent);
                                                            } else {

                                                               // Log.e("MoatazMessage", marker.getSnippet());
                                                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dc.getNumeroTel()));
                                                                startActivity(intent);


                                                            }
                                                        }
                                                    }
                                                    else if(response.getString("results").contains("demi_bien"))
                                                    {
                                                        boolean isNumber = false;
                                                        try {
                                                            int x = Integer.parseInt(dc.getNumeroTel());
                                                            isNumber = true;
                                                        } catch (Exception e) {
                                                            isNumber = false;
                                                        }
                                                        if (isNumber) {

                                                            if (ContextCompat.checkSelfPermission(Find_Doctors.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                                //   Log.e("MoatazMessage", marker.getSnippet());
                                                                ActivityCompat.requestPermissions(Find_Doctors.this, new String[]{android.Manifest.permission.CALL_PHONE}, PHONE_CALL_REQUEST);
                                                                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + marker.getSnippet()));
                                                                //startActivity(intent);
                                                            } else {

                                                                // Log.e("MoatazMessage", marker.getSnippet());
                                                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dc.getNumeroTel()));
                                                                startActivity(intent);


                                                            }
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(Find_Doctors.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                                                Log.e("Tableau", jsonObject.toString());
                                                //  loadingDialog.fermer();
                                            }
                                        });
                                requestQueue.add(objectRequest);

                            }
                        });

                        iter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Directions
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                                        "http://maps.google.com/maps?saddr="+sydney.latitude+","+sydney.longitude+"&daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude));
                                startActivity(intent);
                            }
                        });












                   /* Toast.makeText(Find_Doctors.this,marker.getSnippet(),Toast.LENGTH_LONG).show();
                    // Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + adapter.getItem(position).getNumeroTel()));
                  /*
*/


                        return false;
                    }
                    catch (Exception e)
                    {
                        return false;
                    }
                }
            });


        /**********************************/


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    fetchlaslocation();
                }
                break;
        }
    }

    private Bitmap createStoreMarker(String name) {
        View markerLayout = getLayoutInflater().inflate(R.layout.stroe_marker_layout, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        markerImage.setImageResource(R.drawable.doctor);
        markerRating.setText(name);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

    public void circle()
    {
        double radiusInMeters = 50.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        mCircle = mMap.addCircle (new CircleOptions()
                .center(new LatLng(curLocation.getLatitude(),curLocation.getLongitude()))
                .radius(20000)
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(1));
    }

/*
    private double[] getLocation() {

        final double aDouble[] = new double[2];
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                aDouble[0]=location.getLatitude();
                aDouble[1]=location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(Find_Doctors.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Find_Doctors.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return  null;
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }



        return  aDouble;
    }
    /*private double[] getLocation() {

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



*/

    private String getAdresseName() {
        String Adresse;

        try {
            Geocoder geocoder = new Geocoder(
                    Find_Doctors.this, Locale
                    .getDefault());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(curLocation.getLatitude(),curLocation.getLongitude(), 1);
            Log.v("log_tag", "addresses+)_+++" + addresses);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
return  "";


        }

    }


    /*
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
            }

        }

    }*/

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
