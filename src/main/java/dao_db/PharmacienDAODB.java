package dao_db;

import model.Pharmacien;
import exception.MedicamentNotFoundException;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PharmacienDAODB {
    
    public void ajouter(Pharmacien pharmacien) throws SQLException {
        String sql = "INSERT INTO users (email, password, role, prenom, nom) VALUES (?, ?, 'PHARMACIEN', ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pharmacien.getEmail());
            pstmt.setString(2, pharmacien.getPassword());
            pstmt.setString(3, pharmacien.getPrenom());
            pstmt.setString(4, pharmacien.getNom());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pharmacien.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Pharmacien ajouté avec succès (ID: " + pharmacien.getId() + ")");
        }
    }
    
    public List<Pharmacien> lireTous() throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 'PHARMACIEN'";
        List<Pharmacien> pharmaciens = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pharmacien pharmacien = new Pharmacien();
                pharmacien.setId(rs.getInt("id"));
                pharmacien.setEmail(rs.getString("email"));
                pharmacien.setPassword(rs.getString("password"));
                pharmacien.setRole("PHARMACIEN");
                pharmacien.setPrenom(rs.getString("prenom"));
                pharmacien.setNom(rs.getString("nom"));
                
                pharmaciens.add(pharmacien);
            }
        }
        
        return pharmaciens;
    }
    
    public Pharmacien trouverParId(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'PHARMACIEN'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pharmacien pharmacien = new Pharmacien();
                    pharmacien.setId(rs.getInt("id"));
                    pharmacien.setEmail(rs.getString("email"));
                    pharmacien.setPassword(rs.getString("password"));
                    pharmacien.setRole("PHARMACIEN");
                    pharmacien.setPrenom(rs.getString("prenom"));
                    pharmacien.setNom(rs.getString("nom"));
                    
                    return pharmacien;
                }
            }
        }
        
        throw new MedicamentNotFoundException("Pharmacien avec ID " + id + " non trouvé");
    }
    
    public void modifier(Pharmacien pharmacien) throws SQLException, MedicamentNotFoundException {
        String sql = "UPDATE users SET email=?, password=?, prenom=?, nom=? WHERE id=? AND role='PHARMACIEN'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pharmacien.getEmail());
            pstmt.setString(2, pharmacien.getPassword());
            pstmt.setString(3, pharmacien.getPrenom());
            pstmt.setString(4, pharmacien.getNom());
            pstmt.setInt(5, pharmacien.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Pharmacien avec ID " + pharmacien.getId() + " non trouvé");
            }
            
            System.out.println("Pharmacien modifié avec succès (ID: " + pharmacien.getId() + ")");
        }
    }

    public Pharmacien trouverParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'PHARMACIEN'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Pharmacien(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("prenom"),
                        rs.getString("nom")
                    );
                }
            }
        }
        
        return null; 
    }
    public void supprimer(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'PHARMACIEN'";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Pharmacien avec ID " + id + " non trouvé");
            }
            
            System.out.println("Pharmacien supprimé avec succès (ID: " + id + ")");
        }
    }
}