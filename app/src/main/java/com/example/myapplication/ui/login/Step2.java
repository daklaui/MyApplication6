package com.example.myapplication.ui.login;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.myapplication.Creation_CPT;
import com.example.myapplication.R;

import java.util.Calendar;


public class Step2 extends Fragment {
EditText Date_naisse;
    CheckBox H,F;
    String Sexe="";
    DatePickerDialog.OnDateSetListener dateSetListener;
    public Step2() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_step2, container, false);
        Date_naisse=view.findViewById(R.id.date_naisse);
        H=view.findViewById(R.id.H);
        F=view.findViewById(R.id.F);
      //  Date_naisse.setInputType(InputType.TYPE_NULL);
        Date_naisse.setClickable(true);

/*******************************CheckBox***********************************************/
        H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sexe="H";
                if(H.isChecked()){
                    H.setChecked(true);
                    F.setEnabled(false);
                }else{
                    H.setChecked(false);
                    F.setEnabled(true);
                }
            }
        });

        F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sexe="F";
                if(F.isChecked()){
                    F.setChecked(true);
                    H.setEnabled(false);
                }else{
                    F.setChecked(false);
                    H.setEnabled(true);
                }
            }
        });
/************************************FIN****************************************************/


        /******************************************************/

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        /**********************************************************/
        /***************************************************************************/
        dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String day_1="";
                String Month_1="";
                if(dayOfMonth<10)
                {
                    day_1="0"+dayOfMonth;

                }
                else
                {
                    day_1=String.valueOf(dayOfMonth);
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
                Date_naisse.setText(date);
            }
        };
        /*****************************************************************************/

        Date_naisse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day
                );

                datePickerDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );
                datePickerDialog.show();
            }
        });

        return view ;
    }

}
