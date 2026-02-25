package util;

import model.LigneVente;
import java.util.ArrayList;
import java.util.List;

public class Panier {
    
    private static Panier instance;
    private List<LigneVente> lignes;

    private Panier() {
        this.lignes = new ArrayList<>();
    }
    
    public static Panier getInstance() {
        if (instance == null) {
            instance = new Panier();
        }
        return instance;
    }
    
    public void ajouter(LigneVente ligne) {
        boolean existe = false;
        for (LigneVente l : lignes) {
            if (l.getIdMedicament() == ligne.getIdMedicament()) {
            	
                l.setQuantiteVendue(l.getQuantiteVendue() + ligne.getQuantiteVendue());
                existe = true;
                break;
            }
        }

        if (!existe) {
            lignes.add(ligne);
        }
    }

    public void retirer(LigneVente ligne) {
        lignes.remove(ligne);
    }

    public void vider() {
        lignes.clear();
    }
    
    public List<LigneVente> getLignes() {
        return lignes;
    }

    public double calculerTotal() {
        return lignes.stream()
                .mapToDouble(LigneVente::calculerTotalLigne)
                .sum();
    }

    public boolean estVide() {
        return lignes.isEmpty();
    }

    public int getNombreArticles() {
        return lignes.stream()
                .mapToInt(LigneVente::getQuantiteVendue)
                .sum();
    }
}