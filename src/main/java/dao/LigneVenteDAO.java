package dao;

import model.LigneVente;
import exception.MedicamentNotFoundException;
import util.FileUtil;
import util.IdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LigneVenteDAO {
    
    private static final String FICHIER = "lignes_vente.csv";

    public void ajouter(LigneVente ligneVente) throws IOException {
        int nouvelId = IdGenerator.genererProchainId(FICHIER);
        ligneVente.setId(nouvelId);
        
        String ligne = convertirEnLigneCSV(ligneVente);
        FileUtil.ajouterLigne(FICHIER, ligne);
    }

    public List<LigneVente> lireTous() throws IOException {
        List<String> lignes = FileUtil.lireFichier(FICHIER);
        List<LigneVente> lignesVente = new ArrayList<>();
        
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            LigneVente ligneVente = convertirDepuisLigneCSV(ligne);
            lignesVente.add(ligneVente);
        }
        
        return lignesVente;
    }

    public LigneVente trouverParId(int id) throws IOException, MedicamentNotFoundException {
        List<LigneVente> lignesVente = lireTous();
        
        for (LigneVente ligneVente : lignesVente) {
            if (ligneVente.getId() == id) {
                return ligneVente;
            }
        }
        
        throw new MedicamentNotFoundException("Ligne de vente avec ID " + id + " non trouvée");
    }

    public List<LigneVente> trouverParVenteId(int idVente) throws IOException {
        List<LigneVente> lignesVente = lireTous();
        List<LigneVente> resultats = new ArrayList<>();
        
        for (LigneVente ligneVente : lignesVente) {
            if (ligneVente.getIdVente() == idVente) {
                resultats.add(ligneVente);
            }
        }
        
        return resultats;
    }

    public void modifier(LigneVente ligneVenteModifiee) throws IOException, MedicamentNotFoundException {
        List<LigneVente> lignesVente = lireTous();
        boolean trouve = false;
        
        for (int i = 0; i < lignesVente.size(); i++) {
            if (lignesVente.get(i).getId() == ligneVenteModifiee.getId()) {
                lignesVente.set(i, ligneVenteModifiee);
                trouve = true;
                break;
            }
        }
        
        if (!trouve) {
            throw new MedicamentNotFoundException("Ligne de vente avec ID " + ligneVenteModifiee.getId() + " non trouvée");
        }
        
        List<String> lignes = new ArrayList<>();
        for (LigneVente ligneVente : lignesVente) {
            lignes.add(convertirEnLigneCSV(ligneVente));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
    }

    public void supprimer(int id) throws IOException, MedicamentNotFoundException {
        List<LigneVente> lignesVente = lireTous();
        boolean supprime = false;
        
        for (int i = 0; i < lignesVente.size(); i++) {
            if (lignesVente.get(i).getId() == id) {
                lignesVente.remove(i);
                supprime = true;
                break;
            }
        }
        
        if (!supprime) {
            throw new MedicamentNotFoundException("Ligne de vente avec ID " + id + " non trouvée");
        }
        
        List<String> lignes = new ArrayList<>();
        for (LigneVente ligneVente : lignesVente) {
            lignes.add(convertirEnLigneCSV(ligneVente));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
    }

    public void supprimerParVenteId(int idVente) throws IOException {
        List<LigneVente> lignesVente = lireTous();
        List<LigneVente> aGarder = new ArrayList<>();
        
        for (LigneVente ligneVente : lignesVente) {
            if (ligneVente.getIdVente() != idVente) {
                aGarder.add(ligneVente);
            }
        }
        
        List<String> lignes = new ArrayList<>();
        for (LigneVente ligneVente : aGarder) {
            lignes.add(convertirEnLigneCSV(ligneVente));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
    }

    private String convertirEnLigneCSV(LigneVente ligneVente) {
        return ligneVente.getId() + ","
             + ligneVente.getQuantiteVendue() + ","
             + ligneVente.getPrixUnitaire() + ","
             + ligneVente.getIdVente() + ","
             + ligneVente.getIdMedicament();
    }
    
    private LigneVente convertirDepuisLigneCSV(String ligne) {
        String[] tab = ligne.split(",");
        
        LigneVente ligneVente = new LigneVente();
        ligneVente.setId(Integer.parseInt(tab[0]));
        ligneVente.setQuantiteVendue(Integer.parseInt(tab[1]));
        ligneVente.setPrixUnitaire(Double.parseDouble(tab[2]));
        ligneVente.setIdVente(Integer.parseInt(tab[3]));
        ligneVente.setIdMedicament(Integer.parseInt(tab[4]));
        
        return ligneVente;
    }
}
