package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.Adpater_ListeDesCandidats2;
import com.example.myapplication.data.model.Doctor;
import com.example.myapplication.ui.login.LoadingDialog;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FindByPosition extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView emptytext;
    private static final int PHONE_CALL_REQUEST = 0;
    private static Location curLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    LocationManager locationManager;
    private LocationCallback locationCallback;
    LoadingDialog loadingDialog;
    Intent myIntent1;
    LocationRequest mLocationRequest;
    LatLng l;
    ProgressBar b;
String urlConnection="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_by_position);


            recyclerView = findViewById(R.id.recycleviewbypostion);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            emptytext = findViewById(R.id.textempty);
        emptytext.setVisibility(View.GONE);
            b=findViewById(R.id.progressBar3);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FindByPosition.this);
            loadingDialog = new LoadingDialog(FindByPosition.this);
            myIntent1 = getIntent();
            fetchlaslocation();
            loadingDialog.startLoadingDialog();
            Toolbar toolbar = findViewById(R.id.toolbarfindbyposition);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(myIntent1.getStringExtra("Catego"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            urlConnection=getString(R.string.urlConnection);
            loadingDialog.fermer();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchlaslocation();
            }
            // getCurrentLocation();
        }
    }
    private void MapActions()
    {
        String JSON_URL = urlConnection+"/api/Doctor?id=" + myIntent1.getStringExtra("Catego");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final ArrayList<Doctor> doctors = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Doctor doctor = new Doctor();
                        float[] result = new float[10];
                        doctor.setId(Integer.parseInt(jsonObject.getString("ID_DOCTOR")));
                        doctor.setNom(jsonObject.getString("NOM_DOCTOR"));
                        doctor.setPrenom(jsonObject.getString("PREN_DOCTOR"));
                        doctor.setGen(jsonObject.getString("GENRE_DOCTOR"));
                        doctor.setNumeroTel(jsonObject.getString("NUM_TEL_DOCTOR"));
                        doctor.setAdress(jsonObject.getString("ADRESSE_DOCTOR"));
                        doctor.setDescription(jsonObject.getString("DESCRIPTION"));
                        doctor.setSpecialite(jsonObject.getString("ID_CATEGORIE_DOCTOR"));
                        doctor.setFacebook(jsonObject.getString("URL_FACEBOOK"));
                        doctor.setCp(jsonObject.getString("CP_DOCTOR"));
                        doctor.setVille(jsonObject.getString("VILLE_DOCTOR"));
                        doctor.setImage(jsonObject.getString("IMAGE"));
                        doctor.setHoraire("de " + jsonObject.getString("DAY_DEBUT") + " au " + jsonObject.getString("DAY_FIn") + " : de " + jsonObject.getString("HORAIRE_DEBUT") + " a " + jsonObject.getString("HORAIRE_FIN"));
                        Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(), getLocationFromAddress(FindByPosition.this, jsonObject.getString("ADRESSE_DOCTOR")).latitude, getLocationFromAddress(FindByPosition.this, jsonObject.getString("ADRESSE_DOCTOR")).longitude, result);
                    // Location.distanceBetween(l.latitude, l.longitude, getLocationFromAddress(FindByPosition.this, jsonObject.getString("ADRESSE_DOCTOR")).latitude, getLocationFromAddress(FindByPosition.this, jsonObject.getString("ADRESSE_DOCTOR")).longitude, result);

                        if (result[0] <= 20000) {
                        doctors.add(doctor);
                          }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("errorhne", e.getMessage());

                    }
                }
                if (doctors.size() > 0) {
                     recyclerView.setVisibility(View.VISIBLE);
                     emptytext.setVisibility(View.GONE);

                    recyclerView.setAdapter(new Adpater_ListeDesCandidats2(doctors,getApplicationContext()));
                    b.setVisibility(View.GONE);
                    recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                        GestureDetector gestureDetector = new GestureDetector(FindByPosition.this, new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                return true;
                            }
                        });

                        @Override
                        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent motionEvent) {
                            View child = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                            if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                                int position = rv.getChildLayoutPosition(child);
                                Doctor doctor = doctors.get(position);
                                Intent myIntent = new Intent(FindByPosition.this, DetailleDoctor.class);
                                myIntent.putExtra("Doctor", doctor);
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
                } else {
                  recyclerView.setVisibility(View.GONE);
                     emptytext.setVisibility(View.VISIBLE);
                    b.setVisibility(View.GONE);
                }


                Log.e("Dec", String.valueOf(doctors.size()));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());

            }
        });

        requestQueue.add(jsonArrayRequest);

    }


    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actionbarfind, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_apk) {
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
        } else if (item.getItemId() == R.id.Map) {

            Intent myIntent = new Intent(FindByPosition.this, Find_Doctors.class);
            myIntent.putExtra("key_search", myIntent1.getStringExtra("Catego"));
            startActivity(myIntent);

        } else {
            onBackPressed();

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchlaslocation();
    }

    @SuppressLint("MissingPermission")
    private void fetchlaslocation() {


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(FindByPosition.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(FindByPosition.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0)
                        {int lastLocation = locationResult.getLocations().size() - 1;
                        curLocation = locationResult.getLocations().get(lastLocation);
                    MapActions();}}}, Looper.getMainLooper());

       /* LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(FindByPosition.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(FindByPosition.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getCurrentLocation();

            }
        });

        task.addOnFailureListener(FindByPosition.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(FindByPosition.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
*/
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

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

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
/*
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }*/


@SuppressLint("MissingPermission")
    private void getCurrentLocation() {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        curLocation = task.getResult();
                        if (curLocation == null) {
                            final LocationRequest locationRequest = LocationRequest.create();
                            locationRequest.setInterval(10000);
                            locationRequest.setFastestInterval(2000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    if (locationResult == null) {
                                        return;
                                    }

                                    curLocation = locationResult.getLastLocation();
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                }
                            };
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }

                        MapActions();
                    }
                    else {
                        Toast.makeText(FindByPosition.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
}
