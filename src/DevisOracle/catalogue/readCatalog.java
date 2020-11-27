package DevisOracle.catalogue;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import DevisOracle.shared.Tools;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class readCatalog {

    static final Logger log = LogManager.getLogger(readCatalog.class.getName());

    // private MultiValuedMap<String, BusinessRule> businessRules;
    private HashMap<String, CatalogRef> catalogParRefConstructeur = new HashMap<String, CatalogRef>();
    private HashMap<String, CatalogRef> catalogParRefUGAP = new HashMap<String, CatalogRef>();
    private HashMap<String, CatalogRef> catalogParRefConstructeurConfig = new HashMap<String, CatalogRef>();

    public HashMap<String, CatalogRef> getCatalogParRefConstructeur() {
        return catalogParRefConstructeur;
    }
    public void setCatalogParRefConstructeur(HashMap<String, CatalogRef> catalogParRefConstructeur) {
        this.catalogParRefConstructeur = catalogParRefConstructeur;
    }
    public HashMap<String, CatalogRef> getCatalogParRefUGAP() {
        return catalogParRefUGAP;
    }
    public void setCatalogParRefUGAP(HashMap<String, CatalogRef> catalogParRefUGAP) {
        this.catalogParRefUGAP = catalogParRefUGAP;
    }

    public HashMap<String, CatalogRef> getCatalogParRefConstructeurConfig() {
        return catalogParRefConstructeurConfig;
    }

    public void setCatalogParRefConstructeurConfig(HashMap<String, CatalogRef> catalogParRefConstructeurConfig) {
        this.catalogParRefConstructeurConfig = catalogParRefConstructeurConfig;
    }

    /**
     * Constructeur privé
     */
    private readCatalog() throws Exception{
        readBusinessRulesFromExcel();
    }

    public void updateCatalog() throws Exception{
        readBusinessRulesFromExcel();
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
    private static readCatalog INSTANCE; //= new readBusinessRules();

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static synchronized readCatalog getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new readCatalog();
        }
        return INSTANCE;
    }

    public void readBusinessRulesFromExcel() throws Exception {

        //List<ExcelQuoteRow> quote = new ArrayList<ExcelQuoteRow>();
        // create new multivaluedmap object
        //  HashMap<String, CatalogRef> mesReglesParRefConstructeur = new HashMap<String, CatalogRef>();
        // HashMap<String, CatalogRef> mesReglesParRefUGAP = new HashMap<String, CatalogRef>();
        // MultiValuedMap<String, BusinessRule> myBusinessRules = new LinkedMultiValueMap<>();

        log.debug("Lecture du catalogue métier depuis Excel");

        //////////////////////////////////////////////////////////////////////////////////////
        /* ouverture fichier avec les propriétés. pose prbm de sécurité des informations.
        String fichierCatalog= Tools.readProperty("config.fichierCatalogue");
        File file = new File(fichierCatalog);
        FileInputStream fis = new FileInputStream(file);
         */

        // on intègre le catalogue directement dans le jar. c'est pas top, mais mieux que rien.
        InputStream fis = getClass().getClassLoader().getResourceAsStream("Source_unique_donnees_5_9.xlsx");
        if (fis == null) {
            throw new FileNotFoundException("Fichier de catalogue Source_unique_donnees_x_x.xlsx introuvable dans le classpath");
        }

        Workbook workbook = null;
        workbook = new XSSFWorkbook(fis);
        //Get the nth sheet from the workbook. Sheet sheet = workbook.getSheetAt(i);
        Sheet sheet = workbook.getSheetAt(0); // "BusinessRules");
        // System.out.println("sheetName : " + sheet.getSheetName());

        // get first and last row.
        int rowStart = Math.min(1, sheet.getFirstRowNum());
        int rowEnd = Math.max(4000, sheet.getLastRowNum());

        //System.out.println("rowstart : " + rowStart + " rowend : " + rowEnd);

        // rowStart to start at 2, to ignore headers
        rowStart = rowStart + 2;

        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            //System.out.println("current row : " + rowNum);
            //Get the row object
            Row row = sheet.getRow(rowNum - 1);
            // if row empty
            if (row == null) {
                // This whole row is empty. It can be Handled here as needed
                break;
            }

            CatalogRef catalogRef = new CatalogRef();
            String cellValue = "";

            //System.out.println("Extract row : " + rowNum);
            DataFormatter hdf = new DataFormatter();

            Cell rowCatProduit = row.getCell(Tools.property2ExcelColNumber("RegleMetier.categorieProduit"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (! Tools.isCellEmpty(rowCatProduit)) {
                String catProduit = hdf.formatCellValue(rowCatProduit).trim();
                catalogRef.setCategorieProduit(catProduit);
            } else { catalogRef.setCategorieProduit(""); }

            Cell rowRef = row.getCell(Tools.property2ExcelColNumber("RegleMetier.referenceConstructeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (Tools.isCellEmpty(rowRef)) {
                break;
            }
            catalogRef.setRefConstructeur(hdf.formatCellValue(rowRef).trim());

            Cell rowRefDistributeur = row.getCell(Tools.property2ExcelColNumber("RegleMetier.referenceDistributeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            catalogRef.setRefDistributeur (Tools.getCellValueAsString(rowRefDistributeur));

            Cell rowRefUGAP = row.getCell(Tools.property2ExcelColNumber("RegleMetier.referenceUGAP"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            catalogRef.setRefUGAP(Tools.getCellValueAsString(rowRefUGAP));

            Cell rowDesignation = row.getCell(Tools.property2ExcelColNumber("RegleMetier.designation"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            catalogRef.setDesignation(Tools.getCellValueAsString(rowDesignation));

            Cell rowPrixPublicReponse = row.getCell(Tools.property2ExcelColNumber("RegleMetier.prixPublicReponse"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            catalogRef.setPrixPublicReponse(Tools.getCellValueAsNumeric(rowPrixPublicReponse));

            // System.out.println("colNumber : " + Tools.property2ExcelColNumber("RegleMetier.prixAchat"));
            Cell rowPrixAchatHT = row.getCell(Tools.property2ExcelColNumber("RegleMetier.prixAchat"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            // System.out.println("Prix achat : " + Tools.getCellValueAsNumeric(rowPrixAchatHT));
            catalogRef.setPrixAchatHT(Tools.getCellValueAsNumeric(rowPrixAchatHT));

            Cell rowPrixNetUGAP = row.getCell(Tools.property2ExcelColNumber("RegleMetier.prixNetUGAP"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            catalogRef.setPrixNetUGAP(Tools.getCellValueAsNumeric(rowPrixNetUGAP));

            Cell rowPromotion = row.getCell(Tools.property2ExcelColNumber("RegleMetier.promotion"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Boolean isPromo= false;
            if ( Tools.getCellValueAsString(rowPromotion).equals("PROMO")) {
                System.out.println("PROMOTION !");
                System.out.println(catalogRef.toString());
                isPromo = true;
            }
            catalogRef.setPromotion(isPromo);

            // add to the maps - on gère les promotions
            // if (this.catalogParRefConstructeur.containsKey(catalogRef.getRefConstructeur()))


            if (!isPromo) {
                this.catalogParRefConstructeur.put(catalogRef.getRefConstructeur(), catalogRef);
                this.catalogParRefUGAP.put(catalogRef.getRefUGAP(), catalogRef);
                this.catalogParRefConstructeurConfig.put(catalogRef.getRefConstructeur() + catalogRef.getCategorieProduit(), catalogRef);
            } else {
                this.catalogParRefConstructeur.put(catalogRef.getRefConstructeur() + "PROMO", catalogRef);
                this.catalogParRefUGAP.put(catalogRef.getRefUGAP(), catalogRef);
                this.catalogParRefConstructeurConfig.put(catalogRef.getRefConstructeur() + catalogRef.getCategorieProduit() + "PROMO", catalogRef);
                System.out.println("hashmap key = " + catalogRef.getRefConstructeur() + "PROMO");
            }

        } //end of rows iterator

        workbook.close();
        fis.close();

    }

}