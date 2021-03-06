package com.example.myapplication.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Doctor  implements Serializable {

    public String nom ;
    public String prenom ;
    public String specialite ;
    public String description ;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String image ;
    public String getHoraire() {
        return Horaire;
    }

    public void setHoraire(String horaire) {
        Horaire = horaire;
    }

    String Horaire;


    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String cp ;
    public String ville;

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String adress ;
    public String facebook ;


    public String numeroTel ;

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    String gen;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }



    public Doctor(String nom, String prenom, String numeroTel) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTel = numeroTel;
    }

    public Doctor() {

    }


}
