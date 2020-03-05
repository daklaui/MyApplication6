package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CustomViewPagerNoSwip;
import com.example.myapplication.ui.login.LoadingDialog;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.ui.login.Step1;
import com.example.myapplication.ui.login.Step2;
import com.example.myapplication.ui.login.Step3;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Creation_CPT extends AppCompatActivity {
Button btnNext,btnSkip,btnValide;
EditText Email,Password,PasswordConfor,Prenom,Nom,Numero_Cin,Date_Naissence,Numero_Tel,Cp,Adresse;
TextView etap1,etap2,etap3;

    JSONObject jsonObject;
    private CustomViewPagerNoSwip viewPager;
     LoadingDialog loadingDialog;
    AuthenticationPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation__cpt);
        pagerAdapter  = new AuthenticationPagerAdapter(getSupportFragmentManager());
        viewPager=(CustomViewPagerNoSwip)findViewById(R.id.viewPage);
        btnNext=findViewById(R.id.btnNext);
        btnSkip=findViewById(R.id.btnSkip);
        btnValide=findViewById(R.id.btnvValide);
        btnSkip.setVisibility(View.GONE);
        pagerAdapter.addFragmet(new Step1());
        pagerAdapter.addFragmet(new Step2());
        pagerAdapter.addFragmet(new Step3());
        viewPager.setAdapter(pagerAdapter);
        loadingDialog = new LoadingDialog(Creation_CPT.this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == pagerAdapter.getCount() - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.VISIBLE);
                    btnValide.setVisibility(View.VISIBLE);
                }

               else if(position==0)
                {
                    btnSkip.setVisibility(View.GONE);
                }
                else {

                    // still pages are left
                    btnNext.setText("suivant");
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    public void next_fragment(View view) {

        if(viewPager.getCurrentItem()==0)
        {
            Email=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.email);
            Password=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.password);
            PasswordConfor=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.confirmpassword);
            String email=Email.getText().toString();
            String password=Password.getText().toString();
            String confirmpassword=PasswordConfor.getText().toString();
            if(email.isEmpty()|| password.isEmpty() || confirmpassword.isEmpty() )
            {
               // Toast.makeText(Creation_CPT.this,"merci de verifier",Toast.LENGTH_LONG).show();
                Email.setError("Ce champ est obligatoire!" );
                Password.setError("Ce champ est obligatoire!" );
                PasswordConfor.setError("Ce champ est obligatoire!" );
            }
            else if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()){
                Email.setError("Veuillez saisir une adresse email valide. Par exemple prenom.nom@domaine.com!" );
            }
            else  if(password.length()<6)
            {
                Password.setError("Veuillez saisir au moins 6 caractères" );
            }
            else  if(!password.equals(confirmpassword))
            {
                PasswordConfor.setError("Mot de passe ne correspondant pas!" );
            }

            else
            {

                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        }

     else   if(viewPager.getCurrentItem()==1)
        {


            Prenom=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.prenom);
            Nom=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.nom);
            Numero_Cin=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.cin);
            Date_Naissence=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.date_naisse);



/****************************************************************************************/

            String prenom=Prenom.getText().toString();
            String nom=Nom.getText().toString();
            String cin=Numero_Cin.getText().toString();
            String date_nais=Date_Naissence.getText().toString();
            if(prenom.isEmpty()|| nom.isEmpty() || cin.isEmpty() || date_nais.isEmpty() )
            {
                Prenom.setError("Ce champ est obligatoire!" );
                Nom.setError("Ce champ est obligatoire!" );
                Numero_Cin.setError("Ce champ est obligatoire!" );
                Date_Naissence.setError("Ce champ est obligatoire!" );
                //Toast.makeText(Creation_CPT.this,"merci de verifier",Toast.LENGTH_LONG).show();
            }
            else  if(cin.length()<8 || cin.length()>8)
            {
                Numero_Cin.setError("Veuillez saisir 8 chiffre" );
            }

            else
            {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        }



    }


    public void previous_fragment(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }

    public void valide_form(View view) {

    /************************************************************************************************************/
 if(viewPager.getCurrentItem()==2) {
     Numero_Tel = pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.num_tel);
     Cp = pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.cp);
     Adresse = pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.adress);

     String num_tel = Numero_Tel.getText().toString();
     String cp = Cp.getText().toString();
     String adress = Adresse.getText().toString();
     if (num_tel.isEmpty() || cp.isEmpty() || adress.isEmpty()) {
         Numero_Tel.setError("Ce champ est obligatoire!");
         Cp.setError("Ce champ est obligatoire!");
         Adresse.setError("Ce champ est obligatoire!");

         Toast.makeText(Creation_CPT.this, "merci de verifier", Toast.LENGTH_LONG).show();
     } else if (num_tel.length() < 8 || num_tel.length() > 8) {
         Numero_Tel.setError("Veuillez saisir 8 chiffre");
     } else if (cp.length() < 4 || cp.length() > 4) {
         Cp.setError("Veuillez saisir 4 chiffre");
     } else {
         if (InternetConnection()) {


             /**********************************APELLE API*************************************************************/
             loadingDialog.startLoadingDialog();

    jsonObject= new JSONObject();
    try {
        jsonObject.put("NOM_CLIENT", Nom.getText().toString());
        jsonObject.put("PREN_CLIENT", Prenom.getText().toString());
        jsonObject.put("CIN_CLIENT", Numero_Cin.getText().toString());
        jsonObject.put("DATE_NAISS", Date_Naissence.getText().toString());
        jsonObject.put("ADRESS_CLIENT", Cp.getText().toString()+" "+ Adresse.getText().toString());
        jsonObject.put("EMAIL_CLIENT", Email.getText().toString());
        jsonObject.put("MDP_CLIENT", Password.getText().toString());

    } catch (JSONException e) {
        e.printStackTrace();
        loadingDialog.fermer();
    }


    Log.e("date",jsonObject.toString());


    String URL = "http://92.222.83.184:9999/api/Client";
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    startActivity(new Intent(Creation_CPT.this, LoginActivity.class));
                    loadingDialog.fermer();
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Creation_CPT.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Tableau",jsonObject.toString());
                    loadingDialog.fermer();
                }
            });
    requestQueue.add(objectRequest);
             /************************************************************************************************/
         }
           else
             {
                 loadingDialog.fermer();
                 Toast.makeText(Creation_CPT.this, R.string.error_connection_internet, Toast.LENGTH_SHORT).show();
             }

         }
     }
     /*******************************************************************************************************************/


 }


    public boolean InternetConnection()
    {
        boolean flag=false;
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        flag=true;

                    }

        }
        if(flag==true)
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }
}



class AuthenticationPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public AuthenticationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    void addFragmet(Fragment fragment) {
        fragmentList.add(fragment);
    }
}