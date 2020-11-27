package DevisOracle.controllers;

import DevisOracle.Gui.EditingCell;
import DevisOracle.catalogue.readCatalog;
import DevisOracle.catalogue.readReglesMetier;
import DevisOracle.configuration.PropertyValue;
import DevisOracle.configuration.readProperties;
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

import java.util.List;

public class ConfigurationTabController {
    static final Logger log = LogManager.getLogger(ConfigurationTabController.class.getName());
    // properties controls
    @FXML   private Button btnSaveOptions;
    @FXML   private TableView<PropertyValue> tableOptions;
    @FXML   private TableColumn<PropertyValue, String> colOptionObject, colOptionDesignation, colOptionKey, colOptionValue;


       /*
    ################################################################
    #################### Manage Properties #########################
     */

    public void initialize() {

        List<PropertyValue> lpv = readProperties.getInstance().getConfigAsObservableList();
        showProperties(lpv);
    }

    public void showProperties(List<PropertyValue> properties) {


        try {
            // Affiche les données source du Devis Arrow
            // convertit en observableList pour l'objet TableView
            ObservableList<PropertyValue> observableList = FXCollections.observableArrayList(properties);
            colOptionObject.setCellValueFactory(new PropertyValueFactory<PropertyValue, String>("objet"));
            colOptionDesignation.setCellValueFactory(new PropertyValueFactory<PropertyValue, String>("designation"));
            colOptionKey.setCellValueFactory(new PropertyValueFactory<PropertyValue, String>("cle"));
            colOptionValue.setCellValueFactory(new PropertyValueFactory<PropertyValue, String>("valeur"));

            tableOptions.setEditable(true);

            Callback<TableColumn<PropertyValue, String>, TableCell<PropertyValue, String>> cellFactory
                    = (TableColumn<PropertyValue, String> param) -> new EditingCell();

            //--- Add for Editable Cell of Value field, in String
            colOptionValue.setCellFactory(cellFactory);
            colOptionValue.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<PropertyValue, String>>() {
                        @Override public void handle(TableColumn.CellEditEvent<PropertyValue, String> t) {
                            ((PropertyValue)t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())).setValeur(t.getNewValue());
                            // mise à jour des options, ainsi que du fichier de paramétrage
                            readProperties.getInstance().setProperty(t.getRowValue().getCle(),t.getNewValue() );
                        }
                    });
            //---

            tableOptions.setItems(null);
            tableOptions.setItems(observableList);


        } catch (Exception e) {
            e.printStackTrace();
            log.debug("GuiController - showProperties error", e);
        }
    }

    public void onBtnClickReloadBusinessRules(ActionEvent event) {
        try {
            // readCatalog.getInstance().updateCatalog();
            readReglesMetier.getInstance().updateReglesMetier();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR );
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la lecture des règles métier");
            alert.setContentText("Veuillez vérifier le fichier des règles métier, ainsi que la configuration.");
            alert.showAndWait();
            e.printStackTrace();
            log.debug("GuiController - onBtnClickReloadBusinessRules error", e);
        }
    }

    public void onBtnClickReloadCatalogue(ActionEvent event) {
        try {
            readCatalog.getInstance().updateCatalog();
            // readReglesMetier.getInstance().updateReglesMetier();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR );
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la lecture du catalogue");
            alert.setContentText("Veuillez vérifier le fichier du catalogue, ainsi que la configuration.");
            alert.showAndWait();
            e.printStackTrace();
            log.debug("GuiController - onBtnClickReloadCatalogue error", e);
        }
    }


}
