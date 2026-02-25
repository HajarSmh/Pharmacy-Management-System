package controller;

import dao_db.MedicamentDAODB;
import model.Medicament;
import model.LigneVente;
import util.Panier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Medicament> tableMedicaments;
    @FXML private TableColumn<Medicament, Integer> colId;
    @FXML private TableColumn<Medicament, String> colNom;
    @FXML private TableColumn<Medicament, Double> colPrix;
    @FXML private TableColumn<Medicament, Integer> colStock;
    @FXML private TableColumn<Medicament, LocalDate> colExpiration;
    @FXML private TableColumn<Medicament, Boolean> colPrescription;
    @FXML private TableColumn<Medicament, String> colFournisseur;
    @FXML private Spinner<Integer> spinnerQuantite;
    @FXML private Label lblInfoPanier;

    private MedicamentDAODB medicamentDAO = new MedicamentDAODB();
    private ObservableList<Medicament> listeMedicaments = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colPrescription.setCellValueFactory(new PropertyValueFactory<>("prescriptionRequise"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));

        // 2. Configuration du Spinner (de 1 à 1000)
        spinnerQuantite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
       
        chargerMedicaments();
    }

    @FXML
    private void handleRechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            showAlert("Attention", "Veuillez entrer un nom pour la recherche.");
            return;
        }

        try {
            listeMedicaments = FXCollections.observableArrayList(medicamentDAO.rechercherParNom(recherche));
            tableMedicaments.setItems(listeMedicaments);
            
            if (listeMedicaments.isEmpty()) {
                showAlert("Résultat", "Aucun médicament trouvé pour : " + recherche);
                chargerMedicaments();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur de recherche : " + e.getMessage());
        }
    }

    @FXML
    private void handleAfficherTous() {
        txtRecherche.clear();
        chargerMedicaments();
    }

    @FXML
    private void handleAjouterPanier() {
        Medicament selected = tableMedicaments.getSelectionModel().getSelectedItem();
        int quantite = spinnerQuantite.getValue();

        if (selected == null) {
            showAlert("Sélection", "Veuillez d'abord sélectionner un médicament dans le tableau.");
            return;
        }

        if (quantite > selected.getQuantiteStock()) {
            showAlert("Stock Insuffisant", "Désolé, il n'en reste que " + selected.getQuantiteStock() + " en stock.");
            return;
        }

       
        LigneVente ligne = new LigneVente(0, quantite, selected.getPrix(), 0, selected.getId());
        Panier.getInstance().ajouter(ligne);

       
        lblInfoPanier.setText("Ajouté au panier !");
        spinnerQuantite.getValueFactory().setValue(1);

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                Platform.runLater(() -> lblInfoPanier.setText(""));
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void chargerMedicaments() {
        try {
            listeMedicaments = FXCollections.observableArrayList(medicamentDAO.lireTous());
            tableMedicaments.setItems(listeMedicaments);
        } catch (Exception e) {
            showAlert("Erreur Database", "Impossible de charger les médicaments : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}