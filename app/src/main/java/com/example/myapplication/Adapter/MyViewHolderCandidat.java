package com.example.myapplication.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.model.Doctor;

public class MyViewHolderCandidat extends RecyclerView.ViewHolder{
    public TextView textViewView;
    public TextView textViewViewadresse;
    public ImageView imageView;
   // private ImageView imageView;

    //itemView est la vue correspondante à 1 cellule
    public MyViewHolderCandidat(View itemView) {
        super(itemView);

        //c'est ici que l'on fait nos findView

        textViewView = (TextView) itemView.findViewById(R.id.Nom_doctor);
        textViewViewadresse = (TextView) itemView.findViewById(R.id.AdresseCandidat);
        imageView=itemView.findViewById(R.id.icondoctor);
       // imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
 /* public void bind(final Doctor myObject){

       // Picasso.with(imageView.getContext()).load(myObject.getImageUrl()).centerCrop().fit().into(imageView);
    }*/
}


