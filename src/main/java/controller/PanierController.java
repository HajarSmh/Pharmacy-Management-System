package controller;

import dao_db.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import model.*;
import util.Panier;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PanierController implements Initializable {

    @FXML private TableView<LigneVente> tablePanier;
    @FXML private TableColumn<LigneVente, String> colNom;
    @FXML private TableColumn<LigneVente, Integer> colQuantite;
    @FXML private TableColumn<LigneVente, Double> colPrixUnit;
    @FXML private TableColumn<LigneVente, Double> colSousTotal;
    @FXML private Label lblTotal;

    private MedicamentDAODB medicamentDAO = new MedicamentDAODB();
    private VenteDAODB venteDAO = new VenteDAODB();
    private ClientDAODB clientDAO = new ClientDAODB();
    private PharmacienDAODB pharmacienDAO = new PharmacienDAODB();

    private String emailClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        chargerPanier();
    }

    // ⭐ Méthode appelée depuis MainController
    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
        System.out.println("✓ Email client défini dans PanierController : " + emailClient);  // ← DEBUG
    }

    private void setupTable() {
        colNom.setCellValueFactory(cellData -> {
            try {
                Medicament med = medicamentDAO.trouverParId(cellData.getValue().getIdMedicament());
                return new SimpleStringProperty(med.getNom());
            } catch (Exception e) {
                return new SimpleStringProperty("Erreur");
            }
        });

        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteVendue"));
        colPrixUnit.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        
        colSousTotal.setCellValueFactory(cellData -> {
            double sousTotal = cellData.getValue().calculerTotalLigne();
            return new SimpleDoubleProperty(sousTotal).asObject();
        });
    }

    private void chargerPanier() {
        ObservableList<LigneVente> items = FXCollections.observableArrayList(Panier.getInstance().getLignes());
        tablePanier.setItems(items);
        double total = Panier.getInstance().calculerTotal();
        lblTotal.setText(String.format("Total : %.2f DH", total));
    }

    @FXML
    private void handleModifierQuantite() {
        LigneVente selected = tablePanier.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélectionner un article");
            return;
        }

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Modifier la quantité");
        dialog.setHeaderText("Modifier la quantité");

        ButtonType btnValiderType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValiderType, ButtonType.CANCEL);

        Spinner<Integer> spinner = new Spinner<>(1, 100, selected.getQuantiteVendue());
        spinner.setEditable(true);
        
        GridPane grid = new GridPane();
        grid.setHgap(10); 
        grid.setPadding(new Insets(20));
        grid.add(new Label("Nouvelle quantité :"), 0, 0);
        grid.add(spinner, 1, 0);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> btn == btnValiderType ? spinner.getValue() : null);

        dialog.showAndWait().ifPresent(nouvelleQte -> {
            try {
                Medicament med = medicamentDAO.trouverParId(selected.getIdMedicament());
                if (nouvelleQte > med.getQuantiteStock()) {
                    showAlert("Stock insuffisant ! Disponible : " + med.getQuantiteStock());
                } else {
                    selected.setQuantiteVendue(nouvelleQte);
                    chargerPanier();
                }
            } catch (Exception e) {
                showAlert("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleRetirer() {
        LigneVente selected = tablePanier.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Panier.getInstance().retirer(selected);
            chargerPanier();
        } else {
            showAlert("Veuillez sélectionner un article à retirer");
        }
    }

    @FXML
    private void handleViderPanier() {
        if (Panier.getInstance().estVide()) {
            showAlert("Le panier est déjà vide");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Voulez-vous vider le panier ?", 
            ButtonType.YES, ButtonType.NO);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Panier.getInstance().vider();
                chargerPanier();
                showAlert("Panier vidé");
            }
        });
    }

    @FXML
    private void handleValiderCommande() {
        if (Panier.getInstance().estVide()) {
            showAlert("Votre panier est vide !");
            return;
        }

        System.out.println("📧 Email client utilisé : " + emailClient);  // ← DEBUG

        // ⭐ Vérification de l'email
        if (emailClient == null || emailClient.isEmpty()) {
            showAlert("Erreur : Email client non défini. Veuillez vous reconnecter.");
            return;
        }

        try {
            // Recherche du client par email
            Client client = clientDAO.trouverParEmail(emailClient);
            
            if (client == null) {
                showAlert("Erreur : Client introuvable avec l'email : " + emailClient);
                System.err.println("❌ Client introuvable : " + emailClient);
                return;
            }
            
            System.out.println("✓ Client trouvé : " + client.getPrenom() + " " + client.getNom());
            
            // Recherche d'un pharmacien
            var pharmaciens = pharmacienDAO.lireTous();
            
            if (pharmaciens.isEmpty()) {
                showAlert("Erreur : Aucun pharmacien disponible dans le système");
                System.err.println("❌ Aucun pharmacien trouvé");
                return;
            }
            
            System.out.println("✓ Pharmacien trouvé : " + pharmaciens.get(0).getPrenom());

            // Création de la vente
            Vente vente = new Vente(
                0, 
                LocalDateTime.now(), 
                Panier.getInstance().calculerTotal(), 
                "EN_ATTENTE",  // ← Statut EN_ATTENTE (pas VALIDEE)
                client.getId(), 
                pharmaciens.get(0).getId()
            );
            
            vente.setLignesVente(new ArrayList<>(Panier.getInstance().getLignes()));
            
            // Sauvegarde dans la BDD
            venteDAO.ajouter(vente);
            
            // Vider le panier
            Panier.getInstance().vider();
            chargerPanier();

            // Confirmation
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Commande envoyée");
            successAlert.setHeaderText("Succès !");
            successAlert.setContentText(
                "Votre commande a été envoyée avec succès.\n\n" +
                "Numéro de commande : #" + vente.getId() + "\n" +
                "Statut : EN ATTENTE\n\n" +
                "Un pharmacien va valider votre commande."
            );
            successAlert.showAndWait();
            
            System.out.println("✓ Commande créée : #" + vente.getId());
            
        } catch (Exception e) {
            showAlert("Erreur lors de la validation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}