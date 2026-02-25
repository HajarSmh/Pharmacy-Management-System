package dao_db;

import model.Client;
import exception.MedicamentNotFoundException;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAODB {
    
    public void ajouter(Client client) throws SQLException {
        String sql = "INSERT INTO users (email, password, role, prenom, nom, telephone, adresse) VALUES (?, ?, 'CLIENT', ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getEmail());
            pstmt.setString(2, client.getPassword());
            pstmt.setString(3, client.getPrenom());
            pstmt.setString(4, client.getNom());
            pstmt.setString(5, client.getTelephone());
            pstmt.setString(6, client.getAdresse());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Client ajouté avec succès (ID: " + client.getId() + ")");
        }
    }

    public Client trouverParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'CLIENT'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                    );
                }
            }
        }
        
        return null; 
    }
    public List<Client> lireTous() throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 'CLIENT'";
        List<Client> clients = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setEmail(rs.getString("email"));
                client.setPassword(rs.getString("password"));
                client.setRole("CLIENT");
                client.setPrenom(rs.getString("prenom"));
                client.setNom(rs.getString("nom"));
                client.setTelephone(rs.getString("telephone"));
                client.setAdresse(rs.getString("adresse"));
                
                clients.add(client);
            }
        }
        
        return clients;
    }
    
    public Client trouverParId(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'CLIENT'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setEmail(rs.getString("email"));
                    client.setPassword(rs.getString("password"));
                    client.setRole("CLIENT");
                    client.setPrenom(rs.getString("prenom"));
                    client.setNom(rs.getString("nom"));
                    client.setTelephone(rs.getString("telephone"));
                    client.setAdresse(rs.getString("adresse"));
                    
                    return client;
                }
            }
        }
        
        throw new MedicamentNotFoundException("Client avec ID " + id + " non trouvé");
    }
    
    public void modifier(Client client) throws SQLException, MedicamentNotFoundException {
        String sql = "UPDATE users SET email=?, password=?, prenom=?, nom=?, telephone=?, adresse=? WHERE id=? AND role='CLIENT'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, client.getEmail());
            pstmt.setString(2, client.getPassword());
            pstmt.setString(3, client.getPrenom());
            pstmt.setString(4, client.getNom());
            pstmt.setString(5, client.getTelephone());
            pstmt.setString(6, client.getAdresse());
            pstmt.setInt(7, client.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Client avec ID " + client.getId() + " non trouvé");
            }
            
            System.out.println("Client modifié avec succès (ID: " + client.getId() + ")");
        }
    }
    
    public void supprimer(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'CLIENT'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Client avec ID " + id + " non trouvé");
            }
            
            System.out.println("Client supprimé avec succès (ID: " + id + ")");
        }
    }
}