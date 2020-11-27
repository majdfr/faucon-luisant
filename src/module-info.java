module DevisOracle {
    requires poi.ooxml;
    requires poi;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.configuration2;
    requires commons.beanutils;
    requires java.sql;
    requires json.simple;
    requires org.apache.logging.log4j;
    exports DevisOracle;
    opens DevisOracle.controllers;
    opens DevisOracle.DevisDigora;
    opens DevisOracle.DevisArrow;
    opens DevisOracle.Gui;
    opens DevisOracle.shared;
    opens DevisOracle.configuration;
    opens DevisOracle.catalogue;
    opens DevisOracle.supportRenew;
    exports DevisOracle.controllers;
    opens DevisOracle;
}

