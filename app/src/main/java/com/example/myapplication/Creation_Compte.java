package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Creation_Compte extends AppCompatActivity {
    Button btn_Save;
    EditText nom;
    TextView Select_Date;
    EditText prenom;
    EditText CIN;
    EditText Email;
    EditText Adress;
    EditText MotDePass;
    EditText cp;
    DatePickerDialog.OnDateSetListener dateSetListener;
    JSONObject jsonObject;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation__compte);
        /******************************************************/
        btn_Save = findViewById(R.id.Enregistrer);
        Select_Date = (TextView)findViewById(R.id.Select_Date);
        nom = findViewById(R.id.Nom);
        prenom = findViewById(R.id.Prenom);
        CIN = findViewById(R.id.Cin);
        Email=findViewById(R.id.Email);
        Adress=findViewById(R.id.Adresse);
        cp=findViewById(R.id.Code_Postal);
        MotDePass=findViewById(R.id.Password);
        /******************************************************/

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        /**********************************************************/



        // Spinner click listener
      //  cp.setOnItemSelectedListener();

        /***********************************************************/
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonObject= new JSONObject();
                try {
                    jsonObject.put("NOM_CLIENT", nom.getText().toString());
                    jsonObject.put("PREN_CLIENT", prenom.getText().toString());
                    jsonObject.put("CIN_CLIENT", CIN.getText().toString());
                    jsonObject.put("DATE_NAISS", Select_Date.getText().toString());
                    jsonObject.put("ADRESS_CLIENT", cp.getText().toString()+" "+ Adress.getText().toString());
                    jsonObject.put("EMAIL_CLIENT", Email.getText().toString());
                    jsonObject.put("MDP_CLIENT", MotDePass.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.e("date",jsonObject.toString());


                String URL = "http://92.222.83.184:9999/api/Client";
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                startActivity(new Intent(Creation_Compte.this,LoginActivity.class));

                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Creation_Compte.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Tableau",jsonObject.toString());

                            }
                        });
                requestQueue.add(objectRequest);
            }
        });


        Select_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Creation_Compte.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day
                );

                datePickerDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );
                datePickerDialog.show();
            }
        });

        dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String day_1="";
                String Month_1="";
                if(day<10)
                {
                    day_1="0"+day;
                }
                else
                {
                    day_1=String.valueOf(day);
                }
                if(month<10)
                {
                    Month_1="0"+month;
                }
                else
                {
                    Month_1=String.valueOf(month);
                }
                String date=day_1+"-"+Month_1+"-"+year;
                Select_Date.setText(date);
            }
        };


    }
}
