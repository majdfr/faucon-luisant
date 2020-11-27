package DevisOracle.DevisArrow;

import DevisOracle.DevisArrow.ExcelQuoteRow;
import DevisOracle.DevisArrow.readExcelQuoteToTable;
import DevisOracle.shared.QuoteStructure;
import DevisOracle.shared.Tools;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class exportAnalyseDevis {

    public static void export( String inputFilename ) {

        File file = new File( inputFilename);
        String myPath = file.getParent().toString();
        String filename = file.getName();
        String extension = Tools.readProperty("devisComputacenter.extensionAnalyseDevis");
        String outputFilename = myPath.concat("//").concat(extension).concat(filename);
        // System.out.println("    destFile : " + outputFilename );

        createSheetCopy(inputFilename, outputFilename);

        // read input file
        readExcelQuoteToTable readInputXLS = new readExcelQuoteToTable(inputFilename);
        Integer nbInputSheets = readInputXLS.getNumberSheets();
        // System.out.println("List sheets : " + nbInputSheets);
        // read output file
        readExcelQuoteToTable readOutputXLS = new readExcelQuoteToTable(outputFilename);

        QuoteStructure qtOutputMain = null;
        try {
            qtOutputMain = readInputXLS.readSheetIndex( 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sheetName = qtOutputMain.getSheetName();
        HSSFWorkbook outputWKB = (HSSFWorkbook) readOutputXLS.getWorkbook();
        HSSFSheet outputSheet = outputWKB.getSheetAt(0);
        // worksheet.shiftRows(qtOutputMain.getTotalHTIndex()+3,qtOutputMain.getTotalHTIndex()+50, 47);

        //list of sheets
        // List<KeyValue> listWkb = Tools.getListWorkbookSheets(workbookinput);
        List<QuoteStructure> lqts = new ArrayList<>();

        Integer rowIncr=qtOutputMain.getTotalHTIndex() + 10;
        //HSSFSheet outputSheet = (HSSFSheet) readOutputXLS.getWorkbook().getSheetAt(0);

        for (Integer it=0; it<nbInputSheets; it++) {
            QuoteStructure qts;
            try {
                qts = readInputXLS.readSheetIndex(it);
                lqts.add(qts);
                List<ExcelQuoteRow> quote = qts.getQuote();

                if (it == 0) {
                    continue;
                }
                System.out.println("working on sheet : " + qts.getSheetName() + " - index : " + it );

                for (ExcelQuoteRow quoteRow : quote) {
                    System.out.println("working on row : " + quoteRow.toString());
                    Row row = outputSheet.createRow(rowIncr);
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.reference")).setCellValue(quoteRow.getRef());
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.designation")).setCellValue(quoteRow.getDesignation());
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.quantite")).setCellValue(quoteRow.getQuantite());
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.prixPublic")).setCellValue(quoteRow.getPrixPublic() );
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.remise")).setCellValue(quoteRow.getPourcentageRemise() );
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.prixUnitaire")).setCellValue(quoteRow.getPrixUnitaire() );
                    row.createCell(Tools.property2ExcelColNumber("DevisArrow.montantHT")).setCellValue(quoteRow.getPrixAchatHT() );
                    rowIncr++;
                }
                rowIncr = rowIncr + 4;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        try {
            readInputXLS.closeWorkbook();
            //To write your changes to new workbook
            readOutputXLS.saveWorkboox();
            readOutputXLS.closeWorkbook();
            //FileOutputStream out = new FileOutputStream(outputFilename);
            //outputWKB.write(out);
            // out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static HSSFWorkbook readWorkbook(String filename) {
        //input source excel file which contains sheets to be copied
        FileInputStream file = null;
        HSSFWorkbook workbookinput = null;
        Integer nbSheetsInput = 1;
        try {
            file = new FileInputStream(filename);
            workbookinput = new HSSFWorkbook(file);
            // nbSheetsInput = workbookinput.getNumberOfSheets();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return workbookinput;
    }


    private static void createSheetCopy(String inputFilename, String outputFilename) {

        File originalWb = new File(inputFilename);
        File clonedWb = new File(outputFilename);
        try {
            Files.copy(originalWb.toPath(), clonedWb.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read file
        readExcelQuoteToTable readxls = new readExcelQuoteToTable(outputFilename);

        QuoteStructure qtMain = null;
        try {
            qtMain = readxls.readSheetIndex( 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sheetName = qtMain.getSheetName();

        HSSFWorkbook wkb = (HSSFWorkbook)readxls.getWorkbook();

        // on ne garde qu'une seule feuille
        wkb =  deleteSheets (wkb, sheetName);
        HSSFSheet worksheet = wkb.getSheet(sheetName);
        worksheet.shiftRows(qtMain.getTotalHTIndex()+3,qtMain.getTotalHTIndex()+50, 47);

        /*
        //input source excel file which contains sheets to be copied
        FileInputStream file = null;
        HSSFWorkbook workbookinput = null;
        Integer nbSheetsInput = 1;
        try {
            file = new FileInputStream(inputFilename);
            workbookinput = new HSSFWorkbook(file);
            nbSheetsInput = workbookinput.getNumberOfSheets();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }

        // read file
        readExcelQuoteToTable readxls = new readExcelQuoteToTable(inputFilename);

        QuoteStructure qtMain = null;
        try {
            qtMain = readxls.readSheetIndex( 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sheetName = qtMain.getSheetName();
        //output new excel file to which we need to copy the above sheets   //this would copy entire workbook from source
        HSSFWorkbook wkb=workbookinput;
        // on ne garde qu'une seule feuille
        wkb =  deleteSheets (wkb, sheetName);
        HSSFSheet worksheet = wkb.getSheet(sheetName);
        worksheet.shiftRows(qtMain.getTotalHTIndex()+3,qtMain.getTotalHTIndex()+50, 47);
         */

    }


    private static HSSFWorkbook deleteSheets(HSSFWorkbook workbookoutput, String sheetName) {
        //delete one or two unnecessary sheets, you can delete them by specifying the sheetnames
        Integer keepSheet= workbookoutput.getSheetIndex(sheetName);
        Integer nbSheets = Tools.getNumberSheets(workbookoutput);
        System.out.println("Number of sheets : " + nbSheets + " - keep nb : " + keepSheet);
        // c'est pénible, mais parfois, les indexes changent quand on supprime des feuilles
        while (Tools.getNumberSheets(workbookoutput) > 1) {
            keepSheet= workbookoutput.getSheetIndex(sheetName);
            nbSheets = Tools.getNumberSheets(workbookoutput);
            try {
                for (int index = 0; index < nbSheets; index++) {
                    if (index != keepSheet) {
                        System.out.println("Delete sheet : " + index);
                        workbookoutput.removeSheetAt(index);
                    } else {
                        System.out.println("keep sheet : " + index);
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        return workbookoutput;
    }



}
