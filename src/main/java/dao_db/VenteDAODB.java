package dao_db;

import model.Vente;
import model.LigneVente;
import exception.VenteNotFoundException;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VenteDAODB {
    
    private LigneVenteDAODB ligneVenteDAO;
    
    public VenteDAODB() {
        this.ligneVenteDAO = new LigneVenteDAODB();
    }
    
    public void ajouter(Vente vente) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        
        try {

            conn.setAutoCommit(false);

            String sqlVente = "INSERT INTO ventes (dateVente, montantTotal, statut, idClient, idPharmacien) VALUES (?, ?, ?, ?, ?)";
            
            int venteId;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(vente.getDateVente()));
                pstmt.setDouble(2, vente.getMontantTotal());
                pstmt.setString(3, vente.getStatut());
                pstmt.setInt(4, vente.getIdClient());
                pstmt.setInt(5, vente.getIdPharmacien());
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venteId = generatedKeys.getInt(1);
                        vente.setId(venteId);
                    } else {
                        throw new SQLException("Échec de création de vente, aucun ID généré");
                    }
                }
            }
            
            for (LigneVente ligne : vente.getLignesVente()) {
                ligne.setIdVente(venteId);
                ligneVenteDAO.ajouter(ligne);

                String sqlUpdateStock = "UPDATE medicaments SET quantiteStock = quantiteStock - ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateStock)) {
                    pstmt.setInt(1, ligne.getQuantiteVendue());
                    pstmt.setInt(2, ligne.getIdMedicament());
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Médicament ID " + ligne.getIdMedicament() + " non trouvé");
                    }
                }
            }
            

            conn.commit();
            System.out.println(" Vente ajoutée avec succès (ID: " + venteId + ")");
            
        } catch (SQLException e) {

            conn.rollback();
            System.err.println(" Erreur lors de la création de vente : " + e.getMessage());
            throw e;
            
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Vente> lireTous() throws SQLException {
        String sql = "SELECT * FROM ventes";
        List<Vente> ventes = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Vente vente = new Vente();
                vente.setId(rs.getInt("id"));
                vente.setDateVente(rs.getTimestamp("dateVente").toLocalDateTime());
                vente.setMontantTotal(rs.getDouble("montantTotal"));
                vente.setStatut(rs.getString("statut"));
                vente.setIdClient(rs.getInt("idClient"));
                vente.setIdPharmacien(rs.getInt("idPharmacien"));
                
                List<LigneVente> lignesVente = ligneVenteDAO.trouverParVenteId(vente.getId());
                vente.setLignesVente(lignesVente);
                
                ventes.add(vente);
            }
        }
        
        return ventes;
    }
    
    public Vente trouverParId(int id) throws SQLException, VenteNotFoundException {
        String sql = "SELECT * FROM ventes WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Vente vente = new Vente();
                    vente.setId(rs.getInt("id"));
                    vente.setDateVente(rs.getTimestamp("dateVente").toLocalDateTime());
                    vente.setMontantTotal(rs.getDouble("montantTotal"));
                    vente.setStatut(rs.getString("statut"));
                    vente.setIdClient(rs.getInt("idClient"));
                    vente.setIdPharmacien(rs.getInt("idPharmacien"));

                    List<LigneVente> lignesVente = ligneVenteDAO.trouverParVenteId(vente.getId());
                    vente.setLignesVente(lignesVente);
                    
                    return vente;
                }
            }
        }
        
        throw new VenteNotFoundException(id);
    }

    public void modifier(Vente vente) throws SQLException, VenteNotFoundException {
        String sql = "UPDATE ventes SET dateVente=?, montantTotal=?, statut=?, idClient=?, idPharmacien=? WHERE id=?";
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(vente.getDateVente()));
            pstmt.setDouble(2, vente.getMontantTotal());
            pstmt.setString(3, vente.getStatut());
            pstmt.setInt(4, vente.getIdClient());
            pstmt.setInt(5, vente.getIdPharmacien());
            pstmt.setInt(6, vente.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new VenteNotFoundException(vente.getId());
            }
            
            System.out.println(" Vente modifiée avec succès (ID: " + vente.getId() + ")");
        }
    }
    
    public void supprimer(int id) throws SQLException, VenteNotFoundException {
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            conn.setAutoCommit(false);

            ligneVenteDAO.supprimerParVenteId(id);

            String sql = "DELETE FROM ventes WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new VenteNotFoundException(id);
                }
            }
            
            conn.commit();
            System.out.println(" Vente supprimée avec succès (ID: " + id + ")");
            
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    public List<Vente> trouverParClientId(int idClient) throws SQLException {
        String sql = "SELECT * FROM ventes WHERE idClient = ?";
        List<Vente> ventes = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vente vente = new Vente();
                    vente.setId(rs.getInt("id"));
                    vente.setDateVente(rs.getTimestamp("dateVente").toLocalDateTime());
                    vente.setMontantTotal(rs.getDouble("montantTotal"));
                    vente.setStatut(rs.getString("statut"));
                    vente.setIdClient(rs.getInt("idClient"));
                    vente.setIdPharmacien(rs.getInt("idPharmacien"));
                    
                    List<LigneVente> lignesVente = ligneVenteDAO.trouverParVenteId(vente.getId());
                    vente.setLignesVente(lignesVente);
                    
                    ventes.add(vente);
                }
            }
        }
        
        return ventes;
    }
}
