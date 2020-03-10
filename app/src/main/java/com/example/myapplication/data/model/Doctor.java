package com.example.myapplication.data.model;

public class Doctor {

    public String nom ;
    public String prenom ;
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
