package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController {
    
    @FXML
    private BorderPane mainPane;
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private VBox menuClient;
    
    @FXML
    private VBox menuPharmacien;
    
    private String role;
    private String email;
    private int userId;
    
    public void initData(String role, String email, int userId) {
        this.role = role;
        this.email = email;
        this.userId = userId;
        
        lblWelcome.setText("Bienvenue, " + email);
        
        if (role.equals("CLIENT")) {
            menuClient.setVisible(true);
            menuClient.setManaged(true);
            menuPharmacien.setVisible(false);
            menuPharmacien.setManaged(false);
        } else if (role.equals("PHARMACIEN")) {
            menuPharmacien.setVisible(true);
            menuPharmacien.setManaged(true);
            menuClient.setVisible(false);
            menuClient.setManaged(false);
        }
    }

    
    @FXML
    private void handleCatalogue() {
        chargerVue("/fxml/catalogue.fxml");
    }
    
    @FXML
    private void handlePanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/panier.fxml"));
            Parent root = loader.load();
            
           
            PanierController controller = loader.getController();
            controller.setEmailClient(this.email);  
            
            mainPane.setCenter(root);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleMesCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vente.fxml"));
            Parent root = loader.load();
            
            VenteController controller = loader.getController();
            controller.initDataClient(userId);
            
            mainPane.setCenter(root);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //MENU PHARMACIEN
    
    @FXML
    private void handleMedicaments() {
        chargerVue("/fxml/medicament.fxml");
    }
    
    @FXML
    private void handleClients() {
        chargerVue("/fxml/client.fxml");
    }
    
    @FXML
    private void handlePharmaciens() {
        chargerVue("/fxml/pharmacien.fxml");
    }
    
    @FXML
    private void handleVentes() {
        chargerVue("/fxml/vente.fxml");
    }
        
    @FXML
    private void handleDeconnexion() {
        try {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Déconnexion");
            confirmation.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                        Parent root = loader.load();
                        
                        mainPane.getScene().setRoot(root);
                        
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void chargerVue(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainPane.setCenter(root);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getRole() {
        return role;
    }
}