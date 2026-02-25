package controller;

import dao_db.ClientDAODB;
import model.Client;
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

public class ClientController implements Initializable {

    @FXML private TextField txtRecherche;
    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colTelephone;
    @FXML private TableColumn<Client, String> colAdresse;

    private ClientDAODB clientDAO = new ClientDAODB();
    private ObservableList<Client> listeClients = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        //Lier la liste a la table
        tableClients.setItems(listeClients);
        
        chargerClients();
    }

    @FXML
    private void handleRechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            showAlert("Attention", "Veuillez entrer un email");
            return;
        }
        try {
            Client client = clientDAO.trouverParEmail(recherche);
            if (client != null) {
                listeClients.clear(); 
                listeClients.add(client);
            } else {
                showAlert("Information", "Aucun client trouvé avec cet email");
                chargerClients();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAfficherTous() {
        txtRecherche.clear();
        chargerClients();
    }

    @FXML
    private void handleAjouter() {
        afficherFormulaire(null);
    }

    @FXML
    private void handleModifier() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afficherFormulaire(selected);
        } else {
            showAlert("Attention", "Veuillez sélectionner un client à modifier");
        }
    }

    @FXML
    private void handleSupprimer() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un client");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
            "Supprimer " + selected.getPrenom() + " " + selected.getNom() + " ?", 
            ButtonType.YES, ButtonType.NO);
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    clientDAO.supprimer(selected.getId());
                    showAlert("Succès", "Client supprimé avec succès");
                    chargerClients();
                } catch (Exception e) {
                    showAlert("Erreur", "Suppression impossible : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void chargerClients() { 
        try {
            listeClients.clear(); 
            listeClients.addAll(clientDAO.lireTous()); 
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les clients : " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void afficherFormulaire(Client clientExistant) { 
        boolean isEdition = (clientExistant != null);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdition ? "Modifier Client" : "Ajouter Client");
        
        ButtonType btnValider = new ButtonType(isEdition ? "Modifier" : "Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); 
        grid.setVgap(10); 
        grid.setPadding(new Insets(20));

        TextField txtPrenom = new TextField(isEdition ? clientExistant.getPrenom() : "");
        TextField txtNom = new TextField(isEdition ? clientExistant.getNom() : "");
        TextField txtEmail = new TextField(isEdition ? clientExistant.getEmail() : "");
        PasswordField txtPass = new PasswordField();
        TextField txtTel = new TextField(isEdition ? clientExistant.getTelephone() : "");
        TextArea txtAdr = new TextArea(isEdition ? clientExistant.getAdresse() : "");
        txtAdr.setPrefRowCount(3);

        grid.add(new Label("Prénom:"), 0, 0); 
        grid.add(txtPrenom, 1, 0);
        grid.add(new Label("Nom:"), 0, 1); 
        grid.add(txtNom, 1, 1);
        grid.add(new Label("Email:"), 0, 2); 
        grid.add(txtEmail, 1, 2);
        
        if (!isEdition) {
            grid.add(new Label("Mot de passe:"), 0, 3); 
            grid.add(txtPass, 1, 3);
        }
        
        grid.add(new Label("Téléphone:"), 0, 4); 
        grid.add(txtTel, 1, 4);
        grid.add(new Label("Adresse:"), 0, 5); 
        grid.add(txtAdr, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == btnValider) {
                try {
                    if (isEdition) {
                        clientExistant.setPrenom(txtPrenom.getText());
                        clientExistant.setNom(txtNom.getText());
                        clientExistant.setEmail(txtEmail.getText());
                        clientExistant.setTelephone(txtTel.getText());
                        clientExistant.setAdresse(txtAdr.getText());
                        clientDAO.modifier(clientExistant);
                        showAlert("Succès", "Client modifié avec succès");
                    } else {
                        Client c = new Client(0, txtEmail.getText(), txtPass.getText(), 
                                              txtPrenom.getText(), txtNom.getText(), 
                                              txtTel.getText(), txtAdr.getText());
                        clientDAO.ajouter(c);
                        showAlert("Succès", "Client ajouté avec succès");
                    }
                    chargerClients();
                } catch (Exception e) {
                    showAlert("Erreur", "Action échouée : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}