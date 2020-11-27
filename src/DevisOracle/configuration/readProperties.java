package DevisOracle.configuration;


import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class readProperties {

    static final Logger log = LogManager.getLogger(readProperties.class.getName());

    private static String devfilename = "G:\\Mon Drive\\Workspace\\devisOracleResources\\config\\config.properties";
    private static String filename = "config.properties";
    private static FileBasedConfigurationBuilder<PropertiesConfiguration> builder;
    private static List<PropertyValue> configAsList;
    private static PropertiesConfiguration config;

    /**
     * Constructeur privé
     */
    private readProperties() {
        readProperties();
    }

    public static FileBasedConfigurationBuilder<PropertiesConfiguration> getBuilder() {
        return builder;
    }

    public List<PropertyValue> getConfigAsObservableList() {
        return configAsList;
    }

    /**
     * Sécurité anti-désérialisation
     */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Instance unique pré-initialisée
     */
    private static readProperties INSTANCE = new readProperties();

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static synchronized readProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new readProperties();
        }
        return INSTANCE;
    }

    public static String getProperty(String key) {
        return config.getString(key);
    }

    public static void setProperty(String key, String value) {
        try {
            //  PropertiesConfiguration config = builder.getConfiguration();
            config.setProperty(key, value);
            // updatePropertiesAsObservableList();
            // saveProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readProperties() {

        try {

            // String env = System.getProperty("devis.oracle.env", "null !");
            // System.out.println("System env : " + env);
            // Properties p = System.getProperties();
            // p.list(System.out);

            String var = System.getenv("DEVISORACLEENV");
            System.out.println("System var : " + var);
            String myConfigFile;

            if (var == null ) {
                System.out.println("We're in production");
                myConfigFile = System.getProperty("user.dir").concat("\\config\\").concat(filename);
            } else {
                if (var.equals("DEV")) {
                    myConfigFile = devfilename;
                    System.out.println("System env is DEV !! yay :)");
                } else {
                    System.out.println("We're in production");
                    myConfigFile = System.getProperty("user.dir").concat("\\config\\").concat(filename);
                }
            }


            // File file = new File(filename);
            File file = new File(myConfigFile);
            ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(',');

            PropertiesBuilderParameters propertyParameters = new Parameters().properties();
            propertyParameters.setFile(file);
            propertyParameters.setThrowExceptionOnMissing(true);
            propertyParameters.setListDelimiterHandler(delimiter);

            FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
                    PropertiesConfiguration.class);

            builder.configure(propertyParameters);
            builder.setAutoSave(true);

            config = builder.getConfiguration();
            //System.out.println("Config size : " + config.size());

            updatePropertiesAsObservableList();
        }
        catch (Exception e ) {
            e.printStackTrace();
        }

    }


    private static void updatePropertiesAsObservableList() {

        try {
            configAsList = new ArrayList<>();
            // PropertiesConfiguration config = builder.getConfiguration();
            Iterator<String> keys = config.getKeys();
            keys.forEachRemaining(key -> {
                PropertyValue pv = null;
                if (!key.isEmpty()) {
                    //System.out.println("key  : " + key.toString());
                    switch (key) {
                        /* Options Devis Arrow */
                        case "DevisArrow.designationDevis":
                            pv = new PropertyValue("DevisArrow", "Désignation du devis", key, config.getString(key));
                            break;
                        case "DevisArrow.premiereLigne":
                            pv = new PropertyValue("DevisArrow", "Première ligne (avec une référence) du devis", key, config.getString(key));
                            break;
                        case "DevisArrow.reference":
                            pv = new PropertyValue("DevisArrow", "Référence Constructeur", key, config.getString(key));
                            break;
                        case "DevisArrow.designation":
                            pv = new PropertyValue("DevisArrow", "Désignation Constructeur", key, config.getString(key));
                            break;
                        case "DevisArrow.quantite":
                            pv = new PropertyValue("DevisArrow", "Quantité", key, config.getString(key));
                            break;
                        case "DevisArrow.prixPublic":
                            pv = new PropertyValue("DevisArrow", "Prix Public", key, config.getString(key));
                            break;
                        case "DevisArrow.remise":
                            pv = new PropertyValue("DevisArrow", "Remise Arrow", key, config.getString(key));
                            break;
                        case "DevisArrow.prixUnitaire":
                            pv = new PropertyValue("DevisArrow", "Prix Unitaire", key, config.getString(key));
                            break;
                        case "DevisArrow.montantHT":
                            pv = new PropertyValue("DevisArrow", "Montant HT (PU x qte)", key, config.getString(key));
                            break;
                        /* Options Règles métier */
                        case "RegleMetier.categorieProduit":
                            pv = new PropertyValue("Règle Métier", "Catégorie Produit", key, config.getString(key));
                            break;
                        case "RegleMetier.referenceUGAP":
                            pv = new PropertyValue("Règle Métier", "Référence UGAP", key, config.getString(key));
                            break;
                        case "RegleMetier.referenceConstructeur":
                            pv = new PropertyValue("Règle Métier", "Référence Constructeur", key, config.getString(key));
                            break;
                        case "RegleMetier.referenceDistributeur":
                            pv = new PropertyValue("Règle Métier", "Référence Distributeur", key, config.getString(key));
                            break;
                        case "RegleMetier.designation":
                            pv = new PropertyValue("Règle Métier", "Désignation Constructeur", key, config.getString(key));
                            break;
                        case "RegleMetier.prixPublicReponse":
                            pv = new PropertyValue("Règle Métier", "Prix Public de la réponse à l'AO", key, config.getString(key));
                            break;
                        case "RegleMetier.prixAchat":
                            pv = new PropertyValue("Règle Métier", "Prix achat à Arrow", key, config.getString(key));
                            break;
                        case "RegleMetier.prixNetUGAP":
                            pv = new PropertyValue("Règle Métier", "Prix Net pour UGAP", key, config.getString(key));
                            break;
                        case "RegleMetier.refAlternative1":
                            pv = new PropertyValue("Règle Métier", "Référence alternative 1", key, config.getString(key));
                            break;
                        case "RegleMetier.refAlternative1Quantite":
                            pv = new PropertyValue("Règle Métier", "Quantité pour Référence alternative 1", key, config.getString(key));
                            break;
                        case "RegleMetier.refAlternative2":
                            pv = new PropertyValue("Règle Métier", "Référence alternative 2", key, config.getString(key));
                            break;
                        case "RegleMetier.refAlternative2Quantite":
                            pv = new PropertyValue("Règle Métier", "Quantité pour Référence alternative 1", key, config.getString(key));
                            break;
                        case "RegleMetier.ajouterRef1":
                            pv = new PropertyValue("Règle Métier", "Référence 1 à rajouter", key, config.getString(key));
                            break;
                        case "RegleMetier.ajouterRef1Quantite":
                            pv = new PropertyValue("Règle Métier", "Quantité de Référence 1 à rajouter", key, config.getString(key));
                            break;
                        case "config.fichierCatalogue":
                            pv = new PropertyValue("Configuration", "Fichier Catalogue (ZMOD enrichi)", key, config.getString(key));
                            break;
                        case "config.fichierReglesMetier":
                            pv = new PropertyValue("Configuration", "Fichier de règles métier (json)", key, config.getString(key));
                            break;
                        case "config.modeleDevis":
                            pv = new PropertyValue("Configuration", "Fichier de modèle de devis Computacenter", key, config.getString(key));
                            break;
                        case "SupportRenew.ongletLogiciel":
                            pv = new PropertyValue("Support Renew", "Nom de l'onglet logiciel", key, config.getString(key));
                            break;
                        case "SupportRenew.ongletMateriel":
                            pv = new PropertyValue("Support Renew", "Nom de l'onglet matériel", key, config.getString(key));
                            break;
                        case "SupportRenew.motCleAction":
                            pv = new PropertyValue("Support Renew", "Mot clé pour traiter la ligne", key, config.getString(key));
                            break;
                        default:
                            pv = new PropertyValue("Autres", "-", key, config.getString(key));
                    }
                    // if (pv != null) {
                        configAsList.add(pv);
                    //}
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erreur lors de la lecture des paramètres : ", e);
        }

    }


}