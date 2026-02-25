package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao_db.ClientDAODB;
import dao_db.PharmacienDAODB;
import model.Client;
import model.Pharmacien;

public class LoginController {
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private ComboBox<String> comboRole;
    
    private ClientDAODB clientDAO = new ClientDAODB();
    private PharmacienDAODB pharmacienDAO = new PharmacienDAODB();
    
    @FXML
    public void initialize() {
        comboRole.setValue("CLIENT");
    }
    
    @FXML
    private void handleLogin() {
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String role = comboRole.getValue();
        
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs");
            return;
        }
        
        try {
            boolean authenticated = false;
            
            if (role.equals("CLIENT")) {
                for (Client client : clientDAO.lireTous()) {
                    if (client.getEmail().equals(email) && 
                        client.getPassword().equals(password)) {
                        authenticated = true;
                        ouvrirMainView(role, email, client.getId());
                        break;
                    }
                }
            } else if (role.equals("PHARMACIEN")) {
                for (Pharmacien pharma : pharmacienDAO.lireTous()) {
                    if (pharma.getEmail().equals(email) && 
                        pharma.getPassword().equals(password)) {
                        authenticated = true;
                        ouvrirMainView(role, email, pharma.getId());
                        break;
                    }
                }
            }
            
            if (!authenticated) {
                showAlert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect");
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void ouvrirMainView(String role, String email, int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            MainController mainController = loader.getController();
            mainController.initData(role, email, userId);
            
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Pharmacy - " + role);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}