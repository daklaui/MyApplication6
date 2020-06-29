package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CustomViewPagerNoSwip;
import com.example.myapplication.ui.login.LoadingDialog;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.ui.login.Step1;
import com.example.myapplication.ui.login.Step2;
import com.example.myapplication.ui.login.Step3;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

public class Creation_CPT extends AppCompatActivity {
Button btnNext,btnSkip,btnValide;
EditText Email,Password,PasswordConfor,Prenom,Nom,Numero_Cin,Date_Naissence,Numero_Tel,Cp,Adresse;
TextView etap1,etap2,etap3;
CheckBox H,F;
    String sexe="";
    boolean test_;
    JSONObject jsonObject;
    int _code;
    private CustomViewPagerNoSwip viewPager;
     LoadingDialog loadingDialog;
     HashMap<String, Boolean>stringHashMap;
    AuthenticationPagerAdapter pagerAdapter;
String urlConnection="";
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
        urlConnection=getString(R.string.urlConnection);
        stringHashMap=new HashMap<String, Boolean>();
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
                    btnNext.setVisibility(View.VISIBLE);
                    btnValide.setVisibility(View.GONE);
                }
                else {

                    // still pages are left
                    btnNext.setText("suivant");
                    btnSkip.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnValide.setVisibility(View.GONE);
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
            final String password=Password.getText().toString();
            final String confirmpassword=PasswordConfor.getText().toString();
          //  verife(email);
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

            else
            {

                try {
loadingDialog.startLoadingDialog();
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String URL = urlConnection+"/api/Client?id="+email;
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
loadingDialog.fermer();
                                    if(response.charAt(1)=='b')
                                    {
                                        Email.setError("Adresse existe déjà!" );
                                    }
                                    else
                                    {

                                          if(password.length()<6)
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
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.w("That didn't work!",error.getMessage());
                        }
                    });

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
                catch (Exception e){
                    Log.w("That didn't work!",e.getMessage());
                }
            }




        }

     else   if(viewPager.getCurrentItem()==1)
        {


            Prenom=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.prenom);
            Nom=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.nom);
            Numero_Cin=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.cin);
            Date_Naissence=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.date_naisse);
            H=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.H);
            F=pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.F);

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
try{
    loadingDialog.startLoadingDialog();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String URL = urlConnection+"/api/Client?id="+cin;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loadingDialog.fermer();
                            if(response.charAt(1)=='b')
                            {
                                Numero_Cin.setError("Numero cin existe déjà!" );
                            }
                            else
                            {
                                if (!H.isChecked() && !F.isChecked())
                                {
                                    H.setError("");
                                    F.setError("");
                                }

                                else
                                {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w("That didn't work!",error.getMessage());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.w("That didn't work!",e.getMessage());
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
     //Cp = pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.cp);
    // Adresse = pagerAdapter.getItem(viewPager.getCurrentItem()).getView().findViewById(R.id.adress);

     final String num_tel = Numero_Tel.getText().toString();
    // final String cp = Cp.getText().toString();
   //  String adress = Adresse.getText().toString();
     if (num_tel.isEmpty() ) {
         Numero_Tel.setError("Ce champ est obligatoire!");
      //   Cp.setError("Ce champ est obligatoire!");
       //  Adresse.setError("Ce champ est obligatoire!");

     } else if (num_tel.length() < 8 || num_tel.length() > 8) {
         Numero_Tel.setError("Veuillez saisir 8 chiffre");
     }

     try {
loadingDialog.startLoadingDialog();
         RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
         String URL = urlConnection+"/api/Client?id="+num_tel;
         // Request a string response from the provided URL.
         StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
loadingDialog.fermer();
                         if(response.charAt(1)=='b')
                         {
                             Numero_Tel.setError("Numero existe déjà!" );
                         }
                         else
                         {


                                 if (InternetConnection()) {

                                     if (H.isChecked())
                                     {
                                         sexe="H";
                                     }
                                     else if(F.isChecked())
                                     {
                                         sexe="F";
                                     }

                                     /**********************************APELLE API*************************************************************/
                                     btnValide.setEnabled(false);
                                     loadingDialog.startLoadingDialog();
                                     /***************************************************SENDMESSAGECONFIRMATION***************************************************************/
                                     _code=4000;//SEND_MESSAGE(num_tel);;
                                     AlertDialog.Builder builder= new AlertDialog.Builder(Creation_CPT.this);
                                     View mview=getLayoutInflater().inflate(R.layout.dialoge_confirme_code,null);
                                     final EditText editText=mview.findViewById(R.id.editText);
                                     Button confirme=mview.findViewById(R.id.confirm_button_inscription);
                                     TextView autrecode=mview.findViewById(R.id.autrecode);

                                     confirme.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if(editText.getText().toString().isEmpty())
                                             {
                                                 editText.setError("merci de taper un code");
                                             }
                                             else if (editText.getText().length()!=4)
                                             {
                                                 editText.setError("code contient 4 chiffres");
                                             }
                                             else
                                             {
                                                 if(Integer.parseInt(editText.getText().toString())==_code)
                                                 {
                                                     Toast.makeText(Creation_CPT.this,Date_Naissence.getText().toString(),Toast.LENGTH_LONG).show();
                                                     loadingDialog.startLoadingDialog();
                                                     jsonObject=null;
                                                     jsonObject= new JSONObject();
                                                     String date_invers=Date_Naissence.getText().toString().substring(6,10)+"-"+Date_Naissence.getText().toString().substring(3,5)+"-"+Date_Naissence.getText().toString().substring(0,2);
                                                     try {
                                                         jsonObject.put("NOM_CLIENT", Nom.getText().toString());
                                                         jsonObject.put("PREN_CLIENT", Prenom.getText().toString());
                                                         jsonObject.put("CIN_CLIENT", Numero_Cin.getText().toString());
                                                         jsonObject.put("DATE_NAISS",date_invers);
                                                        // jsonObject.put("CODE_CP",Cp.getText().toString());
                                                         jsonObject.put("NUM_TEL",Numero_Tel.getText().toString());
                                                         //jsonObject.put("ADRESS_CLIENT", Cp.getText().toString()+" "+ Adresse.getText().toString());
                                                         jsonObject.put("EMAIL_CLIENT", Email.getText().toString());
                                                         jsonObject.put("MDP_CLIENT", Password.getText().toString());
                                                         jsonObject.put("GENRE_CLIENT", sexe);

                                                     } catch (JSONException e) {
                                                         e.printStackTrace();
                                                         loadingDialog.fermer();
                                                     }




                                                     Log.e("dateeee",jsonObject.toString());


                                                     String URL = urlConnection+"/api/Client";
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


                                                 }
                                                 else
                                                 {
                                                     editText.setError("merci de verifeir");
                                                 }
                                             }

                                         }
                                     });
                                     builder.setView(mview);
                                     final AlertDialog dialog=builder.create();
                                     loadingDialog.fermer();
                                     dialog.show();

                                     autrecode.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             final AlertDialog.Builder buildert= new AlertDialog.Builder(Creation_CPT.this);
                                             buildert.setMessage("Votre code de confirmation par sms a été envoyé,\n merci de patienter quelques instants s'il vous plait ");
                                             buildert.create();
                                             final AlertDialog alertDialog= buildert.show();
                                             Handler handler  = new Handler();
                                             Runnable runnable = new Runnable() {
                                                 @Override
                                                 public void run() {

                                                     if (alertDialog.isShowing()) {
                                                         alertDialog.dismiss();
                                                         _code=SEND_MESSAGE(Numero_Tel.getText().toString());
                                                         dialog.show();
                                                     }
                                                 }
                                             };
                                             handler.postDelayed(runnable, 1000);



                                         }
                                     });

                                     dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                         @Override
                                         public void onDismiss(DialogInterface dialogs) {
                                             new androidx.appcompat.app.AlertDialog.Builder(Creation_CPT.this)
                                                     .setMessage("Êtes-vous sûr de fermer cette fenêtre?,\n si oui vous avez perdu votre code de confirmation , \n merci de patienter quelques instants s'il vous plait ")
                                                     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialodg, int which) {
                                                             dialog.show();
                                                         }
                                                     })
                                                     .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             new androidx.appcompat.app.AlertDialog.Builder(Creation_CPT.this)
                                                                     .setMessage("Vous avez perdu votre code de confirmation ")
                                                                     .setPositiveButton("ok", null).create().show();
                                                             btnValide.setEnabled(true);

                                                         }


                                                     }).create().show();

                                         }
                                     });
         /* dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialogg) {
                  new androidx.appcompat.app.AlertDialog.Builder(Creation_CPT.this)
                          .setMessage("Êtes-vous sûr de fermer cette fenêtre?,\n si oui vous avez perdu votre code de confirmation , \n merci de patienter quelques instants s'il vous plait ")
                          .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogt, int which) {
                                  dialog.show();
                              }
                          })
                          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  new androidx.appcompat.app.AlertDialog.Builder(Creation_CPT.this)
                                          .setMessage("Vous avez perdu votre code de confirmation ")
                                          .setPositiveButton("ok", null).create().show();
                                  btnValide.setEnabled(true);

                              }


                          }).create().show();
              }
          });*/
                                     /***********************************************************************************************************************************************/
       /*


             /************************************************************************************************/
                                 }
                                 else
                                 {
                                     loadingDialog.fermer();
                                     Toast.makeText(Creation_CPT.this, R.string.error_connection_internet, Toast.LENGTH_SHORT).show();
                                 }




                         }

                         // Toast.makeText(Creation_CPT.this,"1"+String.valueOf(test_),Toast.LENGTH_SHORT).show();
                     }
                 }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 Log.w("That didn't work!",error.getMessage());
             }
         });

         // Add the request to the RequestQueue.
         queue.add(stringRequest);
     }
     catch (Exception e){
         Log.w("That didn't work!",e.getMessage());
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

    @Override
    public void onBackPressed() {
        this.finish();
        Intent intCloseApp = new Intent(Intent.ACTION_MAIN);
        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intCloseApp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intCloseApp.addCategory(Intent.CATEGORY_HOME);
        intCloseApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intCloseApp);

    }


    public String GETHEURSYS()
    {


        Calendar cc = Calendar.getInstance();
        int h = cc.get(Calendar.HOUR_OF_DAY);
        int Min = cc.get(Calendar.MINUTE);

        String Heure="";
        if(h<10)
        {
            Heure="0"+h;
        }
        else
        {
            Heure=""+h;
        }
        String Minute="";
 if(Min<10)
 {
     Minute="0"+Min;
 }
 else
 {
     Minute=""+Min;
 }
        // String date=day_1+"-"+Month_1+"-"+year;
        return Heure+":"+Minute;
    }


    public int GENERATERANDOMNUMBER()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(9999-1000) + 1000;
