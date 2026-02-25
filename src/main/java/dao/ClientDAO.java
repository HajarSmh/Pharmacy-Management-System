package dao;

import model.Client;
import exception.MedicamentNotFoundException;
import util.FileUtil;
import util.IdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    
    private static final String FICHIER = "clients.csv";
    
    public void ajouter(Client client) throws IOException {
        int nouvelId = IdGenerator.genererProchainId(FICHIER);
        client.setId(nouvelId);
        
        String ligne = convertirEnLigneCSV(client);
        FileUtil.ajouterLigne(FICHIER, ligne);
        
        System.out.println("Client ajouté avec succès (ID: " + nouvelId + ")");
    }

    public List<Client> lireTous() throws IOException {
        List<String> lignes = FileUtil.lireFichier(FICHIER);
        List<Client> clients = new ArrayList<>();
        
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            
            Client client = convertirDepuisLigneCSV(ligne);
            clients.add(client);
        }
        
        return clients;
    }

    public Client trouverParId(int id) throws IOException, MedicamentNotFoundException {
        List<Client> clients = lireTous();
        
        for (Client client : clients) {
            if (client.getId() == id) {
                return client;
            }
        }
        
        throw new MedicamentNotFoundException("Client avec ID " + id + " non trouvé");
    }
 
    public void modifier(Client clientModifie) throws IOException, MedicamentNotFoundException {
        List<Client> clients = lireTous();
        boolean trouve = false;
        
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == clientModifie.getId()) {
                clients.set(i, clientModifie);
                trouve = true;
                break;
            }
        }
        
        if (!trouve) {
            throw new MedicamentNotFoundException("Client avec ID " + clientModifie.getId() + " non trouvé");
        }
        
        List<String> lignes = new ArrayList<>();
        for (Client client : clients) {
            lignes.add(convertirEnLigneCSV(client));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Client modifié avec succès (ID: " + clientModifie.getId() + ")");
    }

    public void supprimer(int id) throws IOException, MedicamentNotFoundException {
        List<Client> clients = lireTous();
        boolean supprime = false;
        
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId() == id) {
                clients.remove(i);
                supprime = true;
                break;
            }
        }
        
        if (!supprime) {
            throw new MedicamentNotFoundException("Client avec ID " + id + " non trouvé");
        }
        
        List<String> lignes = new ArrayList<>();
        for (Client client : clients) {
            lignes.add(convertirEnLigneCSV(client));
        }
        
        FileUtil.ecrireFichier(FICHIER, lignes);
        
        System.out.println("Client supprimé avec succès (ID: " + id + ")");
    }
 
    public List<Client> rechercherParNom(String nom) throws IOException {
        List<Client> clients = lireTous();
        List<Client> resultats = new ArrayList<>();
        
        for (Client client : clients) {
            if (client.getNom().toLowerCase().contains(nom.toLowerCase()) ||
                client.getPrenom().toLowerCase().contains(nom.toLowerCase())) {
                resultats.add(client);
            }
        }
        
        return resultats;
    }

    private String convertirEnLigneCSV(Client client) {
        return client.getId() + ","
             + client.getEmail() + ","
             + client.getPassword() + ","
             + client.getPrenom() + ","
             + client.getNom() + ","
             + client.getTelephone() + ","
             + client.getAdresse();
    }
    
    private Client convertirDepuisLigneCSV(String ligne) {
        String[] tab = ligne.split(",");
        
        Client client = new Client();
        client.setId(Integer.parseInt(tab[0]));
        client.setEmail(tab[1]);
        client.setPassword(tab[2]);
        client.setPrenom(tab[3]);
        client.setNom(tab[4]);
        client.setTelephone(tab[5]);
        client.setAdresse(tab[6]);
        client.setRole("CLIENT");
        
        return client;
    }
}