package dao;

import model.EffetSecondaire;
import exception.MedicamentNotFoundException;
import util.FileUtil;
import util.IdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EffetSecondaireDAO {
    
    private static final String FICHIER = "effets_secondaires.csv";

    public void ajouter(EffetSecondaire effet) throws IOException {
        int nouvelId = IdGenerator.genererProchainId(FICHIER);
        effet.setId(nouvelId);
        
        String ligne = convertirEnLigneCSV(effet);
        FileUtil.ajouterLigne(FICHIER, ligne);
        
        System.out.println("Effet secondaire ajouté avec succès (ID: " + nouvelId + ")");
    }

    public List<EffetSecondaire> lireTous() throws IOException {
        List<String> lignes = FileUtil.lireFichier(FICHIER);
        List<EffetSecondaire> effets = new ArrayList<>();
        
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            EffetSecondaire effet = convertirDepuisLigneCSV(ligne);
            effets.add(effet);
        }
        
        return effets;
    }
 
    public EffetSecondaire trouverParId(int id) throws IOException, MedicamentNotFoundException {
        List<EffetSecondaire> effets = lireTous();
        
        for (EffetSecondaire effet : effets) {
            if (effet.getId() == id) {
                return effet;
            }
        }
        
        throw new MedicamentNotFoundException("Effet secondaire avec ID " + id + " non trouvé");
    }

    public void modifier(EffetSecondaire effetModifie) throws IOException, MedicamentNotFoundException {
        List<EffetSecondaire> effets = lireTous();
        boolean trouve = false;
        
        for (int i = 0; i < effets.size(); i++) {
            if (effets.get(i).getId() == effetModifie.getId()) {
                effets.set(i, effetModifie);
                trouve = true;
                break;
            }
        }
        
        if (!trouve) {
            throw new MedicamentNotFoundException("Effet secondaire avec ID " + effetModifie.getId() + " non trouvé");
        }
        
        List<String> lignes = new ArrayList<>();
        for (EffetSecondaire effet : effets) {
            lignes.add(convertirEnLigneCSV(effet));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Effet secondaire modifié avec succès (ID: " + effetModifie.getId() + ")");
    }

    public void supprimer(int id) throws IOException, MedicamentNotFoundException {
        List<EffetSecondaire> effets = lireTous();
        boolean supprime = false;
        
        for (int i = 0; i < effets.size(); i++) {
            if (effets.get(i).getId() == id) {
                effets.remove(i);
                supprime = true;
                break;
            }
        }
        
        if (!supprime) {
            throw new MedicamentNotFoundException("Effet secondaire avec ID " + id + " non trouvé");
        }
        
        List<String> lignes = new ArrayList<>();
        for (EffetSecondaire effet : effets) {
            lignes.add(convertirEnLigneCSV(effet));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Effet secondaire supprimé avec succès (ID: " + id + ")");
    }
    

    private String convertirEnLigneCSV(EffetSecondaire effet) {
        return effet.getId() + ","
             + effet.getNom() + ","
             + effet.getDescription();
    }

    private EffetSecondaire convertirDepuisLigneCSV(String ligne) {
        String[] tab = ligne.split(",");
        
        EffetSecondaire effet = new EffetSecondaire();
        effet.setId(Integer.parseInt(tab[0]));
        effet.setNom(tab[1]);
        effet.setDescription(tab[2]);
        
        return effet;
    }
}