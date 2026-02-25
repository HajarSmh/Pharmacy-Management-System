package controller;

import dao_db.PharmacienDAODB;
import model.Pharmacien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PharmacienController implements Initializable {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Pharmacien> tablePharmaciens;
    @FXML private TableColumn<Pharmacien, Integer> colId;
    @FXML private TableColumn<Pharmacien, String> colPrenom;
    @FXML private TableColumn<Pharmacien, String> colNom;
    @FXML private TableColumn<Pharmacien, String> colEmail;

    private PharmacienDAODB pharmacienDAO = new PharmacienDAODB();
    private ObservableList<Pharmacien> listePharmaciens = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Liaison des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        chargerPharmaciens();
    }

    @FXML
    private void handleRechercher() {
        String email = txtRecherche.getText().trim();
        if (email.isEmpty()) {
            showAlert("Attention", "Veuillez entrer un email pour la recherche.");
            return;
        }
        try {
            Pharmacien p = pharmacienDAO.trouverParEmail(email);
            if (p != null) {
                tablePharmaciens.setItems(FXCollections.observableArrayList(p));
            } else {
                showAlert("Information", "Aucun pharmacien trouvé avec cet email.");
                chargerPharmaciens();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche : " + e.getMessage());
        }
    }

    @FXML
    private void handleAfficherTous() {
        txtRecherche.clear();
        chargerPharmaciens();
    }

    @FXML
    private void handleAjouter() {
        afficherFormulaire(null);
    }

    @FXML
    private void handleModifier() {
        Pharmacien selected = tablePharmaciens.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afficherFormulaire(selected);
        } else {
            showAlert("Attention", "Veuillez sélectionner un pharmacien à modifier.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Pharmacien selected = tablePharmaciens.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + selected.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                try {
                    pharmacienDAO.supprimer(selected.getId());
                    chargerPharmaciens();
                } catch (Exception e) {
                    showAlert("Erreur", "Suppression impossible.");
                }
            }
        });
    }

    private void chargerPharmaciens() {
        try {
        	listePharmaciens.clear();
            listePharmaciens = FXCollections.observableArrayList(pharmacienDAO.lireTous());
            tablePharmaciens.setItems(listePharmaciens);
            tablePharmaciens.refresh();
        } catch (Exception e) {
            showAlert("Erreur", "Chargement échoué.");
            e.printStackTrace();
        }
    }

    private void afficherFormulaire(Pharmacien pharmacienExistant) {
        boolean isEdit = (pharmacienExistant != null);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Modifier Pharmacien" : "Ajouter Pharmacien");

        ButtonType btnOk = new ButtonType(isEdit ? "Modifier" : "Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField prenom = new TextField(isEdit ? pharmacienExistant.getPrenom() : "");
        TextField nom = new TextField(isEdit ? pharmacienExistant.getNom() : "");
        TextField email = new TextField(isEdit ? pharmacienExistant.getEmail() : "");
        PasswordField pass = new PasswordField();

        grid.add(new Label("Prénom:"), 0, 0); grid.add(prenom, 1, 0);
        grid.add(new Label("Nom:"), 0, 1); grid.add(nom, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(email, 1, 2);
        if (!isEdit) {
            grid.add(new Label("Mot de passe:"), 0, 3); grid.add(pass, 1, 3);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == btnOk) {
                try {
                    if (isEdit) {
                        pharmacienExistant.setPrenom(prenom.getText());
                        pharmacienExistant.setNom(nom.getText());
                        pharmacienExistant.setEmail(email.getText());
                        pharmacienDAO.modifier(pharmacienExistant);
                    } else {
                        Pharmacien p = new Pharmacien(0, email.getText(), pass.getText(), prenom.getText(), nom.getText());
                        pharmacienDAO.ajouter(p);
                    }
                    chargerPharmaciens();
                } catch (Exception e) {
                    showAlert("Erreur", "Opération échouée.");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}