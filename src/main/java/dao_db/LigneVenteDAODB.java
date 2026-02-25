package dao_db;

import model.LigneVente;
import exception.MedicamentNotFoundException;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneVenteDAODB {
    
    public void ajouter(LigneVente ligneVente) throws SQLException {
        String sql = "INSERT INTO lignes_vente (quantiteVendue, prixUnitaire, idVente, idMedicament) VALUES (?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ligneVente.getQuantiteVendue());
            pstmt.setDouble(2, ligneVente.getPrixUnitaire());
            pstmt.setInt(3, ligneVente.getIdVente());
            pstmt.setInt(4, ligneVente.getIdMedicament());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ligneVente.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<LigneVente> lireTous() throws SQLException {
        String sql = "SELECT * FROM lignes_vente";
        List<LigneVente> lignesVente = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                LigneVente ligneVente = new LigneVente();
                ligneVente.setId(rs.getInt("id"));
                ligneVente.setQuantiteVendue(rs.getInt("quantiteVendue"));
                ligneVente.setPrixUnitaire(rs.getDouble("prixUnitaire"));
                ligneVente.setIdVente(rs.getInt("idVente"));
                ligneVente.setIdMedicament(rs.getInt("idMedicament"));
                
                lignesVente.add(ligneVente);
            }
        }
        
        return lignesVente;
    }
    
    public List<LigneVente> trouverParVenteId(int idVente) throws SQLException {
        String sql = "SELECT * FROM lignes_vente WHERE idVente = ?";
        List<LigneVente> lignesVente = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LigneVente ligneVente = new LigneVente();
                    ligneVente.setId(rs.getInt("id"));
                    ligneVente.setQuantiteVendue(rs.getInt("quantiteVendue"));
                    ligneVente.setPrixUnitaire(rs.getDouble("prixUnitaire"));
                    ligneVente.setIdVente(rs.getInt("idVente"));
                    ligneVente.setIdMedicament(rs.getInt("idMedicament"));
                    
                    lignesVente.add(ligneVente);
                }
            }
        }
        
        return lignesVente;
    }
    
    public void supprimer(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "DELETE FROM lignes_vente WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException("Ligne de vente avec ID " + id + " non trouvée");
            }
        }
    }
    
    public void supprimerParVenteId(int idVente) throws SQLException {
        String sql = "DELETE FROM lignes_vente WHERE idVente = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            pstmt.executeUpdate();
        }
    }
}