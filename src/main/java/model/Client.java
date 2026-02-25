package model;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {

    private String prenom;
    private String nom;
    private String telephone;
    private String adresse;

    public Client() {
        super();
    }

    public Client(int id, String email, String motDePasse, String prenom, 
                  String nom, String telephone, String adresse) {
        super(id, email, motDePasse, "CLIENT");  
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    
    public List<Vente> consulterHistorique() {
        return new ArrayList<>();
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Client [id=" + getId() + ", prenom=" + prenom + ", nom=" + nom 
                + ", email=" + getEmail() + ", telephone=" + telephone 
                + ", adresse=" + adresse + "]";
    }
}