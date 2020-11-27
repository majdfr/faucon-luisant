package DevisOracle.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogsTabController {

    static final Logger log = LogManager.getLogger(LogsTabController.class.getName());
    @FXML private TextArea txtBusinessRulesLogs;

    public void setLogtext(String text) {
        txtBusinessRulesLogs.setText(text);
    }

}
