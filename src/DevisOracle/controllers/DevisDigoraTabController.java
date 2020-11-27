package DevisOracle.controllers;

import DevisOracle.DevisArrow.ExcelQuoteRow;
import DevisOracle.DevisDigora.DigoraAnalyse;
import DevisOracle.DevisDigora.readDevisDigora;
import DevisOracle.shared.KeyValue;
import DevisOracle.shared.QuoteStructure;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

public class DevisDigoraTabController {

    static final Logger log = LogManager.getLogger(DevisDigoraTabController.class.getName());
    // Digora
    @FXML private TextField txtDigoraFileToConvert;
    @FXML private TableView<ExcelQuoteRow> tableDevisDigora;
    @FXML private TableColumn<ExcelQuoteRow, String> colDigoraRefConstructeur, colDigoraRefUGAP, colDigoraDesignation;
    @FXML private TableColumn<ExcelQuoteRow, Double>  colDigoraPrixUnitaire, colDigoraPrixTotal;
    @FXML private TableColumn<ExcelQuoteRow, Integer>  colDigoraQte;
    @FXML private ListView lstOngletsDevisDigora;
    @FXML private Pane panelDigoraDragFileHere;

    // table d'affichage des résultats (montant achat/vente, marge, etc ...)
    @FXML private TableView<DigoraAnalyse> tableDevisDigoraAnalyse;
    @FXML private TableColumn<DigoraAnalyse, String> colDigoraAnalyseInfo;
    @FXML private TableColumn<DigoraAnalyse, String>  colDigoraAnalyseValue;

      /* #############################################################

    Gestion des devis DIGORA

    ################################################################
     */


    public void onClickLstOngletsDigora(MouseEvent mouseEvent) {
        System.out.println("clicked on " + lstOngletsDevisDigora.getSelectionModel().getSelectedItem() );
        KeyValue selected = (KeyValue)lstOngletsDevisDigora.getSelectionModel().getSelectedItem();
        Integer sheetIndex = selected.getKey();
        QuoteStructure qs = readDevisDigora.readFile(txtDigoraFileToConvert.getText(), sheetIndex);
        showDigoraQuote (sheetIndex, qs);
        List<DigoraAnalyse> analysis = readDevisDigora.AnalyserDevisDigora(qs);
        showDigoraAnalysis(analysis);

    }


    private void showDigoraQuote(Integer sheetIndex, QuoteStructure qs) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


        ObservableList<ExcelQuoteRow> observableList = FXCollections.observableArrayList(qs.getQuote());

        colDigoraRefConstructeur.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("ref"));
        colDigoraRefUGAP.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("refUGAP"));
        colDigoraDesignation.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("designation"));
        colDigoraQte.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Integer>("quantite"));
        colDigoraPrixUnitaire.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixUnitaire"));
        colDigoraPrixTotal.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixVenteHT"));

        colDigoraPrixUnitaire.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
            @Override
            public TableCell<ExcelQuoteRow, Double> call(TableColumn<ExcelQuoteRow, Double> param) {
                return new TableCell<ExcelQuoteRow, Double>() {
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

        colDigoraPrixTotal.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
            @Override
            public TableCell<ExcelQuoteRow, Double> call(TableColumn<ExcelQuoteRow, Double> param) {
                return new TableCell<ExcelQuoteRow, Double>() {
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

        tableDevisDigora.setItems(null);
        tableDevisDigora.setItems(observableList);

    }


    private void showDigoraAnalysis(List<DigoraAnalyse> analysis) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        ObservableList<DigoraAnalyse> observableList = FXCollections.observableArrayList(analysis);

        colDigoraAnalyseInfo.setCellValueFactory(new PropertyValueFactory<DigoraAnalyse, String>("info"));
        colDigoraAnalyseValue.setCellValueFactory(new PropertyValueFactory<DigoraAnalyse, String>("value"));

        tableDevisDigoraAnalyse.setRowFactory(tv -> new TableRow<DigoraAnalyse>() {
            @Override
            protected void updateItem(DigoraAnalyse item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty)
                    setStyle("");
                else if (item.isAlerte()) {
                    setStyle("-fx-background-color: red");
                }
                else {
                    //setStyle("-fx-background-color: white");
                    setStyle("");
                }
            }
        });

        tableDevisDigoraAnalyse.setItems(null);
        tableDevisDigoraAnalyse.setItems(observableList);

    }

    public void onDigoraDragFileDropped(DragEvent event) {

        Dragboard db = event.getDragboard();
        event.setDropCompleted(true);
        event.consume();

        /* let the source know whether the string was successfully
         * transferred and used */
        boolean success = false;
        if (db.hasFiles()) {
            // can manage multiple files by iterating through the db.getFiles() list
            onDigoraQuoteProvided(db.getFiles().get(0).toString());
        }

    }

    public void onBtnDigoraFileSelectorClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez un devis ...");
        Stage stage = (Stage) panelDigoraDragFileHere.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        String fileToConvert = file.getAbsolutePath();
        // Show in VIEW
        onDigoraQuoteProvided(fileToConvert);
        // txtDigoraFileToConvert.setText(fileToConvert);

    }

    private void onDigoraQuoteProvided(String filename) {

        txtDigoraFileToConvert.setText(filename);
        try {
            List<KeyValue> lstOnglets = supportRenewMgmt.listSheets(txtDigoraFileToConvert.getText());
            ObservableList<KeyValue> observOnglets = FXCollections.observableArrayList(lstOnglets);

            lstOngletsDevisDigora.setItems(observOnglets);
            lstOngletsDevisDigora.setCellFactory(param -> new ListCell<KeyValue>() {
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
            log.debug("GuiController - onDigoraDragFileDropped IO error", ioe);
        }

    }


    public void onBtnDigoraExportQuote(ActionEvent actionEvent) {
        KeyValue selected = (KeyValue)lstOngletsDevisDigora.getSelectionModel().getSelectedItem();
        Integer sheetIndex = selected.getKey();
        readDevisDigora.exportQuote(txtDigoraFileToConvert.getText(), sheetIndex);
    }

    // When user click on btnConvert, this method will be called.
    public void onDragFileOver(DragEvent event) {
        // System.out.println("Dragged file over ... ");
        if (event.getGestureSource() != panelDigoraDragFileHere
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

}
