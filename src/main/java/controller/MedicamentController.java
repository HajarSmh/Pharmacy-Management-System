package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import dao_db.MedicamentDAODB;
import model.Medicament;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MedicamentController {
    
    @FXML private TableView<Medicament> tableMedicaments;
    @FXML private TableColumn<Medicament, Integer> colId;
    @FXML private TableColumn<Medicament, String> colNom;
    @FXML private TableColumn<Medicament, Double> colPrix;
    @FXML private TableColumn<Medicament, Integer> colStock;
    @FXML private TableColumn<Medicament, LocalDate> colExpiration;
    @FXML private TableColumn<Medicament, Boolean> colPrescription;
    @FXML private TableColumn<Medicament, String> colFournisseur;
    
    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private TextField txtStock;
    @FXML private DatePicker dateExpiration;
    @FXML private CheckBox checkPrescription;
    @FXML private TextField txtFournisseur;
    @FXML private CheckBox checkAfficherInactifs; 
    
    private MedicamentDAODB medicamentDAO = new MedicamentDAODB();
    private ObservableList<Medicament> listeMedicaments;
    
    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colPrescription.setCellValueFactory(new PropertyValueFactory<>("prescriptionRequise"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));
        
        chargerMedicaments();
    }
    
    @FXML
    private void handleAjouter() {
        if (!validerChamps()) return;
        
        try {
            Medicament medicament = new Medicament(
                0,
                txtNom.getText(),
                Double.parseDouble(txtPrix.getText()),
                Integer.parseInt(txtStock.getText()),
                dateExpiration.getValue(),
                checkPrescription.isSelected(),
                txtFournisseur.getText()
            );
            // Par défaut, le médicament est actif (défini dans le constructeur)
            
            medicamentDAO.ajouter(medicament);
            showAlert(Alert.AlertType.INFORMATION, "Médicament ajouté avec succès !");
            clearFields();
            chargerMedicaments();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleModifier() {
        Medicament selected = tableMedicaments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un médicament");
            return;
        }
        
        if (!validerChamps()) return;
        
        try {
            selected.setNom(txtNom.getText());
            selected.setPrix(Double.parseDouble(txtPrix.getText()));
            selected.setQuantiteStock(Integer.parseInt(txtStock.getText()));
            selected.setDateExpiration(dateExpiration.getValue());
            selected.setPrescriptionRequise(checkPrescription.isSelected());
            selected.setNomFournisseur(txtFournisseur.getText());
            // On ne modifie PAS le statut actif ici
            
            medicamentDAO.modifier(selected);
            showAlert(Alert.AlertType.INFORMATION, "Médicament modifié avec succès !");
            clearFields();
            chargerMedicaments();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSupprimer() { //Désactivation au lieu de suppression
        Medicament selected = tableMedicaments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un médicament");
            return;
        }
        
        // Vérifier si déjà inactif
        if (!selected.isActif()) {
            showAlert(Alert.AlertType.INFORMATION, "Ce médicament est déjà désactivé");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Désactiver le médicament");
        confirmation.setHeaderText("Voulez-vous désactiver ce médicament ?");
        confirmation.setContentText(
            "Médicament : " + selected.getNom() + "\n\n" +
            "Le médicament sera retiré du catalogue actif mais restera dans l'historique des ventes.\n" +
            "Vous pourrez le réactiver plus tard si nécessaire."
        );
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    selected.setActif(false);
                    medicamentDAO.modifier(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Médicament désactivé avec succès !");
                    clearFields();
                    chargerMedicaments();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    @FXML
    private void handleReactiver() {  
        Medicament selected = tableMedicaments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un médicament");
            return;
        }
        
        if (selected.isActif()) {
            showAlert(Alert.AlertType.INFORMATION, "Ce médicament est déjà actif");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Réactiver le médicament");
        confirmation.setHeaderText("Voulez-vous réactiver ce médicament ?");
        confirmation.setContentText(
            "Médicament : " + selected.getNom() + "\n\n" +
            "Le médicament sera à nouveau disponible dans le catalogue."
        );
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    selected.setActif(true);
                    medicamentDAO.modifier(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Médicament réactivé avec succès !");
                    clearFields();
                    chargerMedicaments();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    @FXML
    private void handleActualiser() {
        chargerMedicaments();
    }
    
    @FXML
    private void handleAfficherInactifs() { 
        chargerMedicaments();
    }
    
    @FXML
    private void handleSelectionner() {
        Medicament selected = tableMedicaments.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtNom.setText(selected.getNom());
            txtPrix.setText(String.valueOf(selected.getPrix()));
            txtStock.setText(String.valueOf(selected.getQuantiteStock()));
            dateExpiration.setValue(selected.getDateExpiration());
            checkPrescription.setSelected(selected.isPrescriptionRequise());
            txtFournisseur.setText(selected.getNomFournisseur());
        }
    }
    
    private void chargerMedicaments() { 
        try {
            List<Medicament> tous = medicamentDAO.lireTous();
            
            //ici c'est pour afficher tous les med en cochant la case
            if (checkAfficherInactifs != null && checkAfficherInactifs.isSelected()) {
                listeMedicaments = FXCollections.observableArrayList(tous);
            } else {
                // Sinon, seulement les actifs
                List<Medicament> actifs = tous.stream()
                    .filter(Medicament::isActif)
                    .collect(Collectors.toList());
                listeMedicaments = FXCollections.observableArrayList(actifs);
            }
            
            tableMedicaments.setItems(listeMedicaments);
            tableMedicaments.refresh();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validerChamps() {
        if (txtNom.getText().isEmpty() || txtPrix.getText().isEmpty() ||
            txtStock.getText().isEmpty() || dateExpiration.getValue() == null ||
            txtFournisseur.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs");
            return false;
        }
        return true;
    }
    
    private void clearFields() {
        txtNom.clear();
        txtPrix.clear();
        txtStock.clear();
        dateExpiration.setValue(null);
        checkPrescription.setSelected(false);
        txtFournisseur.clear();
        tableMedicaments.getSelectionModel().clearSelection();
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}