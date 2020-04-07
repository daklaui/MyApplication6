package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.model.Doctor;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adpater_ListeDesCandidats2 extends RecyclerView.Adapter<MyViewHolderCandidat> {



    List<Doctor> list;Context mContext;
    public Adpater_ListeDesCandidats2(List<Doctor> list, Context mContext ){
        this.list = list;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MyViewHolderCandidat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.templatedetaillegouvernerat,parent,false);
        return new MyViewHolderCandidat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderCandidat holder, int position) {
        Doctor myObject = list.get(position);
        holder.textViewView.setText(myObject.getNom()+" "+myObject.getPrenom());
        holder.textViewViewadresse.setText(myObject.getAdress());
        Picasso.with(mContext).load("http://51.83.72.59"+myObject.getImage()).fit().centerInside().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
