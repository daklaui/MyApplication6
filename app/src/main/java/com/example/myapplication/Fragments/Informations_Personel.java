package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;


public class Informations_Personel extends Fragment {
Button btn_Save,Select_Date;
EditText nom;
EditText prenom;
EditText CIN;
    EditText Date_Nais;
    DatePickerDialog.OnDateSetListener dateSetListener;
    public HashMap data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_informations__personel, container, false);
        btn_Save=view.findViewById(R.id.Enregistrer);
        Select_Date=view.findViewById(R.id.Select_Date);
        nom=view.findViewById(R.id.Nom);
        prenom=view.findViewById(R.id.Prenom);
        CIN=view.findViewById(R.id.Cin);
       // Date_Nais=view.findViewById(R.id.Date_Nais);
        Calendar calendar = Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = new HashMap();

                data.put("NOM_CLIENT","test");
                data.put("PREN_CLIENT","test");
                data.put("CIN_CLIENT","test");
               // data.put("DATE_NAISS","22-10-2019");
               // data.put("DATE_DOC_CIN","22-10-2019");
                data.put("ADRESS_CLIENT","test");
                String URL="http://92.222.83.184:9999/api/Client";
                //JSONObject object =  new JSONObject(data);

               RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
                 JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(data),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Rest Response",response.toString());
                            }
                        },
                        new Response.ErrorListener(){

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Rest Error",error.toString());
                                Log.e("json",new JSONObject(data).toString());

                            }
                        });
               requestQueue.add(objectRequest);
            }
        });

        Select_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity().getApplicationContext(), androidx.fragment.R.style.TextAppearance_Compat_Notification,dateSetListener,year,month,day
                );
/*
                datePickerDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );*/
                datePickerDialog.show();
            }
        });
/*
        dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date=day+"/"+month+"/"+year;
                Select_Date.setText(date);
            }
        };
*/
        return view;
     //   btn_Save.findViewById(R.id.Enregistrer).setOnClickListener(this);
    }





}
