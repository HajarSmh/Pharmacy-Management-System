package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Medicament {
    private int id;
    private String nom;
    private double prix;
    private int quantiteStock;
    private LocalDate dateExpiration;
    private boolean prescriptionRequise;
    private String nomFournisseur;
    private boolean actif;  
    private List<EffetSecondaire> effetsSecondaires;
    
    public Medicament() {
        this.effetsSecondaires = new ArrayList<>();
        this.actif = true; 
    }
    
    public Medicament(int id, String nom, double prix, int quantiteStock,
                      LocalDate dateExpiration, boolean prescriptionRequise, 
                      String nomFournisseur) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.dateExpiration = dateExpiration;
        this.prescriptionRequise = prescriptionRequise;
        this.nomFournisseur = nomFournisseur;
        this.actif = true; 
        this.effetsSecondaires = new ArrayList<>();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Medicament other = (Medicament) obj;
        return id == other.id;
    }
    
    @Override
    public String toString() {
        return "Medicament [id=" + id + ", nom=" + nom + ", prix=" + prix + 
               ", quantiteStock=" + quantiteStock + ", dateExpiration=" + dateExpiration + 
               ", prescriptionRequise=" + prescriptionRequise + ", nomFournisseur=" + nomFournisseur + 
               ", actif=" + actif + "]";
    }

    public int getId() {
        return id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public int getQuantiteStock() {
        return quantiteStock;
    }
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    
    public boolean isPrescriptionRequise() {
        return prescriptionRequise;
    }
    
    public String getNomFournisseur() {
        return nomFournisseur;
    }
    
    public boolean isActif() {  
        return actif;
    }
    
    public List<EffetSecondaire> getEffetsSecondaires() {
        return effetsSecondaires;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }
    
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public void setPrescriptionRequise(boolean prescriptionRequise) {
        this.prescriptionRequise = prescriptionRequise;
    }
    
    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    public void setEffetsSecondaires(List<EffetSecondaire> effetsSecondaires) {
        this.effetsSecondaires = effetsSecondaires;
    }

    
    public boolean verifierDateExpiration() {
        return LocalDate.now().isBefore(dateExpiration);
    }
    
    public void mettreAJourStock(int quantite) {
        this.quantiteStock += quantite;
    }
    
    public boolean estDisponible() {
        return quantiteStock > 0 && verifierDateExpiration() && actif; 
    }
    
    public void ajouterEffetSecondaire(EffetSecondaire effet) {
        if (!effetsSecondaires.contains(effet)) {
            effetsSecondaires.add(effet);
        }
    }
}