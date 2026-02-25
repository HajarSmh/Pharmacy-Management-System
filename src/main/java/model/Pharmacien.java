package model;

public class Pharmacien extends User {

    private String prenom;
    private String nom;

    public Pharmacien() {
        super();
    }

    public Pharmacien(int id, String email, String motDePasse, 
                      String prenom, String nom) {
        super(id, email, motDePasse, "PHARMACIEN");
        this.prenom = prenom;
        this.nom = nom;
    }


    public void gererStock() {
        System.out.println("Gestion du stock par " + prenom + " " + nom);
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

    @Override
    public String toString() {
        return "Pharmacien [id=" + getId() + ", prenom=" + prenom 
                + ", nom=" + nom + ", email=" + getEmail() + "]";
    }

     public boolean validerVente(Vente vente) {
         if (vente == null) {
             return false;
         }
         vente.setStatut("VALIDEE");
         return true;
     }
}