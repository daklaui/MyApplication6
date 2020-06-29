package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
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
import com.example.myapplication.Adapter.Adpater_ListeDesCandidats;
import com.example.myapplication.Adapter.Adpater_ListeDesCandidats2;
import com.example.myapplication.Adapter.DoctorAdpater;
import com.example.myapplication.data.model.Doctor;
import com.example.myapplication.ui.login.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListeDesDoctorsParVille extends AppCompatActivity {
RecyclerView recyclerView;
    LoadingDialog loadingDialog;
    TextView empty;
    ProgressBar b;
    String urlConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_des_doctors_par_ville);
        recyclerView=findViewById(R.id.recycle2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        empty=findViewById(R.id.textemptydoctorparvile);
        empty.setVisibility(View.GONE);
        b=findViewById(R.id.progressBar4);
        Intent myIntent = getIntent();
        urlConnection=getString(R.string.urlConnection);
        /**********************************/
        loadingDialog=new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        String JSON_URL = urlConnection+"/api/GetDoctorParGouverneratAndCat?id="+myIntent.getStringExtra("Gouvernerat")+"&id2="+myIntent.getStringExtra("Catego");
        Log.e("cchainedecone",JSON_URL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final ArrayList<Doctor> doctors = new ArrayList<>();
                // Toast.makeText(Liste_Des_Doctors.this,adress,Toast.LENGTH_LONG).show();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        //  Toast.makeText(Liste_Des_Doctors.this,jsonObject.getString("VILLE_DOCTOR"),Toast.LENGTH_LONG).show();

                            Doctor doctor = new Doctor();

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
                        doctor.setHoraire("de " +jsonObject.getString("DAY_DEBUT")+" au "+jsonObject.getString("DAY_FIn")+" : de "+jsonObject.getString("HORAIRE_DEBUT")+" a "+jsonObject.getString("HORAIRE_FIN"));

                        doctors.add(doctor);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
                Log.e("Dec", String.valueOf(doctors.size()));

                if(doctors.size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    b.setVisibility(View.GONE);
                    recyclerView.setAdapter(new Adpater_ListeDesCandidats2(doctors,getApplicationContext()));
                    recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                        GestureDetector gestureDetector = new GestureDetector(ListeDesDoctorsParVille.this, new GestureDetector.SimpleOnGestureListener() {
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
                                Intent myIntent = new Intent(ListeDesDoctorsParVille.this, DetailleDoctor.class);
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

                }
                else
                {recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    b.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                loadingDialog.fermer();
            }
        });

        requestQueue.add(jsonArrayRequest);

        loadingDialog.fermer();
        Toolbar toolbar = findViewById(R.id.toolbargouvernerat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(myIntent.getStringExtra("Catego"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
