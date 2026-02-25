package dao;

import model.Pharmacien;
import exception.MedicamentNotFoundException;
import util.FileUtil;
import util.IdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PharmacienDAO {
    
    private static final String FICHIER = "pharmaciens.csv";

    public void ajouter(Pharmacien pharmacien) throws IOException {
        int nouvelId = IdGenerator.genererProchainId(FICHIER);
        pharmacien.setId(nouvelId);
        
        String ligne = convertirEnLigneCSV(pharmacien);
        FileUtil.ajouterLigne(FICHIER, ligne);
        
        System.out.println("Pharmacien ajouté avec succès (ID: " + nouvelId + ")");
    }

    public List<Pharmacien> lireTous() throws IOException {
        List<String> lignes = FileUtil.lireFichier(FICHIER);
        List<Pharmacien> pharmaciens = new ArrayList<>();
        
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            Pharmacien pharmacien = convertirDepuisLigneCSV(ligne);
            pharmaciens.add(pharmacien);
        }
        
        return pharmaciens;
    }

    public Pharmacien trouverParId(int id) throws IOException, MedicamentNotFoundException {
        List<Pharmacien> pharmaciens = lireTous();
        
        for (Pharmacien pharmacien : pharmaciens) {
            if (pharmacien.getId() == id) {
                return pharmacien;
            }
        }
        
        throw new MedicamentNotFoundException("Pharmacien avec ID " + id + " non trouvé");
    }

    public void modifier(Pharmacien pharmacienModifie) throws IOException, MedicamentNotFoundException {
        List<Pharmacien> pharmaciens = lireTous();
        boolean trouve = false;
        
        for (int i = 0; i < pharmaciens.size(); i++) {
            if (pharmaciens.get(i).getId() == pharmacienModifie.getId()) {
                pharmaciens.set(i, pharmacienModifie);
                trouve = true;
                break;
            }
        }
        
        if (!trouve) {
            throw new MedicamentNotFoundException("Pharmacien avec ID " + pharmacienModifie.getId() + " non trouvé");
        }
        
        List<String> lignes = new ArrayList<>();
        for (Pharmacien pharmacien : pharmaciens) {
            lignes.add(convertirEnLigneCSV(pharmacien));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Pharmacien modifié avec succès (ID: " + pharmacienModifie.getId() + ")");
    }

    public void supprimer(int id) throws IOException, MedicamentNotFoundException {
        List<Pharmacien> pharmaciens = lireTous();
        boolean supprime = false;
        
        for (int i = 0; i < pharmaciens.size(); i++) {
            if (pharmaciens.get(i).getId() == id) {
                pharmaciens.remove(i);
                supprime = true;
                break;
            }
        }
        
        if (!supprime) {
            throw new MedicamentNotFoundException("Pharmacien avec ID " + id + " non trouvé");
        }
        
        List<String> lignes = new ArrayList<>();
        for (Pharmacien pharmacien : pharmaciens) {
            lignes.add(convertirEnLigneCSV(pharmacien));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Pharmacien supprimé avec succès (ID: " + id + ")");
    }
    

    private String convertirEnLigneCSV(Pharmacien pharmacien) {
        return pharmacien.getId() + ","
             + pharmacien.getEmail() + ","
             + pharmacien.getPassword() + ","
             + pharmacien.getPrenom() + ","
             + pharmacien.getNom();
    }
    
 
    private Pharmacien convertirDepuisLigneCSV(String ligne) {
        String[] tab = ligne.split(",");
        
        Pharmacien pharmacien = new Pharmacien();
        pharmacien.setId(Integer.parseInt(tab[0]));
        pharmacien.setEmail(tab[1]);
        pharmacien.setPassword(tab[2]);
        pharmacien.setPrenom(tab[3]);
        pharmacien.setNom(tab[4]);
        pharmacien.setRole("PHARMACIEN");
        
        return pharmacien;
    }
}