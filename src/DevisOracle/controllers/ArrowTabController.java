package DevisOracle.controllers;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import DevisOracle.DevisArrow.*;
import DevisOracle.Gui.guiMethods;
import DevisOracle.catalogue.CatalogRef;
import DevisOracle.shared.KeyValue;
import DevisOracle.shared.QuoteStructure;
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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArrowTabController {

    static final Logger log = LogManager.getLogger(ArrowTabController.class.getName());

    @FXML   private TextField txtFileToConvert, txtTotalHTSource, txtTotalHTDest, txtTotalTTCSource, txtTotalTTCDest;
    @FXML   private TextField  txtMargeDegagee, txtTotalTTCUgap, txtTotalHTUGAP, txtMarkupUgap;
    @FXML   private TextArea txtDesignationDevis;
    @FXML   private Pane paneDragFileHere;
    @FXML   private TableView<ExcelQuoteRow> tableQuote, tableCCQuote;
    @FXML   private TableColumn<ExcelQuoteRow, String> colRef, colCCRef, colCCRefUGAP;
    @FXML   private TableColumn<ExcelQuoteRow, String> colDesignation, colCCDesignation;
    @FXML   private TableColumn<ExcelQuoteRow, Integer> colQuantite, colCCQuantite, colSection, colCCSection;
    @FXML   private TableColumn<ExcelQuoteRow, Double> colPrixPublic, colCCPrixPublic;
    @FXML   private TableColumn<ExcelQuoteRow, Double> colPourcentageRemise;
    @FXML   private TableColumn<ExcelQuoteRow, Double> colPrixUnitaire, colCCPrixUnitaire;
    @FXML   private TableColumn<ExcelQuoteRow, CatalogRef> colPrixUnitaireReponse;
    @FXML   private TableColumn<ExcelQuoteRow, Double> colPrixAchatHT, colCCPrixVenteHT;
    @FXML   private TextArea txtBusinessRulesLogs;
    @FXML   private ListView lstOngletsDevis;
    @FXML   private ChoiceBox choiceDureeSupport, choiceCategorieProduit, choiceExportType, choiceDureeSupportManuel;
    @FXML   private CheckBox checkBoxUtiliserPromos;


    private MainController mainController;

    public void injectMainController(MainController mainController){
        this.mainController = mainController;
    }



    // When user drop a file into the specified zone, this method will be called.
    public void onDragFileDropped(DragEvent event) {
        System.out.println("Dragged file !");
        Dragboard db = event.getDragboard();
        event.setDropCompleted(true);
        event.consume();

        /* let the source know whether the string was successfully
         * transferred and used */
        boolean success = false;
        if (db.hasFiles()) {
            // can manage multiple files by iterating through the db.getFiles() list
            txtFileToConvert.setText(db.getFiles().get(0).toString());
            try {
                readExcelFile(txtFileToConvert.getText());

            } catch (IOException ioe) {
                ioe.printStackTrace();
                log.debug("GuiController - onDragFile error", ioe);
            }
            /*
            javafx.scene.control.Alert mylert = new Alert(Alert.AlertType.INFORMATION, "Lecture des règles métier en cours ... ");
            mylert.getButtonTypes().clear();
            mylert.setResizable(true);
            mylert.getDialogPane().setPrefSize(480, 170);
            mylert.showAndWait();
            readExcelAndApplyBusinessRules(txtFileToConvert.getText());
            mylert.close();
            //success = true;
             */
        }

        // Show in VIEW
        System.out.println("Dragged file !");
    }

    // When user click on btnConvert, this method will be called.
    public void onDragFileOver(DragEvent event) {
        // System.out.println("Dragged file over ... ");
        if (event.getGestureSource() != paneDragFileHere
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    public void onBtnFileSelectorClick(ActionEvent event) {
        System.out.println("Button file selector Clicked!");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez un devis ...");
        Stage stage = (Stage) paneDragFileHere.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        String fileToConvert = file.getAbsolutePath();
        // Show in VIEW
        txtFileToConvert.setText(fileToConvert);
    }


    public void onClickLstOnglets(MouseEvent arg0) {
        System.out.println("clicked on " + lstOngletsDevis.getSelectionModel().getSelectedItem() );
        KeyValue selected = (KeyValue)lstOngletsDevis.getSelectionModel().getSelectedItem();
        Integer sheetIndex = selected.getKey();
        readExcelQuoteToTable readxls = new readExcelQuoteToTable(txtFileToConvert.getText());
        System.out.println("clicked on key " + sheetIndex );
        try {
            readExcelAndApplyBusinessRules (readxls, sheetIndex);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("GuiController - onClickLstOnglets IO error", e);
        } catch (Exception e){
            e.printStackTrace();
            log.debug("GuiController - onClickLstOnglets error", e);
        }
    }

    private void readExcelFile(String myFile) throws IOException {
        if (myFile != null && !myFile.isEmpty()) {
            //ObservableList<Person> teamMembers =
            readExcelQuoteToTable readxls = new readExcelQuoteToTable(myFile);
            Integer numberSheets = readxls.getNumberSheets();
            // System.out.println("nombre onglets : " + numberSheets);
            List<KeyValue> onglets = readxls.getListWorkbookSheets();
            // System.out.println("onglets dans controller : " + onglets.toString());
            ObservableList<KeyValue> observOnglets = FXCollections.observableArrayList(onglets);
            //System.out.println("observable onglets dans controller : " + observOnglets.toString());
            lstOngletsDevis.setItems(observOnglets);
            lstOngletsDevis.setCellFactory(param -> new ListCell<KeyValue>() {
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

            readExcelAndApplyBusinessRules (readxls, 0);

        }
    }

    private void readExcelAndApplyBusinessRules(readExcelQuoteToTable readxls, Integer sheetIndex) throws IOException {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        DecimalFormat dfp = new DecimalFormat("##.##%");

        QuoteStructure qts = readxls.readSheetIndex(sheetIndex);
        List<ExcelQuoteRow> arrowQuote = qts.getQuote();
        System.out.println("list size : " + arrowQuote.size());
        System.out.println("Quote structure : " + qts.toString() );
        txtDesignationDevis.setText(readxls.getDesignationDevis());
        readxls.closeWorkbook();
        // on applique les règles business
        appliquerReglesMetier apply = new appliquerReglesMetier();

        Integer duree = Integer.parseInt(choiceDureeSupport.getValue().toString());
        String produit = choiceCategorieProduit.getValue().toString();
        // ReturnValue appliedBusinessRules = apply.applyBusinessRules(arrowQuote, duree);
        ReturnValue appliedBusinessRules = apply.appliquerReglesMetier (arrowQuote, duree, produit,  checkBoxUtiliserPromos.isSelected());

        // nouveau devis computa
        List<ExcelQuoteRow> ccQuote = appliedBusinessRules.getComputaQuote();
        // devis arrow vérifié, por voir si les prix sont les mêmes.
        List<ExcelQuoteRow> arrowQuoteVerified = appliedBusinessRules.getArrowQuote();
        // les logs correpsondants aux traitements appliquéd
        List<String> businessLogs = appliedBusinessRules.getLogs();

        try {
            // Affiche les données source du Devis Arrow
            // convertit en observableList pour l'objet TableView
            ObservableList<ExcelQuoteRow> observableList = FXCollections.observableArrayList(arrowQuoteVerified);
            colSection.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Integer>("sectionDevis"));
            colRef.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("ref"));
            colDesignation.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("designation"));
            colQuantite.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Integer>("quantite"));
            colPrixPublic.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixPublic"));
            colPourcentageRemise.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("pourcentageRemise"));
            colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixUnitaire"));
            colPrixAchatHT.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixAchatHT"));
            colPrixUnitaireReponse.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, CatalogRef>("catalogRef"));

            colPourcentageRemise.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
                @Override
                public TableCell<ExcelQuoteRow, Double> call(TableColumn<ExcelQuoteRow, Double> param) {
                    return new TableCell<ExcelQuoteRow, Double>() {
                        @Override
                        protected void updateItem(Double item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty && item != null) {
                                setText(dfp.format(item ));
                            } else {
                                setText(null);
                            }
                        }
                    };
                }
            });


            colPrixUnitaireReponse.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, CatalogRef>, TableCell<ExcelQuoteRow, CatalogRef>>() {
                @Override
                public TableCell<ExcelQuoteRow, CatalogRef> call(TableColumn<ExcelQuoteRow, CatalogRef> param) {
                    return new TableCell<ExcelQuoteRow, CatalogRef>() {
                        @Override
                        protected void updateItem(CatalogRef item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty && item != null) {
                                try {
                                    setText(currencyFormat.format(item.getPrixAchatHT()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    setText ("-");
                                }

                            } else {
                                setText(null);
                            }
                        }
                    };
                }
            });

            colPrixPublic.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
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

            colPrixAchatHT.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
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

            colPrixUnitaire.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
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

            tableQuote.setRowFactory(tv -> new TableRow<ExcelQuoteRow>() {
                @Override
                protected void updateItem(ExcelQuoteRow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty)
                        setStyle("");
                    else if (item.isAlerte()) {
                        setStyle("-fx-background-color: yellow");
                    }
                    else {
                        //setStyle("-fx-background-color: white");
                        setStyle("");
                    }
                }
            });

            tableQuote.getSelectionModel().setCellSelectionEnabled(true);
            tableQuote.setItems(null);
            //tableQuote.getItems().setAll(observableList);
            tableQuote.setItems(observableList);
            System.out.println("observable source list size : " + observableList.size());
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("GuiController - readExcelAndApplyBusinessRules error", e);
        }


        try {
            // Affiche les données destination : devis computa
            // convertit en observableList pour l'objet TableView
            ObservableList<ExcelQuoteRow> observableListCC = FXCollections.observableArrayList(ccQuote);
            colCCSection.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Integer>("sectionDevis"));
            colCCRef.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("ref"));
            colCCRefUGAP.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("refUGAP"));


            colCCDesignation.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, String>("designation"));
            colCCQuantite.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Integer>("quantite"));
            colCCPrixPublic.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixPublic"));
            //colCCPourcentageRemise.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("pourcentageRemise"));
            colCCPrixUnitaire.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixUnitaire"));
            colCCPrixVenteHT.setCellValueFactory(new PropertyValueFactory<ExcelQuoteRow, Double>("prixVenteHT"));

            colCCPrixPublic.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
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

            colCCPrixUnitaire.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
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

            colCCPrixVenteHT.setCellFactory(new Callback<TableColumn<ExcelQuoteRow, Double>, TableCell<ExcelQuoteRow, Double>>() {
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

            tableCCQuote.setRowFactory(tv -> new TableRow<ExcelQuoteRow>() {
                @Override
                protected void updateItem(ExcelQuoteRow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty)
                        setStyle("");
                    else {
                        System.out.println("item TableccQuote : " + item.toString());
                        if (item.isAlerte()) {
                            //setStyle("-fx-background-color: red;-fx-text-fill: white; ");
                            // this.setTextFill(Color.WHITE);
                            this.getStyleClass().add("table-cell-alerte");
                        } else if (item.isModifiee()) {
                            System.out.println("    BINGO ! item TableccQuote isModifier  = " + item.isModifiee());
                            this.getStyleClass().add("table-cell-modified");
                            // setStyle("-fx-text-fill: green ");
                        } else {
                            // setStyle("-fx-background-color: white");
                            setStyle("");
                        }
                    }
                }
            });

            tableCCQuote.getSelectionModel().setCellSelectionEnabled(true);
            tableCCQuote.setItems(null);
            tableCCQuote.setItems(observableListCC);
            System.out.println("observable dest list size : " + observableListCC.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("GuiController - readExcelAndApplyBusinessRules error", ex);
        }
        // afficher les logs
        String myLogs= String.join("\n", businessLogs);
        mainController.setLogtext(myLogs);

        // LogsTabController logController = loader.<LogsTabController>getController();
        //logController.setLogtext(myLogs);

        CalculerTotaux();
    }

    /*
    @FXML
    public void copyText() {
        String elementTableSelected = tableQuote.getSelectionModel().getSelectedItem().getNotificationsLanguage().esProperty().getValue();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(elementTableSelected);
        clipboard.setContent(content);
        System.out.println(elementTableSelected);
    }

     */

    public static String formatPercent(double done, int digits) {
        DecimalFormat percentFormat = new DecimalFormat("0.0%");
        percentFormat.setDecimalSeparatorAlwaysShown(false);
        percentFormat.setMinimumFractionDigits(digits);
        percentFormat.setMaximumFractionDigits(digits);
        return percentFormat.format(done);
    }

    public void onChoiceSupportChange(ActionEvent actionEvent) {
        System.out.println("Changed support duration ! " + choiceDureeSupport.getValue().toString());
        // System.out.println("clicked on " + lstOngletsDevis.getSelectionModel().getSelectedItem() );
        KeyValue selected = (KeyValue)lstOngletsDevis.getSelectionModel().getSelectedItem();
        Integer sheetIndex = 0;
        if (selected != null) {
            sheetIndex = selected.getKey();
        }
        readExcelQuoteToTable readxls = new readExcelQuoteToTable(txtFileToConvert.getText());
        System.out.println("clicked on key " + sheetIndex );
        try {
            readExcelAndApplyBusinessRules (readxls, sheetIndex);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("GuiController - onChoiceSupportChange IO error", e);
        } catch (Exception e){
            e.printStackTrace();
            log.debug("GuiController - onChoiceSupportChange error", e);
        }
    }


    public void onChoiceCategorieProduitChange(ActionEvent actionEvent) {
        System.out.println("Changed product category ! " + choiceDureeSupport.getValue().toString());
        // System.out.println("clicked on " + lstOngletsDevis.getSelectionModel().getSelectedItem() );
        KeyValue selected = (KeyValue)lstOngletsDevis.getSelectionModel().getSelectedItem();
        Integer sheetIndex = 0;
        if (selected != null) {
            sheetIndex = selected.getKey();
        }
        readExcelQuoteToTable readxls = new readExcelQuoteToTable(txtFileToConvert.getText());
        System.out.println("clicked on key " + sheetIndex );
        try {
            readExcelAndApplyBusinessRules (readxls, sheetIndex);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("GuiController - onChoiceCategorieProduitChange IO error", e);
        } catch (Exception e){
            e.printStackTrace();
            log.debug("GuiController - onChoiceCategorieProduitChange error", e);
        }
    }


    public void exporter(ActionEvent actionEvent) {

        //exportRules2Json ejson = new exportRules2Json();
        //ejson.exportRules("G:\\Mon Drive\\Boulot\\Computacenter France\\Projets Clients LOB\\UGAP\\AO UGAP Oracle Full Eté 2019\\15-catalogue\\Source_unique_données 4.0.xlsx", 5);

        System.out.println("Button export Clicked!");

        Double montantPrixAchat = guiMethods.getMontantPrixAchat(tableQuote.getItems());

        switch(choiceExportType.getValue().toString()) {
            case "Devis Client":
                guiMethods.exportDevisClient(tableCCQuote.getItems(), txtFileToConvert.getText(), txtDesignationDevis.getText(),false, montantPrixAchat);
                break;
            case "Devis Interne":
                // code block
                guiMethods.exportDevisClient(tableCCQuote.getItems(), txtFileToConvert.getText(), txtDesignationDevis.getText(),true, montantPrixAchat);
                break;
            case "Devis pour analyse":
                exportAnalyseDevis.export(txtFileToConvert.getText());
                break;
            default:
                // code block
        }


    }

    public void onClickAppliquerMarkupUgap(ActionEvent actionEvent) {
        CalculerTotaux();
    }

    public void CalculerTotaux() {
        try {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            DecimalFormat dfp = new DecimalFormat("##.##%");

            String pourcentageMarkupStr = txtMarkupUgap.getText().trim();
            Double pourcentageMarkup = Double.valueOf(pourcentageMarkupStr);

            Double totalHTSource = Double.valueOf(0);
            for (ExcelQuoteRow item : tableQuote.getItems()) {
                if (item != null ) {
                    totalHTSource = totalHTSource + item.getPrixAchatHT();
                }
                //System.out.println("totalHTSource : " + totalHTSource);
            }
            System.out.println("final totalHTSource : " + totalHTSource);

            txtTotalHTSource.setText(currencyFormat.format(totalHTSource));
            txtTotalTTCSource.setText(currencyFormat.format(totalHTSource * 1.2));

            Double totalHTDest = Double.valueOf(0);
            for (ExcelQuoteRow item : tableCCQuote.getItems()) {
                if (item != null ) {
                    totalHTDest = totalHTDest + item.getPrixVenteHT();
                }
            }
            txtTotalHTDest.setText(currencyFormat.format(totalHTDest));
            txtTotalTTCDest.setText(currencyFormat.format(totalHTDest * 1.2));

            Double marge =  (totalHTDest - totalHTSource) / totalHTSource  ;

            txtMargeDegagee.setText(formatPercent(marge, 2) );


            Double prixUgapHT = totalHTDest / (1 - pourcentageMarkup / 100);
            Double prixUgapTTC = prixUgapHT * 1.2;
            txtTotalHTUGAP.setText(currencyFormat.format(prixUgapHT));
            txtTotalTTCUgap.setText(currencyFormat.format(prixUgapTTC));


        } catch (Exception e)
        {
            e.printStackTrace();
            log.debug("GuiController - CalculerTotaux error", e);
        }
    }


    public void onCheckBoxActionEvent(ActionEvent actionEvent) {
        System.out.println("Clickk action Event : " + checkBoxUtiliserPromos.isSelected());
        KeyValue selected = (KeyValue)lstOngletsDevis.getSelectionModel().getSelectedItem();
        Integer sheetIndex = 0;
        if (selected != null) {
            sheetIndex = selected.getKey();
        }
        readExcelQuoteToTable readxls = new readExcelQuoteToTable(txtFileToConvert.getText());

        try {
            readExcelAndApplyBusinessRules (readxls, sheetIndex);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("GuiController - onChoiceCategorieProduitChange IO error", e);
        } catch (Exception e){
            e.printStackTrace();
            log.debug("GuiController - onChoiceCategorieProduitChange error", e);
        }
    }
}
