package DevisOracle.controllers;

import DevisOracle.shared.KeyValue;
import DevisOracle.supportRenew.SupportRenew;
import DevisOracle.supportRenew.supportRenewMgmt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.List;

public class RenewSupportTabController {

    static final Logger log = LogManager.getLogger(RenewSupportTabController.class.getName());

    // Support renew
    @FXML private TableView<SupportRenew> tableSupportRenew;
    @FXML private TableColumn<SupportRenew, String> colRenewRefUGAP, colRenewClient,
            colRenewContrat, colRenewDesignation, colRenewCSI,colRenewQte;
    @FXML private TableColumn<SupportRenew, Double>   colRenewPrixPublicOracle, colRenewPrixAchatUGAP;
    @FXML private ListView lstOngletsRenewSupport;
    @FXML private TextField txtRenewFileToConvert;
    @FXML private Pane paneReenewDragFileHere;

     /* #############################################################

    Gestion des devis de maintenance (renew)

    ################################################################
     */

    public void onBtnRenewFileSelectorClick(ActionEvent actionEvent) {
    }

    public void onRenewDragFileDropped(DragEvent event) {

        Dragboard db = event.getDragboard();
        event.setDropCompleted(true);
        event.consume();

        /* let the source know whether the string was successfully
         * transferred and used */
        boolean success = false;
        if (db.hasFiles()) {
            // can manage multiple files by iterating through the db.getFiles() list
            txtRenewFileToConvert.setText(db.getFiles().get(0).toString());
            try {
                // readExcelFile(txtRenewFileToConvert.getText());
                // Tools.readWorkbook();
                List<KeyValue> lstOnglets = supportRenewMgmt.listSheets(txtRenewFileToConvert.getText());
                ObservableList<KeyValue> observOnglets = FXCollections.observableArrayList(lstOnglets);
                //System.out.println("observable onglets dans controller : " + observOnglets.toString());
                lstOngletsRenewSupport.setItems(observOnglets);
                lstOngletsRenewSupport.setCellFactory(param -> new ListCell<KeyValue>() {
                    @Override
                    protected void updateItem(KeyValue item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || item == null) {
                            setText(null);
                        } else {
                            setText(item.getValue());
                        }
                    }
                });

            } catch (Exception ioe) {
                ioe.printStackTrace();
                log.debug("GuiController - onRenewDragFileDropped IO error", ioe);
            }
        }

    }


    public void onClickLstOngletsRenew(MouseEvent mouseEvent) {
        System.out.println("clicked on " + lstOngletsRenewSupport.getSelectionModel().getSelectedItem() );
        KeyValue selected = (KeyValue)lstOngletsRenewSupport.getSelectionModel().getSelectedItem();
        Integer sheetIndex = selected.getKey();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        List<SupportRenew> lstSR = supportRenewMgmt.readSupportRenewFile(txtRenewFileToConvert.getText(), sheetIndex);
        ObservableList<SupportRenew> observableList = FXCollections.observableArrayList(lstSR);

        colRenewRefUGAP.setCellValueFactory(new PropertyValueFactory<SupportRenew, String>("refUGAP"));
        colRenewClient.setCellValueFactory(new PropertyValueFactory<SupportRenew, String>("client"));
        colRenewContrat.setCellValueFactory(new PropertyValueFactory<SupportRenew, String>("contrat"));
        colRenewDesignation.setCellValueFactory(new PropertyValueFactory<SupportRenew, String>("designation"));
        colRenewCSI.setCellValueFactory(new PropertyValueFactory<SupportRenew, String>("csi"));
        colRenewQte.setCellValueFactory(new PropertyValueFactory<SupportRenew, String>("quantite"));
        colRenewPrixPublicOracle.setCellValueFactory(new PropertyValueFactory<SupportRenew, Double>("prixPublicOracle"));
        colRenewPrixAchatUGAP.setCellValueFactory(new PropertyValueFactory<SupportRenew, Double>("prixAchatUGAP"));

        colRenewPrixPublicOracle.setCellFactory(new Callback<TableColumn<SupportRenew, Double>, TableCell<SupportRenew, Double>>() {
            @Override
            public TableCell<SupportRenew, Double> call(TableColumn<SupportRenew, Double> param) {
                return new TableCell<SupportRenew, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            setText(currencyFormat.format(item ));
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        colRenewPrixAchatUGAP.setCellFactory(new Callback<TableColumn<SupportRenew, Double>, TableCell<SupportRenew, Double>>() {
            @Override
            public TableCell<SupportRenew, Double> call(TableColumn<SupportRenew, Double> param) {
                return new TableCell<SupportRenew, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            setText(currencyFormat.format(item ));
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        tableSupportRenew.setItems(null);
        tableSupportRenew.setItems(observableList);

    }

    public void onExportRenewQuotes(ActionEvent actionEvent) {
        supportRenewMgmt.exportAll(tableSupportRenew.getItems(), txtRenewFileToConvert.getText());
    }

    // When user click on btnConvert, this method will be called.
    public void onDragFileOver(DragEvent event) {
        // System.out.println("Dragged file over ... ");
        if (event.getGestureSource() != paneReenewDragFileHere
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

}
