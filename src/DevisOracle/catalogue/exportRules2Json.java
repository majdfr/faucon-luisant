package DevisOracle.catalogue;

import DevisOracle.shared.KeyValue;
import DevisOracle.shared.Tools;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class exportRules2Json {

    public Workbook workbook = null;
    private FileInputStream fis = null;
    private String designationDevis;

    public exportRules2Json() {
    }

    public Workbook getWorkbook() {
        return workbook;
    }
    private void setWorkbook(Workbook wkb) {
        workbook = wkb;
    }

    public Integer getNumberSheets() {
        int numberOfSheets = getWorkbook().getNumberOfSheets();
        return numberOfSheets;
    }

    public List<KeyValue> getListWorkbookSheets() {
        List<KeyValue> sheets = new ArrayList<>();
        int numberOfSheets = getWorkbook().getNumberOfSheets();
        for(int i=0; i < numberOfSheets; i++){
            String sheetName = getWorkbook().getSheetName(i);
            KeyValue kv = new KeyValue(i, sheetName);
            sheets.add(kv);
        }
        return sheets;
    }

    private void readWorkbook(String fileName) {
        Workbook wkb = null;
        try {
            //Create the input stream from the xlsx/xls file
            fis = new FileInputStream(fileName);
            //Create Workbook instance for xlsx/xls file input stream
            if (fileName.toLowerCase().endsWith("xlsx")) {
                wkb = new XSSFWorkbook(fis);
            } else if (fileName.toLowerCase().endsWith("xls")) {
                wkb = new HSSFWorkbook(fis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setWorkbook(wkb);
    }

    private void closeWorkbook() throws IOException {
        workbook.close();
        fis.close();
    }

    public void exportRules(String filename, Integer sheetIndex)  {

        readWorkbook(filename);

        Sheet sheet = getWorkbook().getSheet("REGLES");
        // get first and last row.
        int rowStart = Math.min(0, sheet.getFirstRowNum());
        int rowEnd = Math.max(100, sheet.getLastRowNum());
        System.out.println("rowstart : " + rowStart);
        JSONObject obj = new JSONObject();
        JSONArray jsonRegles = new JSONArray();
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            //Get the row object
            Row row;
            try {
                row = sheet.getRow(rowNum);
                // System.out.println("Row : " + rowNum + row.toString());
            } catch (Exception e) {
                continue;
            }
            // if row empty
            if (row == null) {
                continue;
            }

            try {
                DataFormatter hdf = new DataFormatter();

                // en-tête du tableau dans le devis Arrow
                Cell rowRefUGAP = row.getCell(Tools.convertExcelColumName2Number("A"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (Tools.isCellEmpty(rowRefUGAP) ) {
                    continue;
                }
                String strCellRef = hdf.formatCellValue(rowRefUGAP);
                // en-tête du tableau dans le devis Arrow
                if (strCellRef.equals("RefUGAP")) {
                    continue;
                }
                Cell rowConfiguration = row.getCell(Tools.convertExcelColumName2Number("B"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefConstructeur = row.getCell(Tools.convertExcelColumName2Number("C"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowDesignation = row.getCell(Tools.convertExcelColumName2Number("D"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefAlt1 = row.getCell(Tools.convertExcelColumName2Number("J"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefAlt1Qte = row.getCell(Tools.convertExcelColumName2Number("K"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefAlt2 = row.getCell(Tools.convertExcelColumName2Number("L"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefAlt2Qte = row.getCell(Tools.convertExcelColumName2Number("M"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefAdd = row.getCell(Tools.convertExcelColumName2Number("N"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell rowRefAddQte = row.getCell(Tools.convertExcelColumName2Number("O"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                // générer json ici
                String rowConfigStr = Tools.getCellValueAsString(rowConfiguration);
                String rowRefUGAPStr = Tools.getCellValueAsString(rowRefUGAP);
                String rowRefConstructeurStr = Tools.getCellValueAsString(rowRefConstructeur);
                String rowDesignationStr = Tools.getCellValueAsString(rowDesignation);
                String rowRefAlt1Str = Tools.getCellValueAsString(rowRefAlt1);
                Integer rowRefAlt1QteInt = Tools.getCellValueAsNumeric (rowRefAlt1Qte).intValue();
                String rowRefAlt2Str = Tools.getCellValueAsString(rowRefAlt2);
                Integer rowRefAlt2QteInt = Tools.getCellValueAsNumeric (rowRefAlt2Qte).intValue();
                String rowRefAddStr = Tools.getCellValueAsString(rowRefAdd);
                Integer rowRefAddQteInt = Tools.getCellValueAsNumeric (rowRefAddQte).intValue();


                JSONObject jRegle = new JSONObject();

                jRegle.put("ref", rowRefConstructeurStr);
                jRegle.put("configuration", rowConfigStr);
                jRegle.put("designation", rowDesignationStr);

                JSONArray jRemplacerPar = new JSONArray();
                JSONArray jAjouter = new JSONArray();
                // on créé les refs alternatives
                if (! rowRefAlt1Str.equals("")) {
                    JSONObject jRegle1 = new JSONObject();
                    jRegle1.put("ref", rowRefAlt1Str);
                    jRegle1.put("qte", rowRefAlt1QteInt);
                    JSONObject jRegle2 = new JSONObject();
                    jRemplacerPar.add(jRegle1);
                    if (! rowRefAlt2Str.equals("")) {
                        jRegle2.put("ref", rowRefAlt2Str);
                        jRegle2.put("qte", rowRefAlt2QteInt);
                        jRemplacerPar.add(jRegle2);
                    }
                    // on créé l'objet json dans les regles
                    jRegle.put("RemplacerPar", jRemplacerPar);
                }
                if (! rowRefAddStr.equals("")) {
                    // on créé les refs A Ajouter
                    JSONObject jAdd = new JSONObject();
                    jAdd.put("ref", rowRefAddStr);
                    jAdd.put("qte", rowRefAddQteInt);
                    // on les rajoute au tableau
                    jAjouter.add(jAdd);
                    // on créé l'objet json dans les regles
                    jRegle.put("Ajouter", jAjouter);
                }

                jsonRegles.add(jRegle);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } //end of rows iterator


        obj.put("REGLES", jsonRegles);
        System.out.print(obj);

        try (FileWriter file = new FileWriter("G:\\Mon Drive\\Boulot\\Computacenter France\\Projets Clients LOB\\UGAP\\AO UGAP Oracle Full Eté 2019\\15-catalogue\\test.json")) {
            closeWorkbook();
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // } //end of sheets for loop

        //close file input stream
        //fis.close();
    }

}