return randomNumber;
    }

    public void verife(String id)
    {
        //loadingDialog.startLoadingDialog();

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String URL = urlConnection+"/api/Client?id="+id;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if(response.charAt(1)=='b')
                            {
                               // test_=true;
                                stringHashMap.put("isExiste",true);
                               // Toast.makeText(Creation_CPT.this,String.valueOf(response.charAt(1)),Toast.LENGTH_LONG).show();

                            }
                            else
                            {

                                test_=false;
                                stringHashMap.put("isExiste",false);
                            }

                           // Toast.makeText(Creation_CPT.this,"1"+String.valueOf(test_),Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    test_=false;

                    stringHashMap.put("isExiste",false);
                    Log.w("That didn't work!",error.getMessage());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.w("That didn't work!",e.getMessage());
        }
        Log.w("hashmapmoataz!",String.valueOf(stringHashMap));
        Toast.makeText(Creation_CPT.this,"2"+String.valueOf(stringHashMap.get("isExiste")),Toast.LENGTH_SHORT).show();
//        loadingDialog.fermer();
        //return test_;
    }



public String GETDATESYS()
{
    Calendar cc = Calendar.getInstance();
    int year = cc.get(Calendar.YEAR);
    int month = cc.get(Calendar.MONTH);
    int mDay = cc.get(Calendar.DAY_OF_MONTH);

    month=month+1;
    String day_1="";
    String Month_1="";
    if(mDay<10)
    {
        day_1="0"+mDay;

    }
    else
    {
        day_1=String.valueOf(mDay);
    }
    if(month<10)
    {
        Month_1="0"+month;
    }
    else
    {
        Month_1=String.valueOf(month);
    }
   // String date=day_1+"-"+Month_1+"-"+year;
    return day_1+"/"+Month_1+"/"+year;
}
    boolean test_validté;
    int code ;
    public int SEND_MESSAGE(String num_tel)
    {
        test_validté=false;
        code= GENERATERANDOMNUMBER();
        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url ="https://www.tunisiesms.tn/client/Api/Api.aspx?fct=sms&key=AdmWpJvNJ8BOfsk3XpbZ4iDlo/-/bGP5wLGUCSIsThe4XwCYzAIBJpzUF/WEPPHdi95q170RSbmN4WzpJvlxX9Vsl/-/7YM9IRM4&mobile=216"+num_tel+"&sms="+code+"&sender=Dawini&date="+GETDATESYS()+"&heure="+GETHEURSYS();
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                         //   Toast.makeText(Creation_CPT.this,response,Toast.LENGTH_LONG).show();
                            loadingDialog.fermer();
                            test_validté=true;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w("That didn't work!",error.getMessage());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
        catch (Exception e){}


        return  code;
    }


    public  void CreateBuilder()
    {



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

