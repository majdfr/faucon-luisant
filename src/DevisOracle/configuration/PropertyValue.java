package DevisOracle.configuration;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PropertyValue {

    private final StringProperty objet;
    private final StringProperty designation;
    private final StringProperty cle;
    private final StringProperty valeur;


    public PropertyValue(String objet, String designation, String cle, String valeur) {
        this.objet = new SimpleStringProperty(objet);
        this.designation = new SimpleStringProperty(designation);
        this.cle = new SimpleStringProperty(cle);
        this.valeur = new SimpleStringProperty(valeur);
    }

    public String getObjet() {
        return objet.get();
    }

    public StringProperty objetProperty() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet.set(objet);
    }

    public String getDesignation() {
        return designation.get();
    }

    public StringProperty designationProperty() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation.set(designation);
    }

    public String getCle() {
        return cle.get();
    }

    public StringProperty cleProperty() {
        return cle;
    }

    public void setCle(String cle) {
        this.cle.set(cle);
    }

    public String getValeur() {
        return valeur.get();
    }

    public StringProperty valeurProperty() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur.set(valeur);
    }
}
