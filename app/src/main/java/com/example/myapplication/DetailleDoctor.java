package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.data.model.Doctor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailleDoctor extends AppCompatActivity {
    SharedPreferences sharedPreferences;    JSONObject jsonObject;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location curLocation;
    LocationManager locationManager;
    LocationRequest mLocationRequest;
    TextView hidetext ;
    private static final int REQUEST_CODE= 101;
    private static final int PHONE_CALL_REQUEST = 0;
    ImageView imageView;
    String urlConnection="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaille_doctor);
        Toolbar toolbar = findViewById(R.id.toolbardetaille);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Détails");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        hidetext=findViewById(R.id.hidetext);
        hidetext.setVisibility(View.GONE);
        imageView=findViewById(R.id.imageDoctor);
        fetchlaslocation();
        Intent myIntent = getIntent();
urlConnection=getString(R.string.urlConnection);
        final Doctor doctor = (Doctor)myIntent.getSerializableExtra("Doctor");
        final TextView textView = findViewById(R.id.Nom_Pren_Doctor);
        final TextView spec = findViewById(R.id.Specialite_Doctor);
        final TextView Desc = findViewById(R.id.Descriptition);
        final TextView AdresseDoc = findViewById(R.id.AdresseDoc);
        final TextView Horaire = findViewById(R.id.Horaire);
        final Button facebook = findViewById(R.id.facebook);

       String imageurl=urlConnection+doctor.getImage();

        Picasso.with(this).load(imageurl).fit().centerInside().into(imageView);

        Button confirme = findViewById(R.id.buttonOk_appel);
        Button iter = findViewById(R.id.iter_doctor);
        TextView num_tel =findViewById(R.id.Num_Tel_Doctor);
        TextView sexe =findViewById(R.id.Sexe2_doctor);

        if(doctor.getFacebook()==null || doctor.getFacebook().contains("null") || doctor.getFacebook().isEmpty())
        {
            facebook.setVisibility(View.GONE);
        }

        textView.setText(doctor.getNom() + " " + doctor.getPrenom());
        spec.setText(doctor.getSpecialite());
        if(doctor.getDescription().isEmpty() || doctor.getDescription().contains("null"))
        {
            Desc.setText("");
        }
        else
        {
            Desc.setText(doctor.getDescription());
        }

        AdresseDoc.setText(doctor.getCp()+","+doctor.getVille().trim()+","+doctor.getAdress());
        if(!doctor.getHoraire().toLowerCase().contains("null"))
        {
            Horaire.setText(doctor.getHoraire());
        }
        else
        {
            Horaire.setText("");
        }

       /* if(doctor.getFacebook().length()>0 && doctor.getFacebook()!=null&& doctor.getFacebook()!="null"&& !doctor.getFacebook().contains("null"))
        {
            facebook.setText(doctor.getFacebook());
        }
        else
        {
            facebook.setVisibility(View.GONE);
        }*/




        num_tel.setText( doctor.getNumeroTel());
        if(doctor.getGen().contains("H"))
        {
            sexe.setText("Sexe : Homme" );
        }
        else
        {
            sexe.setText("Sexe : Femme");
        }


        confirme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("client", Context.MODE_PRIVATE);
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("ID_CLIENT_DEMANDE", sharedPreferences.getInt("Id_client", 0));
                    jsonObject.put("ID_DOCTOR_DEMANDE", doctor.getId());
                    jsonObject.put("ADRESSE_CLIENT_GPS", hidetext.getText().toString());


                } catch (JSONException e) {
                    e.printStackTrace();

                }

                String URL = urlConnection+"/api/values";
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
                                            int x = Integer.parseInt(doctor.getNumeroTel());
                                            isNumber = true;
                                        } catch (Exception e) {
                                            isNumber = false;
                                        }
                                        if (isNumber) {

                                            if (ContextCompat.checkSelfPermission(DetailleDoctor.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                //   Log.e("MoatazMessage", marker.getSnippet());
                                                ActivityCompat.requestPermissions(DetailleDoctor.this, new String[]{android.Manifest.permission.CALL_PHONE}, PHONE_CALL_REQUEST);
                                                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + marker.getSnippet()));
                                                //startActivity(intent);
                                            } else {

                                                // Log.e("MoatazMessage", marker.getSnippet());
                                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + doctor.getNumeroTel()));
                                                startActivity(intent);


                                            }
                                        }
                                    }
                                    else if(response.getString("results").contains("demi_bien"))
                                    {
                                        boolean isNumber = false;
                                        try {
                                            int x = Integer.parseInt(doctor.getNumeroTel());
                                            isNumber = true;
                                        } catch (Exception e) {
                                            isNumber = false;
                                        }
                                        if (isNumber) {

                                            if (ContextCompat.checkSelfPermission(DetailleDoctor.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                //   Log.e("MoatazMessage", marker.getSnippet());
                                                ActivityCompat.requestPermissions(DetailleDoctor.this, new String[]{android.Manifest.permission.CALL_PHONE}, PHONE_CALL_REQUEST);
                                                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + marker.getSnippet()));
                                                //startActivity(intent);
                                            } else {

                                                // Log.e("MoatazMessage", marker.getSnippet());
                                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + doctor.getNumeroTel()));
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
                                Toast.makeText(DetailleDoctor.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                                Log.e("Tableau", jsonObject.toString());
                                //  loadingDialog.fermer();
                            }
                        });
                requestQueue.add(objectRequest);

            }
        });

        iter.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(3000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationServices.getFusedLocationProviderClient(DetailleDoctor.this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient(DetailleDoctor.this).removeLocationUpdates(this);
                                if (locationResult != null && locationResult.getLocations().size() > 0)
                                {int lastLocation = locationResult.getLocations().size() - 1;
                                    curLocation = locationResult.getLocations().get(lastLocation);

                                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        //Write Function To enable gps
                                        OnGPS();
                                    }
                                    else {

                                        LatLng m;
                                        m=getLocationFromAddress(DetailleDoctor.this,doctor.getAdress());

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                                                "http://maps.google.com/maps?saddr="+curLocation.getLatitude()+","+curLocation.getLongitude()+"&daddr="+m.latitude+","+m.longitude));
                                        startActivity(intent);
                                    }




                                }}}, Looper.getMainLooper());


                // Directions

            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(doctor.getFacebook()));
                startActivity(intent);
            }
        });
       // Toast.makeText(this,imageurl,Toast.LENGTH_LONG).show();
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

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    private void createLocationRequest(){
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private  void fetchlaslocation()
    {
        if (ActivityCompat.checkSelfPermission(DetailleDoctor.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailleDoctor.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return ;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(DetailleDoctor.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(DetailleDoctor.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0)
                        {int lastLocation = locationResult.getLocations().size() - 1;
                            curLocation = locationResult.getLocations().get(lastLocation);
                            try {
                                Geocoder geocoder = new Geocoder(
                                        DetailleDoctor.this, Locale
                                        .getDefault());
                                List<Address> addresses;
                                addresses = geocoder.getFromLocation(curLocation.getLatitude(),curLocation.getLongitude(), 1);
                                Log.v("log_tag", "addresses+)_+++" + addresses);
                                hidetext.setText( addresses.get(0).getAddressLine(0));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                hidetext.setText(  "");


                            }


                            }}}, Looper.getMainLooper());

    }

    private String getAdresseName() {
        String Adresse;

        try {
            Geocoder geocoder = new Geocoder(
                    DetailleDoctor.this, Locale
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

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        finish();
        return true;
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
}
