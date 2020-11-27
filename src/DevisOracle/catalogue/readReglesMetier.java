package DevisOracle.catalogue;

import DevisOracle.shared.Tools;

import java.io.*;
import java.util.HashMap;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class readReglesMetier {
    static final Logger log = LogManager.getLogger(readCatalog.class.getName());
    private HashMap<String, RegleMetier> hmRegles = new HashMap<>();

    public HashMap<String, RegleMetier> getHmRegles() {
        return hmRegles;
    }

    public void setHmRegles(HashMap<String, RegleMetier> hmRegles) {
        this.hmRegles = hmRegles;
    }

    /**
         * Constructeur privé
         */
        private readReglesMetier() throws Exception{
            // this.data = readBusinessRulesFromExcel();
            String fichierRegles = Tools.readProperty("config.fichierReglesMetier");
            log.debug("lecture des règles métier depuis le fichier " + fichierRegles);
            // this.hmRegles = readJsonFile("G:\\Mon Drive\\Workspace\\devisOracleResources\\rules2.json");
            this.hmRegles = readJsonFile(fichierRegles);
            System.out.println("REGLES : " + this.hmRegles.toString());
        }

        public void updateReglesMetier() throws Exception{
            // this.data = readBusinessRulesFromExcel();

            String fichierRegles = Tools.readProperty("config.fichierReglesMetier");
            log.debug("Mise à jour des règles métier depuis le fichier " + fichierRegles);
            // this.hmRegles = readJsonFile("G:\\Mon Drive\\Workspace\\devisOracleResources\\rules2.json");
            this.hmRegles = readJsonFile(fichierRegles);
            System.out.println("REGLES : " + this.hmRegles.toString());
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
        private static readReglesMetier INSTANCE; //= new readBusinessRules();

        /**
         * Point d'accès pour l'instance unique du singleton
         */
        public static synchronized readReglesMetier getInstance() throws Exception {
            if (INSTANCE == null) {
                INSTANCE = new readReglesMetier();
            }
            return INSTANCE;
        }


    public static HashMap<String, RegleMetier> readJsonFile(String filename) throws Exception
    {

        // List<RegleMetier> lrm = new ArrayList<>();
        HashMap<String, RegleMetier> hmr = new HashMap<>();

        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(new FileReader(filename));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        // getting reglesMetier
        JSONArray ja = (JSONArray) jo.get("REGLES");
        // iterating
        Iterator itr2 = ja.iterator();
        while (itr2.hasNext())
        {

            RegleMetier regle = new RegleMetier();
            JSONObject jRegle = (JSONObject) itr2.next();

            String ref = (String) jRegle.get("ref");
            regle.setRef(ref);

            String configuration = (String) jRegle.get("configuration");
            regle.setConfiguration(configuration);

            Boolean ignorer = (Boolean) jRegle.get("Ignorer");
            if (ignorer == null) {
                regle.setIgnore(false);
            } else {
                regle.setIgnore(ignorer);
            }

            Boolean ignorerSupport = (Boolean) jRegle.get("IgnorerSupport");
            if (ignorerSupport == null) {
                regle.setIgnoreSupport(false);
            } else {
                regle.setIgnoreSupport(ignorerSupport);
            }

            // getting alternatives
            JSONArray altRefs = (JSONArray)jRegle.get("RemplacerPar");
            if (altRefs != null ) {
                Map<String, Integer> mkv = castJsonArrayToMap(altRefs, "RemplacerPar");
                regle.setRefAlternatives(mkv);
            }

            // getting AjouterRefs
            JSONArray addRefs = (JSONArray)jRegle.get("Ajouter");
            if (addRefs != null ) {
                Map<String, Integer> addmkv = castJsonArrayToMap(addRefs, "Ajouter");
                regle.setRefAjouter(addmkv);
            }

            hmr.put(ref, regle);

        }
    return hmr;

    } // end readJsonFile

    private static Map<String, Integer> castJsonArrayToMap(JSONArray ja, String item) {

        Map<String, Integer> rmap = new HashMap<>();

        // iterating over items
        Iterator itr1 = ja.iterator();

        while (itr1.hasNext())
        {
            JSONObject jo = (JSONObject) itr1.next();
            String ref = (String) jo.get("ref");
            long qte = (Long) jo.get("qte");

            // String qte = (String) jo.get("qte");
            Integer qteInt = Math.toIntExact(qte);
            rmap.put(ref,qteInt);
        }

        return rmap;
    }

}// end class