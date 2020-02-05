package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.DoctorAdpater;
import com.example.myapplication.data.model.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Liste_Des_Doctors extends AppCompatActivity {
    ListView listView;
    private static final int PHONE_CALL_REQUEST=0;
    DoctorAdpater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste__des__doctors);
        listView = (ListView) findViewById(R.id.list);

        Intent myIntent = getIntent();
        /**********************************/


        String JSON_URL = "http://92.222.83.184:9999/api/Doctor?id=" + myIntent.getStringExtra("key_search");
       final String adress=myIntent.getStringExtra("Adresse");

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
                         doctor.setNom(jsonObject.getString("NOM_DOCTOR"));
                         doctor.setNumeroTel(jsonObject.getString("NUM_TEL_DOCTOR"));
                         doctors.add(doctor);
                   }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
                Log.e("Dec", String.valueOf(doctors.size()));
                adapter = new DoctorAdpater(Liste_Des_Doctors.this, doctors);
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
                // Toast.makeText(Liste_Des_Doctors.this,"eeeee",Toast.LENGTH_LONG).show();
                //Toast.makeText(Liste_Des_Doctors.this,String.valueOf(checkSelfPermission(Manifest.permission.CALL_PHONE)) ,Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(Liste_Des_Doctors.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Liste_Des_Doctors.this, new String[]{android.Manifest.permission.CALL_PHONE},PHONE_CALL_REQUEST);
                }
                else
                {
                  // Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + adapter.getItem(position).getNumeroTel()));
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + adapter.getItem(position).getNumeroTel()));
                    startActivity(intent);
                }

            }
        });
    }



}
