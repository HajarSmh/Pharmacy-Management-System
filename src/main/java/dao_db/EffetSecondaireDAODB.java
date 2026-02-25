package dao_db;

import model.EffetSecondaire;
import exception.MedicamentNotFoundException;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EffetSecondaireDAODB {
    
    public void ajouter(EffetSecondaire effet) throws SQLException {
        String sql = "INSERT INTO effets_secondaires (nom, description) VALUES (?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, effet.getNom());
            pstmt.setString(2, effet.getDescription());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    effet.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Effet secondaire ajouté avec succès (ID: " + effet.getId() + ")");
        }
    }
    
    public List<EffetSecondaire> lireTous() throws SQLException {
        String sql = "SELECT * FROM effets_secondaires";
        List<EffetSecondaire> effets = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                EffetSecondaire effet = new EffetSecondaire();
                effet.setId(rs.getInt("id"));
                effet.setNom(rs.getString("nom"));
                effet.setDescription(rs.getString("description"));
                
                effets.add(effet);
            }
        }
        
        return effets;
    }
    
    public EffetSecondaire trouverParId(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "SELECT * FROM effets_secondaires WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EffetSecondaire effet = new EffetSecondaire();
                    effet.setId(rs.getInt("id"));
                    effet.setNom(rs.getString("nom"));
                    effet.setDescription(rs.getString("description"));
                    
                    return effet;
                }
            }
        }
        
        throw new MedicamentNotFoundException("Effet secondaire avec ID " + id + " non trouvé");
    }
    
    public void modifier(EffetSecondaire effet) throws SQLException, MedicamentNotFoundException {
        String sql = "UPDATE effets_secondaires SET nom=?, description=? WHERE id=?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, effet.getNom());
            pstmt.setString(2, effet.getDescription());
            pstmt.setInt(3, effet.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Effet secondaire avec ID " + effet.getId() + " non trouvé");
            }
            
            System.out.println("Effet secondaire modifié avec succès (ID: " + effet.getId() + ")");
        }
    }
    
    public void supprimer(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "DELETE FROM effets_secondaires WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Effet secondaire avec ID " + id + " non trouvé");
            }
            
            System.out.println("Effet secondaire supprimé avec succès (ID: " + id + ")");
        }
    }
}