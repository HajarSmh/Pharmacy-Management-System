package dao;

import model.Vente;
import model.LigneVente;
import exception.VenteNotFoundException;
import util.FileUtil;
import util.IdGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {
    
    private static final String FICHIER = "ventes.csv";
    private LigneVenteDAO ligneVenteDAO;
    
    public VenteDAO() {
        this.ligneVenteDAO = new LigneVenteDAO();
    }

    public void ajouter(Vente vente) throws IOException {
        int nouvelId = IdGenerator.genererProchainId(FICHIER);
        vente.setId(nouvelId);
        
        String ligne = convertirEnLigneCSV(vente);
        FileUtil.ajouterLigne(FICHIER, ligne);
        
        for (LigneVente ligneVente : vente.getLignesVente()) {
            ligneVente.setIdVente(nouvelId);
            ligneVenteDAO.ajouter(ligneVente);
        }
        
        System.out.println("Vente ajoutée avec succès (ID: " + nouvelId + ")");
    }
 
    public List<Vente> lireTous() throws IOException {
        List<String> lignes = FileUtil.lireFichier(FICHIER);
        List<Vente> ventes = new ArrayList<>();
        
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            Vente vente = convertirDepuisLigneCSV(ligne);
            
            List<LigneVente> lignesVente = ligneVenteDAO.trouverParVenteId(vente.getId());
            vente.setLignesVente(lignesVente);
            
            ventes.add(vente);
        }
        
        return ventes;
    }

    public Vente trouverParId(int id) throws IOException, VenteNotFoundException {
        List<Vente> ventes = lireTous();
        
        for (Vente vente : ventes) {
            if (vente.getId() == id) {
                return vente;
            }
        }
        
        throw new VenteNotFoundException(id);
    }

    public void modifier(Vente venteModifiee) throws IOException, VenteNotFoundException {
        List<Vente> ventes = lireTous();
        boolean trouve = false;
        
        for (int i = 0; i < ventes.size(); i++) {
            if (ventes.get(i).getId() == venteModifiee.getId()) {
                ventes.set(i, venteModifiee);
                trouve = true;
                break;
            }
        }
        
        if (!trouve) {
            throw new VenteNotFoundException(venteModifiee.getId());
        }
        
        List<String> lignes = new ArrayList<>();
        for (Vente vente : ventes) {
            lignes.add(convertirEnLigneCSV(vente));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Vente modifiée avec succès (ID: " + venteModifiee.getId() + ")");
    }
    
    public void supprimer(int id) throws IOException, VenteNotFoundException {
        List<Vente> ventes = lireTous();
        boolean supprime = false;
        
        for (int i = 0; i < ventes.size(); i++) {
            if (ventes.get(i).getId() == id) {
                ventes.remove(i);
                supprime = true;
                break;
            }
        }
        
        if (!supprime) {
            throw new VenteNotFoundException(id);
        }
        
        
        ligneVenteDAO.supprimerParVenteId(id);
        
        List<String> lignes = new ArrayList<>();
        for (Vente vente : ventes) {
            lignes.add(convertirEnLigneCSV(vente));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Vente supprimée avec succès (ID: " + id + ")");
    }
    
    public List<Vente> trouverParClientId(int idClient) throws IOException {
        List<Vente> ventes = lireTous();
        List<Vente> resultats = new ArrayList<>();
        
        for (Vente vente : ventes) {
            if (vente.getIdClient() == idClient) {
                resultats.add(vente);
            }
        }
        
        return resultats;
    }

    public List<Vente> trouverParStatut(String statut) throws IOException {
        List<Vente> ventes = lireTous();
        List<Vente> resultats = new ArrayList<>();
        
        for (Vente vente : ventes) {
            if (vente.getStatut().equals(statut)) {
                resultats.add(vente);
            }
        }
        
        return resultats;
    }

    private String convertirEnLigneCSV(Vente vente) {
        return vente.getId() + ","
             + vente.getDateVente() + ","
             + vente.getMontantTotal() + ","
             + vente.getStatut() + ","
             + vente.getIdClient() + ","
             + vente.getIdPharmacien();
    }

    private Vente convertirDepuisLigneCSV(String ligne) {
        String[] tab = ligne.split(",");
        
        Vente vente = new Vente();
        vente.setId(Integer.parseInt(tab[0]));
        vente.setDateVente(LocalDateTime.parse(tab[1]));
        vente.setMontantTotal(Double.parseDouble(tab[2]));
        vente.setStatut(tab[3]);
        vente.setIdClient(Integer.parseInt(tab[4]));
        vente.setIdPharmacien(Integer.parseInt(tab[5]));
        
        return vente;
    }
}