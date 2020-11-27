package DevisOracle.controllers;

import DevisOracle.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {
    static final Logger log = LogManager.getLogger(MainController.class.getName());

    @FXML private ArrowTabController arrowTabController;
    @FXML private DevisManuelTabController devisManuelTabController;
    @FXML private AboutTabController aboutTabController;
    @FXML private LogsTabController logsTabController;

    /*
    public TextArea getVisualLog() {
        return  loggerTabController.getLoggerTxtArea();
    }
    */

    public void setLogtext(String text) {
        logsTabController.setLogtext(text);
    }


    @FXML private void initialize() {
        arrowTabController.injectMainController(this);

    }

    // @Override
    /*
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // TODO (don't really need to do anything here).

        List<PropertyValue> lpv = readProperties.getInstance().getConfigAsObservableList();
        showProperties(lpv);
        List<ExcelQuoteRow> devisManuel = new ArrayList<>();
        initializeDevisManuel(devisManuel);
        txtDestinationExportDossier.setText(Tools.readProperty("DevisManuel.destination"));
    }

     */

}
