package DevisOracle.shared;

import DevisOracle.configuration.readProperties;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tools {

    public static String getFolder(String filename) {
        File file = new File(filename);
        String myPath = file.getParent().toString();
        return myPath;
    }


    public static String getCurrencyAsString(Double number) {
        // Format currency for Canada locale in Canada locale,
        // the decimal point symbol is a comma and currency
        // symbol is $.
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        String currency = format.format(number);
        System.out.println("Currency in Canada : " + currency);
        return currency;
    }


    public static Integer property2ExcelColNumber(String col) {

        String colName = readProperties.getInstance().getProperty(col);
        Integer columnNumber = CellReference.convertColStringToIndex(colName);
        //  System.out.println("	 Col : " + colName + " is at indice : " + columnNumber);
        return columnNumber;

    }

    public static String readProperty(String prop) {

        String colName = readProperties.getInstance().getProperty(prop);
        return colName;

    }

    public static Integer convertExcelColumName2Number(String col) {
        Integer columnNumber = CellReference.convertColStringToIndex(col);
        return columnNumber;
    }

    public static List<KeyValue> getListWorkbookSheets(Workbook wkb) {
        List<KeyValue> sheets = new ArrayList<>();
        int numberOfSheets = wkb.getNumberOfSheets();
        for(int i=0; i < numberOfSheets; i++){
            String sheetName = wkb.getSheetName(i);
            KeyValue kv = new KeyValue(i, sheetName);
            sheets.add(kv);
        }
        return sheets;
    }

    public static Integer getNumberSheets(Workbook wkb) {
        int numberOfSheets = wkb.getNumberOfSheets();
        return numberOfSheets;
    }

    public static boolean isCellEmpty(Cell myCell) {

        boolean response = false;

        switch (myCell.getCellType()) {
            case _NONE:
                response = true;
                break;
            case BLANK:
                response = true;
                break;
            case BOOLEAN:
                //cellValue = row.getCell(0).getBooleanCellValue();
                response = false;
                break;
            case ERROR:
                response = false;
                //cellValue = row.getCell(0).getErrorCellValue();
                break;
            case FORMULA:
                //cellValue = row.getCell(0).getCellFormula();
                response = false;
                break;
            case NUMERIC:
                response = false;
                break;
            case STRING:
                response = false;
                //System.out.println("	 cell value : " + cellValue);
                break;
        }
        return response;

    } // end getStringFromCell function



    public static String getCellValueAsString(Cell myCell) {

        // Pour les gestion des formules
        Workbook wb = myCell.getSheet().getWorkbook();
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        // pour le foncitonnement de la fonction
        DataFormatter formatter = new DataFormatter();
        SimpleDateFormat inputDateFormater = null;
        SimpleDateFormat outputDateFormater = null;
        String cellValue = "";

        switch (myCell.getCellType()) {
            case _NONE:
                // cellValue="";
                return null;
            case BLANK:
                return "";
            case BOOLEAN:
                //cellValue = row.getCell(0).getBooleanCellValue();
                cellValue = formatter.formatCellValue(myCell).trim();
                break;
            case ERROR:
                // System.out.println("	ERROR !! Cell value is error");
                return "ERROR";
            //cellValue = row.getCell(0).getErrorCellValue();
            case FORMULA:

                switch(myCell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        Double tmp = myCell.getNumericCellValue();
                        Integer inte = tmp.intValue();
                        // String formatted = String.format("%s",tmp);

                        cellValue = inte.toString(); // tmp.toString();
                        break;
                    case STRING:
                        // System.out.println("Last evaluated as \"" + myCell.getRichStringCellValue() + "\"");
                        cellValue = myCell.getStringCellValue();
                        break;
                }
                 /*
                try {
                    CellValue myCellCachedValue = evaluator.evaluate(myCell);
                    switch (myCellCachedValue.getCellType()) {
                        case BOOLEAN:
                            // System.out.println(myCellCachedValue.getBooleanValue());
                            break;
                        case NUMERIC:
                            //System.out.println(myCellCachedValue.getNumberValue());
                            Double tmp = myCellCachedValue.getNumberValue();
                            cellValue = tmp.toString();
                            break;
                        case STRING:
                            //System.out.println(myCellCachedValue.getStringValue());
                            cellValue = myCellCachedValue.getStringValue();
                            break;
                        case BLANK:
                            break;
                        case ERROR:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                  */


                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(myCell)) {
                    Date myDate = myCell.getDateCellValue();
                    inputDateFormater = new SimpleDateFormat("dd/MM/yyyy");
                    cellValue =  inputDateFormater.format(myDate);
                } else {
                    cellValue = formatter.formatCellValue(myCell).trim();
                }
                break;
            case STRING:
                cellValue = myCell.getStringCellValue().trim();
                //System.out.println("	 cell value : " + cellValue);
                break;
        }
        return cellValue;
    } // end getStringFromCell function


    public static Double getCellValueAsNumeric(Cell myCell) {

        // Pour les gestion des formules
        Workbook wb = myCell.getSheet().getWorkbook();
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        Double cellValue = Double.valueOf(0);
        DataFormatter formatter = new DataFormatter();

        switch (myCell.getCellType()) {
            case _NONE:
                // cellValue="";
                return cellValue;
            case BLANK:
                return cellValue;
            case BOOLEAN:
                //cellValue = row.getCell(0).getBooleanCellValue();
                // cellValue = formatter.formatCellValue(myCell).trim();
                return cellValue;
            case ERROR:
                return cellValue;
            //cellValue = row.getCell(0).getErrorCellValue();
            case FORMULA:


                switch(myCell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        cellValue = myCell.getNumericCellValue();
                        break;
                    case STRING:
                        // System.out.println("Last evaluated as \"" + myCell.getRichStringCellValue() + "\"");
                        String tmp = myCell.getStringCellValue();
                        try {
                            cellValue = Double.valueOf(myCell.getStringCellValue().trim());
                        } catch (Exception e) {
                            cellValue = Double.valueOf(0);
                            e.printStackTrace();
                        }
                        break;
                }
                 /*
                try {
                    CellValue myCellCachedValue = evaluator.evaluate(myCell);
                    switch (myCellCachedValue.getCellType()) {
                        case BOOLEAN:
                            // System.out.println(myCellCachedValue.getBooleanValue());
                            break;
                        case NUMERIC:
                            //System.out.println(myCellCachedValue.getNumberValue());
                            cellValue = myCellCachedValue.getNumberValue();
                            break;
                        case STRING:
                            //System.out.println(myCellCachedValue.getStringValue());
                            String tmp = myCellCachedValue.getStringValue();
                            try {
                                cellValue = Double.valueOf(myCell.getStringCellValue().trim());
                            } catch (Exception e) {
                                cellValue = Double.valueOf(0);
                                e.printStackTrace();
                            }
                            break;
                        case BLANK:
                            break;
                        case ERROR:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                  */

                return cellValue;
            case NUMERIC:
                // cellValue = Double.toString(myCell.getNumericCellValue());
                cellValue = myCell.getNumericCellValue();
                break;
            case STRING:
                try {
                    cellValue = Double.valueOf(myCell.getStringCellValue().trim());
                } catch (Exception e) {
                    cellValue = Double.valueOf(0);
                }
                return cellValue;
        }
        return cellValue;
    } // end getStringFromCell function

    public static Workbook readWorkbook(String fileName) {
        Workbook wkb = null;
        FileInputStream fis;
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
        return wkb;
    }

    public static void closeWorkbook(Workbook wkb) {
        try {
            wkb.close();
            System.out.println("Workbook closed !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
