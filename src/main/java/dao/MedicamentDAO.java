package dao;

import model.Medicament;
import exception.MedicamentNotFoundException;
import util.FileUtil;
import util.IdGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDAO {
    
    private static final String FICHIER = "medicaments.csv";
    
    public void ajouter(Medicament medicament) throws IOException {
        int nouvelId = IdGenerator.genererProchainId(FICHIER);
        medicament.setId(nouvelId);
        
        String ligne = convertirEnLigneCSV(medicament);
        FileUtil.ajouterLigne(FICHIER, ligne);
        
        System.out.println(" Médicament ajouté avec succès (ID: " + nouvelId + ")");
    }
    
    public List<Medicament> lireTous() throws IOException {
        List<String> lignes = FileUtil.lireFichier(FICHIER);
        List<Medicament> medicaments = new ArrayList<>();
        
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            Medicament medicament = convertirDepuisLigneCSV(ligne);
            medicaments.add(medicament);
        }
        
        return medicaments;
    }

    public Medicament trouverParId(int id) throws IOException, MedicamentNotFoundException {
        List<Medicament> medicaments = lireTous();
        
        for (Medicament medicament : medicaments) {
            if (medicament.getId() == id) {
                return medicament;
            }
        }
        
        throw new MedicamentNotFoundException(id);
    }

    public void modifier(Medicament medicamentModifie) throws IOException, MedicamentNotFoundException {
        List<Medicament> medicaments = lireTous();
        boolean trouve = false;
        
        for (int i = 0; i < medicaments.size(); i++) {
            if (medicaments.get(i).getId() == medicamentModifie.getId()) {
                medicaments.set(i, medicamentModifie);
                trouve = true;
                break;
            }
        }
        
        if (!trouve) {
            throw new MedicamentNotFoundException(medicamentModifie.getId());
        }
        
        List<String> lignes = new ArrayList<>();
        for (Medicament medicament : medicaments) {
            lignes.add(convertirEnLigneCSV(medicament));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Médicament modifié avec succès (ID: " + medicamentModifie.getId() + ")");
    }
    
    public void supprimer(int id) throws IOException, MedicamentNotFoundException {
        List<Medicament> medicaments = lireTous();
        boolean supprime = false;
        
        for (int i = 0; i < medicaments.size(); i++) {
            if (medicaments.get(i).getId() == id) {
                medicaments.remove(i);
                supprime = true;
                break;
            }
        }
        
        if (!supprime) {
            throw new MedicamentNotFoundException(id);
        }
        
        List<String> lignes = new ArrayList<>();
        for (Medicament medicament : medicaments) {
            lignes.add(convertirEnLigneCSV(medicament));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("✓ Médicament supprimé avec succès (ID: " + id + ")");
    }

    public List<Medicament> rechercherParNom(String nom) throws IOException {
        List<Medicament> medicaments = lireTous();
        List<Medicament> resultats = new ArrayList<>();
        
        for (Medicament medicament : medicaments) {
            if (medicament.getNom().toLowerCase().contains(nom.toLowerCase())) {
                resultats.add(medicament);
            }
        }
        
        return resultats;
    }

    private String convertirEnLigneCSV(Medicament medicament) {
        return medicament.getId() + ","
             + medicament.getNom() + ","
             + medicament.getPrix() + ","
             + medicament.getQuantiteStock() + ","
             + medicament.getDateExpiration() + ","
             + medicament.isPrescriptionRequise() + ","
             + medicament.getNomFournisseur();
    }
    

    private Medicament convertirDepuisLigneCSV(String ligne) {
        String[] tab = ligne.split(",");
        
        Medicament medicament = new Medicament();
        medicament.setId(Integer.parseInt(tab[0]));
        medicament.setNom(tab[1]);
        medicament.setPrix(Double.parseDouble(tab[2]));
        medicament.setQuantiteStock(Integer.parseInt(tab[3]));
        medicament.setDateExpiration(LocalDate.parse(tab[4]));
        medicament.setPrescriptionRequise(Boolean.parseBoolean(tab[5]));
        medicament.setNomFournisseur(tab[6]);
        
        return medicament;
    }
}