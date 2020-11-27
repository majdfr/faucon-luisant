package DevisOracle.controllers;

import DevisOracle.DevisArrow.ExcelQuoteRow;
import DevisOracle.DevisArrow.appliquerReglesMetier;
import DevisOracle.Gui.EditingCellQuoteInteger;
import DevisOracle.Gui.EditingCellQuoteString;
import DevisOracle.Gui.guiMethods;
import DevisOracle.catalogue.CatalogRef;
import DevisOracle.catalogue.readCatalog;
import DevisOracle.shared.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DevisManuelTabController {

    static final Logger log = LogManager.getLogger(DevisManuelTabController.class.getName());
    // manual devis
    @FXML   private TextField txtDestinationExportDossier, txtDestinationExportFichier;
    // manual devis declarations
    @FXML   private TableColumn<ExcelQuoteRow, String> colCCManualRefUGAP, colCCManualRef, colCCManualDesignation;
    @FXML   private TableColumn<ExcelQuoteRow, Double> colCCManualPrixPublic, colCCManualPrixUnitaire, colCCManualPrixVenteHT;
    @FXML   private TableColumn<ExcelQuoteRow, Integer> colCCManualQuantite;
    @FXML   private TableView<ExcelQuoteRow> tableManualQuote;
    @FXML   private TableView<ExcelQuoteRow> tableQuote;
    @FXML   private ChoiceBox choiceExportManuelType, choiceDureeSupportManuel;
    @FXML   private CheckBox checkBoxDevisManuelUtiliserPromos;
      /*
    ################################################################
    ***************************************************************
    #################### Devis Manuel #########################
     */

    public void initialize() {
        List<ExcelQuoteRow> devisManuel = new ArrayList<>();
        initializeDevisManuel(devisManuel);
        txtDestinationExportDossier.setText(Tools.readProperty("DevisManuel.destination"));
    }


    public void initializeDevisManuel(List<ExcelQuoteRow> devisManuel) {
        try {
            // Affiche les données source du Devis Arrow
            // convertit en observableList pour l'objet TableView
            ObservableList<ExcelQuoteRow> observableList = FXCollections.observableArrayList(devisManuel);

            colCCManualRef.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("ref"));
            colCCManualRefUGAP.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("refUGAP"));
            colCCManualDesignation.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("designation"));
            colCCManualQuantite.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Integer>("quantite"));
            colCCManualPrixPublic.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixPublic"));
            colCCManualPrixUnitaire.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixUnitaire"));
            colCCManualPrixVenteHT.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixVenteHT"));

            tableManualQuote.setEditable(true);

            Callback<TableColumn<ExcelQuoteRow, String>, TableCell<ExcelQuoteRow, String>> cellFactory
                    = (TableColumn<ExcelQuoteRow, String> param) -> new EditingCellQuoteString();
            //--- Add for Editable Cell of Value field, in String
            colCCManualRef.setCellFactory(cellFactory);
            colCCManualRef.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<ExcelQuoteRow, String>>() {
                        @Override public void handle(TableColumn.CellEditEvent<ExcelQuoteRow, String> t) {
                            // ((ExcelQuoteRow)t.getTableView().getItems().get(
                            //       t.getTablePosition().getRow())).setRef(t.getNewValue()) ;
                            ExcelQuoteRow myRow = (ExcelQuoteRow)t.getTableView().getItems().get(
                                    t.getTablePosition().getRow());
                            System.out.println("myRow : " + myRow.toString());
                            myRow.setRef(t.getNewValue());
                            CatalogRef catalogRef;
                            try {
                                Boolean isKeyAvailable = readCatalog.getInstance().getCatalogParRefConstructeur().containsKey(t.getNewValue());
                                if (isKeyAvailable) {
                                    catalogRef = readCatalog.getInstance().getCatalogParRefConstructeur().get(t.getNewValue());
                                    System.out.println("catalogRef :" + catalogRef.toString());
                                    myRow.setCatalogRef(catalogRef);
                                    System.out.println("getRefUGAP :" + catalogRef.getRefUGAP());
                                    myRow.setRefUGAP(catalogRef.getRefUGAP());
                                    myRow.setDesignation(catalogRef.getDesignation());
                                    myRow.setPrixPublic(catalogRef.getPrixPublicReponse());
                                    myRow.setPrixUnitaire(catalogRef.getPrixNetUGAP());
                                    myRow.setQuantite(1);
                                    myRow.setPrixAchatHT(catalogRef.getPrixAchatHT());
                                    myRow.setPrixVenteHT(catalogRef.getPrixNetUGAP());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // clé par défaut.
                        }
                    });
            //---


            Callback<TableColumn<ExcelQuoteRow, Integer>, TableCell<ExcelQuoteRow, Integer>> cellManualQuantiteFactory
                    = (TableColumn<ExcelQuoteRow, Integer> param) -> new EditingCellQuoteInteger();
            //--- Add for Editable Cell of Value field, in String
            colCCManualQuantite.setCellFactory(cellManualQuantiteFactory);
            colCCManualQuantite.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<ExcelQuoteRow, Integer>>() {
                        @Override public void handle(TableColumn.CellEditEvent<ExcelQuoteRow, Integer> t) {
                            // ((ExcelQuoteRow)t.getTableView().getItems().get(
                            //       t.getTablePosition().getRow())).setRef(t.getNewValue()) ;
                            ExcelQuoteRow myRow = (ExcelQuoteRow)t.getTableView().getItems().get(
                                    t.getTablePosition().getRow());
                            myRow.setQuantite(t.getNewValue());
                            myRow.setPrixVenteHT(myRow.getPrixUnitaire() * t.getNewValue());
                            // clé par défaut.
                        }
                    });

            tableManualQuote.setItems(null);
            tableManualQuote.setItems(observableList);


        } catch (Exception e) {
            e.printStackTrace();
            log.debug("GuiController - InitializeDevisManuel error", e);
        }
    }


    public void onAddManualQuoteLine(ActionEvent actionEvent) {
        ExcelQuoteRow row = new ExcelQuoteRow();
        row.setRef("ref");
        tableManualQuote.getItems().add(row);
    }

    public void exporterManuel(ActionEvent actionEvent) {

        String myFile = txtDestinationExportDossier.getText().concat(txtDestinationExportFichier.getText());
        Double montantPrixAchat = (double)0;
        try {
            montantPrixAchat = guiMethods.getMontantPrixAchat(tableQuote.getItems());
        } catch (NullPointerException npe) {
            System.out.println("Pas de prix d'achat lors de l'export manuel du devis ... on le met à 0");
        }

        switch(choiceExportManuelType.getValue().toString()) {
            case "Devis Client":
                guiMethods.exportDevisClient(tableManualQuote.getItems(), myFile, "Devis Manuel",false, montantPrixAchat);
                break;
            case "Devis Interne":
                // code block
                guiMethods.exportDevisClient(tableManualQuote.getItems(), myFile, "Devis Manuel",true,montantPrixAchat);
                break;
            case "Devis pour analyse":
                //exportAnalyseDevis.export(txtFileToConvert.getText());
                break;
            default:
                // code block
        }

    }

    public void onRemoveManualQuoteLine(ActionEvent actionEvent) {
        // ObservableList<ExcelQuoteRow> observableList = tableManualQuote.getItems();
//        Integer selectedIdx =  tableManualQuote.getSelectionModel().getSelectedIndex(); //  .getSelectedItem();
//        tableManualQuote.getItems().remove(selectedIdx);

        ExcelQuoteRow eqr =  tableManualQuote.getSelectionModel().getSelectedItem();  //  .getSelectedItem();
        tableManualQuote.getItems().remove(eqr);


    }


    public void onCleanManualQuoteTable(ActionEvent actionEvent) {
        tableManualQuote.getItems().clear();
    }


    public void onAddSupport(ActionEvent actionEvent) {
        ExcelQuoteRow eqr = tableManualQuote.getSelectionModel().getSelectedItem();

//        appliquerReglesMetier arm = new appliquerReglesMetier();
        ExcelQuoteRow rowSupport = null;
        try {
            Integer duree = Integer.parseInt(choiceDureeSupportManuel.getValue().toString());
            // MAJD XXXXXX ! il faut gérer le check box dans le devis manuel
            rowSupport = appliquerReglesMetier.AddSupport(eqr, duree, checkBoxDevisManuelUtiliserPromos.isSelected());
            // ExcelQuoteRow rowSupport = new ExcelQuoteRow();
            // rowSupport.setRef("ref Support");
            if (rowSupport != null) {
                System.out.println(rowSupport.toString());
                tableManualQuote.getItems().add(rowSupport);
            } else {
                System.out.println("RowSupport is null in onAddSupport");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onCheckBoxManuelUsePromoAction(ActionEvent actionEvent) {

    }
}
