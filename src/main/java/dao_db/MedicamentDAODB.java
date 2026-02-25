package dao_db;

import model.Medicament;
import exception.MedicamentNotFoundException;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDAODB {

    public void ajouter(Medicament medicament) throws SQLException {
        String sql = "INSERT INTO medicaments (nom, prix, quantiteStock, dateExpiration, prescriptionRequise, nomFournisseur, actif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        //                                                                                                                                    ↑ AJOUTÉ
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, medicament.getNom());
            pstmt.setDouble(2, medicament.getPrix());
            pstmt.setInt(3, medicament.getQuantiteStock());
            pstmt.setDate(4, Date.valueOf(medicament.getDateExpiration()));
            pstmt.setBoolean(5, medicament.isPrescriptionRequise());
            pstmt.setString(6, medicament.getNomFournisseur());
            pstmt.setBoolean(7, medicament.isActif()); 
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    medicament.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Médicament ajouté avec succès (ID: " + medicament.getId() + ")");
        }
    }

    public List<Medicament> lireTous() throws SQLException {
        String sql = "SELECT * FROM medicaments";
        List<Medicament> medicaments = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicament medicament = new Medicament();
                medicament.setId(rs.getInt("id"));
                medicament.setNom(rs.getString("nom"));
                medicament.setPrix(rs.getDouble("prix"));
                medicament.setQuantiteStock(rs.getInt("quantiteStock"));
                medicament.setDateExpiration(rs.getDate("dateExpiration").toLocalDate());
                medicament.setPrescriptionRequise(rs.getBoolean("prescriptionRequise"));
                medicament.setNomFournisseur(rs.getString("nomFournisseur"));
                medicament.setActif(rs.getBoolean("actif")); 
                
                medicaments.add(medicament);
            }
        }
        
        return medicaments;
    }

    public Medicament trouverParId(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "SELECT * FROM medicaments WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Medicament medicament = new Medicament();
                    medicament.setId(rs.getInt("id"));
                    medicament.setNom(rs.getString("nom"));
                    medicament.setPrix(rs.getDouble("prix"));
                    medicament.setQuantiteStock(rs.getInt("quantiteStock"));
                    medicament.setDateExpiration(rs.getDate("dateExpiration").toLocalDate());
                    medicament.setPrescriptionRequise(rs.getBoolean("prescriptionRequise"));
                    medicament.setNomFournisseur(rs.getString("nomFournisseur"));
                    medicament.setActif(rs.getBoolean("actif")); 
                    
                    return medicament;
                }
            }
        }
        
        throw new MedicamentNotFoundException(id);
    }

    public void modifier(Medicament medicament) throws SQLException, MedicamentNotFoundException {
        String sql = "UPDATE medicaments SET nom=?, prix=?, quantiteStock=?, dateExpiration=?, prescriptionRequise=?, nomFournisseur=?, actif=? WHERE id=?";

        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicament.getNom());
            pstmt.setDouble(2, medicament.getPrix());
            pstmt.setInt(3, medicament.getQuantiteStock());
            pstmt.setDate(4, Date.valueOf(medicament.getDateExpiration()));
            pstmt.setBoolean(5, medicament.isPrescriptionRequise());
            pstmt.setString(6, medicament.getNomFournisseur());
            pstmt.setBoolean(7, medicament.isActif()); 
            pstmt.setInt(8, medicament.getId()); 
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException(medicament.getId());
            }
            
            System.out.println("Médicament modifié avec succès (ID: " + medicament.getId() + ")");
        }
    }

    public void supprimer(int id) throws SQLException, MedicamentNotFoundException {
        String sql = "DELETE FROM medicaments WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new MedicamentNotFoundException(id);
            }
            
            System.out.println("Médicament supprimé avec succès (ID: " + id + ")");
        }
    }

    public List<Medicament> rechercherParNom(String nom) throws SQLException {
        String sql = "SELECT * FROM medicaments WHERE nom LIKE ?";
        List<Medicament> medicaments = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medicament medicament = new Medicament();
                    medicament.setId(rs.getInt("id"));
                    medicament.setNom(rs.getString("nom"));
                    medicament.setPrix(rs.getDouble("prix"));
                    medicament.setQuantiteStock(rs.getInt("quantiteStock"));
                    medicament.setDateExpiration(rs.getDate("dateExpiration").toLocalDate());
                    medicament.setPrescriptionRequise(rs.getBoolean("prescriptionRequise"));
                    medicament.setNomFournisseur(rs.getString("nomFournisseur"));
                    medicament.setActif(rs.getBoolean("actif"));
                    
                    medicaments.add(medicament);
                }
            }
        }
        
        return medicaments;
    }
}