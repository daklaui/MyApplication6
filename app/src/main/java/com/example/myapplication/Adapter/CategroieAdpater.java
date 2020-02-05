package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.data.model.Categorie;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CategroieAdpater extends ArrayAdapter<Categorie> {

    private ArrayList<Categorie> listData;
    private LayoutInflater layoutInflater;
    public CategroieAdpater(Context aContext, ArrayList<Categorie> listDat) {
        super(aContext,R.layout.template_liste_categories,listDat);
        this.listData = listDat;
    }

    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Categorie dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.template_liste_categories, parent, false);
            viewHolder.Titre = (TextView) convertView.findViewById(R.id.Titre);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        lastPosition = position;

        viewHolder.Titre.setText(dataModel.getTitre());
        // Return the completed view to render on screen
        return convertView;
    }
    static class ViewHolder {
        TextView Titre;

    }
}
