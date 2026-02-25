package controller;

import dao_db.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import model.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VenteController implements Initializable {

    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, Integer> colId;
    @FXML private TableColumn<Vente, LocalDateTime> colDate;
    @FXML private TableColumn<Vente, Double> colTotal;
    @FXML private TableColumn<Vente, String> colStatut;
    @FXML private TableColumn<Vente, Integer> colIdClient;
    @FXML private TableColumn<Vente, Integer> colIdPharmacien;
    
    // ⭐ AJOUT : Boutons à masquer pour les clients
    @FXML private Button btnNouvelle;
    @FXML private Button btnValider;
    @FXML private Button btnRefuser;
    @FXML private Button btnSupprimer;

    private VenteDAODB venteDAO = new VenteDAODB();
    private ClientDAODB clientDAO = new ClientDAODB();
    private PharmacienDAODB pharmacienDAO = new PharmacienDAODB();
    private MedicamentDAODB medicamentDAO = new MedicamentDAODB();
    private ObservableList<Vente> listeVentes = FXCollections.observableArrayList();
    private int userId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colIdClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        colIdPharmacien.setCellValueFactory(new PropertyValueFactory<>("idPharmacien"));
        
        // Configuration visuelle de la colonne Statut
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<Vente, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    switch (statut) {
                        case "EN_ATTENTE": 
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;"); 
                            break;
                        case "VALIDEE": 
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;"); 
                            break;
                        case "REFUSEE": 
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;"); 
                            break;
                        default: 
                            setStyle("");
                    }
                }
            }
        });

        tableVentes.setItems(listeVentes);
        chargerVentes();
    }
  
    @FXML
    private void handleActualiser() {
        chargerVentes();
    }
    
    // ⭐ MODIFIÉ : Masque les boutons pour les clients
    public void initDataClient(int userId) {
        this.userId = userId;
        
        // Masquer les boutons pour les clients
        if (btnNouvelle != null) btnNouvelle.setVisible(false);
        if (btnValider != null) btnValider.setVisible(false);
        if (btnRefuser != null) btnRefuser.setVisible(false);
        if (btnSupprimer != null) btnSupprimer.setVisible(false);
        
        // Optionnel : Masquer les colonnes ID
        if (colIdClient != null) colIdClient.setVisible(false);
        if (colIdPharmacien != null) colIdPharmacien.setVisible(false);
        
        chargerVentesClient(userId);
    }

    private void chargerVentesClient(int userId) {
        try {
            List<Vente> toutesVentes = venteDAO.lireTous();
            List<Vente> ventesClient = toutesVentes.stream()
                .filter(v -> v.getIdClient() == userId)
                .collect(Collectors.toList());
            
            listeVentes.clear();
            listeVentes.addAll(ventesClient);
            System.out.println("✓ " + listeVentes.size() + " commandes chargées");
        } catch (Exception e) {
            showAlert("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void chargerVentes() {
        try {
            listeVentes.clear();
            listeVentes.addAll(venteDAO.lireTous());
            System.out.println("✓ " + listeVentes.size() + " ventes chargées");
        } catch (Exception e) {
            showAlert("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNouvelleVente() {
        afficherFormulaireNouvelleVente();
    }

    @FXML
    private void handleDetails() {
        Vente selected = tableVentes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afficherDetailsVente(selected);
        } else {
            showAlert("Veuillez sélectionner une vente");
        }
    }

    @FXML
    private void handleValider() {
        Vente selected = tableVentes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            validerVente(selected);
        } else {
            showAlert("Veuillez sélectionner une vente à valider");
        }
    }

    @FXML
    private void handleRefuser() {
        Vente selected = tableVentes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            refuserVente(selected);
        } else {
            showAlert("Veuillez sélectionner une vente à refuser");
        }
    }

    @FXML
    private void handleSupprimer() {
        Vente selected = tableVentes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            supprimerVente(selected);
        } else {
            showAlert("Veuillez sélectionner une vente à supprimer");
        }
    }

    private void afficherFormulaireNouvelleVente() {
        Dialog<Vente> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Vente");
        dialog.setHeaderText("Créer une nouvelle vente");

        ButtonType btnCreerType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCreerType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); 
        grid.setVgap(10); 
        grid.setPadding(new Insets(20));

        // Sélection Client
        ComboBox<Client> comboClients = new ComboBox<>();
        try {
            List<Client> clients = clientDAO.lireTous();
            comboClients.setItems(FXCollections.observableArrayList(clients));
            comboClients.setPromptText("Sélectionner un client");
            
            comboClients.setCellFactory(param -> new ListCell<Client>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getPrenom() + " " + client.getNom() + " (" + client.getEmail() + ")");
                    }
                }
            });
            comboClients.setButtonCell(new ListCell<Client>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getPrenom() + " " + client.getNom());
                    }
                }
            });
        } catch (Exception e) { 
            showAlert("Erreur clients : " + e.getMessage()); 
            e.printStackTrace();
            return; 
        }

        // Sélection Pharmacien
        ComboBox<Pharmacien> comboPharmaciens = new ComboBox<>();
        try {
            List<Pharmacien> pharmaciens = pharmacienDAO.lireTous();
            comboPharmaciens.setItems(FXCollections.observableArrayList(pharmaciens));
            comboPharmaciens.setPromptText("Sélectionner un pharmacien");
            
            comboPharmaciens.setCellFactory(param -> new ListCell<Pharmacien>() {
                @Override
                protected void updateItem(Pharmacien pharmacien, boolean empty) {
                    super.updateItem(pharmacien, empty);
                    if (empty || pharmacien == null) {
                        setText(null);
                    } else {
                        setText(pharmacien.getPrenom() + " " + pharmacien.getNom());
                    }
                }
            });
            comboPharmaciens.setButtonCell(new ListCell<Pharmacien>() {
                @Override
                protected void updateItem(Pharmacien pharmacien, boolean empty) {
                    super.updateItem(pharmacien, empty);
                    if (empty || pharmacien == null) {
                        setText(null);
                    } else {
                        setText(pharmacien.getPrenom() + " " + pharmacien.getNom());
                    }
                }
            });
        } catch (Exception e) { 
            showAlert("Erreur pharmaciens : " + e.getMessage()); 
            e.printStackTrace();
            return; 
        }

        // Sélection Médicaments
        ComboBox<Medicament> comboMedicaments = new ComboBox<>();
        try {
            List<Medicament> medicaments = medicamentDAO.lireTous();
            comboMedicaments.setItems(FXCollections.observableArrayList(medicaments));
            comboMedicaments.setPromptText("Sélectionner un médicament");
            
            comboMedicaments.setCellFactory(param -> new ListCell<Medicament>() {
                @Override
                protected void updateItem(Medicament medicament, boolean empty) {
                    super.updateItem(medicament, empty);
                    if (empty || medicament == null) {
                        setText(null);
                    } else {
                        setText(medicament.getNom() + " - " + medicament.getPrix() + " DH (Stock: " + medicament.getQuantiteStock() + ")");
                    }
                }
            });
            comboMedicaments.setButtonCell(new ListCell<Medicament>() {
                @Override
                protected void updateItem(Medicament medicament, boolean empty) {
                    super.updateItem(medicament, empty);
                    if (empty || medicament == null) {
                        setText(null);
                    } else {
                        setText(medicament.getNom());
                    }
                }
            });
        } catch (Exception e) { 
            showAlert("Erreur médicaments : " + e.getMessage()); 
            e.printStackTrace();
            return; 
        }

        Spinner<Integer> spinnerQuantite = new Spinner<>(1, 100, 1);
        spinnerQuantite.setEditable(true);
        spinnerQuantite.setPrefWidth(80);
        
        TableView<LigneVente> tableLignes = new TableView<>();
        ObservableList<LigneVente> lignesVente = FXCollections.observableArrayList();
        tableLignes.setItems(lignesVente);

        // Configuration colonnes TableLignes
        TableColumn<LigneVente, String> colMed = new TableColumn<>("Médicament");
        colMed.setCellValueFactory(cd -> {
            try { 
                return new SimpleStringProperty(medicamentDAO.trouverParId(cd.getValue().getIdMedicament()).getNom()); 
            } catch(Exception e) { 
                return new SimpleStringProperty("?"); 
            }
        });
        colMed.setPrefWidth(200);
        
        TableColumn<LigneVente, Integer> colQ = new TableColumn<>("Qte");
        colQ.setCellValueFactory(new PropertyValueFactory<>("quantiteVendue"));
        colQ.setPrefWidth(80);
        
        TableColumn<LigneVente, Double> colPrix = new TableColumn<>("Prix Unit.");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colPrix.setPrefWidth(100);
        
        TableColumn<LigneVente, Double> colTotalLigne = new TableColumn<>("Total");
        colTotalLigne.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().calculerTotalLigne()).asObject());
        colTotalLigne.setPrefWidth(100);
        
        tableLignes.getColumns().addAll(colMed, colQ, colPrix, colTotalLigne);
        tableLignes.setPrefHeight(200);

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button btnRetirer = new Button("Retirer sélectionné");
        btnRetirer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        Label lblTotalVente = new Label("Total : 0.00 DH");
        lblTotalVente.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        btnAjouter.setOnAction(e -> {
            Medicament m = comboMedicaments.getValue();
            if (m == null) {
                showAlert("Veuillez sélectionner un médicament");
                return;
            }
            
            int quantite = spinnerQuantite.getValue();
            if (quantite > m.getQuantiteStock()) {
                showAlert("Stock insuffisant ! Stock disponible : " + m.getQuantiteStock());
                return;
            }
            
            lignesVente.add(new LigneVente(0, quantite, m.getPrix(), 0, m.getId()));
            double total = lignesVente.stream().mapToDouble(LigneVente::calculerTotalLigne).sum();
            lblTotalVente.setText(String.format("Total : %.2f DH", total));
            
            comboMedicaments.setValue(null);
            spinnerQuantite.getValueFactory().setValue(1);
        });
        
        btnRetirer.setOnAction(e -> {
            LigneVente selected = tableLignes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                lignesVente.remove(selected);
                double total = lignesVente.stream().mapToDouble(LigneVente::calculerTotalLigne).sum();
                lblTotalVente.setText(String.format("Total : %.2f DH", total));
            }
        });

        grid.add(new Label("Client:"), 0, 0); 
        grid.add(comboClients, 1, 0);
        grid.add(new Label("Pharmacien:"), 0, 1); 
        grid.add(comboPharmaciens, 1, 1);
        grid.add(new Label("Médicament:"), 0, 2);
        HBox hboxMed = new HBox(5, comboMedicaments, new Label("Qté:"), spinnerQuantite, btnAjouter);
        grid.add(hboxMed, 1, 2);
        grid.add(tableLignes, 0, 3, 2, 1);
        grid.add(btnRetirer, 0, 4);
        grid.add(lblTotalVente, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefSize(700, 600);

        dialog.setResultConverter(btn -> {
            if (btn == btnCreerType) {
                try {
                    if (comboClients.getValue() == null || comboPharmaciens.getValue() == null) {
                        showAlert("Veuillez sélectionner un client et un pharmacien");
                        return null;
                    }
                    
                    if (lignesVente.isEmpty()) {
                        showAlert("Veuillez ajouter au moins un médicament");
                        return null;
                    }
                    
                    double total = lignesVente.stream().mapToDouble(LigneVente::calculerTotalLigne).sum();
                    Vente v = new Vente(0, LocalDateTime.now(), total, "VALIDEE", 
                                       comboClients.getValue().getId(), 
                                       comboPharmaciens.getValue().getId());
                    v.setLignesVente(new ArrayList<>(lignesVente));
                    venteDAO.ajouter(v);
                    showAlert("Vente créée avec succès !"); 
                    chargerVentes();
                    return v;
                } catch (Exception ex) { 
                    showAlert("Erreur lors de la création : " + ex.getMessage()); 
                    ex.printStackTrace();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void validerVente(Vente vente) {
        if (!vente.getStatut().equals("EN_ATTENTE")) {
            showAlert("Seules les ventes EN_ATTENTE peuvent être validées.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
            "Confirmer la validation de la vente #" + vente.getId() + " ?", 
            ButtonType.YES, ButtonType.NO);
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    vente.setStatut("VALIDEE");
                    venteDAO.modifier(vente);
                    showAlert("Vente validée avec succès");
                    chargerVentes(); 
                } catch (Exception e) { 
                    showAlert("Erreur : " + e.getMessage()); 
                    e.printStackTrace();
                }
            }
        });
    }

    private void refuserVente(Vente vente) {
        if (!vente.getStatut().equals("EN_ATTENTE")) {
            showAlert("Action impossible sur ce statut.");
            return;
        }

        TextInputDialog raisonDialog = new TextInputDialog("Prescription manquante");
        raisonDialog.setTitle("Refus");
        raisonDialog.setHeaderText("Raison du refus pour la vente #" + vente.getId());
        
        raisonDialog.showAndWait().ifPresent(raison -> {
            try {
                vente.setStatut("REFUSEE");
                venteDAO.modifier(vente);
                showAlert("Vente refusée pour raison : " + raison);
                chargerVentes();  
            } catch (Exception e) { 
                showAlert("Erreur : " + e.getMessage()); 
                e.printStackTrace();
            }
        });
    }

    private void afficherDetailsVente(Vente vente) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la vente");
        alert.setHeaderText("Vente #" + vente.getId());

        try {
            Client c = clientDAO.trouverParId(vente.getIdClient());
            Pharmacien p = pharmacienDAO.trouverParId(vente.getIdPharmacien());
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Date : %s\n", vente.getDateVente()));
            sb.append(String.format("Client : %s %s\n", c.getPrenom(), c.getNom()));
            sb.append(String.format("Pharmacien : %s %s\n", p.getPrenom(), p.getNom()));
            sb.append(String.format("Statut : %s\n", vente.getStatut()));
            sb.append(String.format("Montant Total : %.2f DH\n\n", vente.getMontantTotal()));
            sb.append("Médicaments vendus :\n");
            
            for (LigneVente ligne : vente.getLignesVente()) {
                Medicament m = medicamentDAO.trouverParId(ligne.getIdMedicament());
                sb.append(String.format("- %s (x%d) : %.2f DH\n", 
                    m.getNom(), ligne.getQuantiteVendue(), ligne.calculerTotalLigne()));
            }
            
            alert.setContentText(sb.toString());
        } catch (Exception e) { 
            alert.setContentText("Erreur lors du chargement des détails : " + e.getMessage()); 
            e.printStackTrace();
        }
        alert.showAndWait();
    }

    private void supprimerVente(Vente vente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Supprimer définitivement la vente #" + vente.getId() + " ?", 
            ButtonType.YES, ButtonType.NO);
        
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                try {
                    venteDAO.supprimer(vente.getId());
                    showAlert("Vente supprimée avec succès");
                    chargerVentes();  
                } catch (Exception e) { 
                    showAlert("Erreur suppression : " + e.getMessage()); 
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}