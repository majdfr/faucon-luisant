package DevisOracle.DevisArrow;

import DevisOracle.shared.Tools;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class exportQuote {

    // private static String[] columns = {"Référence UGAP", "Référence Constructeur", "Désignation", "Quantité", "Prix Unitaire", "Prix"};
    static final Logger log = LogManager.getLogger(exportQuote.class.getName());

    public static void exportSingleQuote (List<ExcelQuoteRow> quote, String designation, String fileDest, Boolean interneCCCFR, Double montantPrixAchat) {
        // Create a Workbook
        // Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        String fichierModele = Tools.readProperty("config.modeleDevis");
        Workbook workbook = Tools.readWorkbook(fichierModele) ; // new HSSFWorkbook() for generating `.xls` file

        System.out.println("exportSingleQuote fichierModele :  " + fichierModele);
        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        // Sheet sheet = workbook.createSheet(sheetName);
        Sheet sheet = workbook.getSheetAt(0);
        //System.out.println("Quote size : "  + quote.size());
        sheet.shiftRows(16,sheet.getLastRowNum(), quote.size());

        /*
        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

         */

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Cell Style for formatting Currency
        CellStyle currencyCellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
       //  dateCellStyle.setDataFormat((short)7); //8 = "($#,##0.00_);[Red]($#,##0.00)"
        // currencyCellStyle.setDataFormat(format.getFormat("#,##0€;#€,##0")); //8 = "($#,##0.00_);[Red]($#,##0.00)"
        currencyCellStyle.setDataFormat(format.getFormat("#,##0.00 €")); //8 = "($#,##0.00_);[Red]($#,##0.00)"


        // Create Cell Style for formatting percentage
        CellStyle percentageCellStyle = workbook.createCellStyle();
        percentageCellStyle.setDataFormat(format.getFormat("0.00%"));


        // Create Other rows and cells with employees data
        String premiereLigne = Tools.readProperty("DevisComputacenter.premiereLigne");
        int rowNum = Integer.parseInt(premiereLigne) - 1;


        for(ExcelQuoteRow quoteRow: quote) {
            Row row = sheet.createRow(rowNum++);
            if (interneCCCFR) {
                row.createCell(0).setCellValue(quoteRow.getRefOrigine() );
            }

            row.createCell(1).setCellValue(quoteRow.getRefUGAP() );
            row.createCell(2).setCellValue(quoteRow.getRef() );
            row.createCell(3).setCellValue(quoteRow.getDesignation()) ;
            row.createCell(4).setCellValue(quoteRow.getQuantite());

            Cell cellPU = row.createCell(5);
            cellPU.setCellValue(quoteRow.getPrixUnitaire() );
            cellPU.setCellStyle(currencyCellStyle);

            Cell cellPV = row.createCell(6);
            cellPV.setCellValue(quoteRow.getPrixVenteHT() );
            cellPV.setCellStyle(currencyCellStyle);


            /*
            Cell cellPVAchat = row.createCell(8);
            cellPVAchat.setCellValue(quoteRow.getPrixAchatHT() );
            cellPVAchat.setCellStyle(currencyCellStyle);

            Cell cellPVMarge = row.createCell(9);
            Double pvMarge = (quoteRow.getPrixVenteHT() - quoteRow.getPrixAchatHT()) / quoteRow.getPrixAchatHT();
            cellPVMarge.setCellValue(pvMarge );
            cellPVMarge.setCellStyle(percentageCellStyle);

             */

        }

        // Resize all columns to fit the content size
        /*
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
         */

        // sum des montants
        Integer totalRowsSum = quote.size() + 16 ;
        String formula= "SUM(G15:G" + totalRowsSum + ")";
        Integer indexRowSumHT = quote.size()+16;// ligne 16, et -1 comme le nombre de ligne commence par 0
        Row rw = sheet.getRow(indexRowSumHT);
        if (rw != null ) {
            Cell cellTotalHT = rw.getCell(Tools.convertExcelColumName2Number("G"));
            if (cellTotalHT != null ) {
                cellTotalHT.setCellFormula(formula);
            }

            Cell cellPrixVente = rw.getCell(Tools.convertExcelColumName2Number("A"));
            if (cellPrixVente != null ) {
                cellPrixVente.setCellValue(montantPrixAchat);
            } else {
                System.out.println("Cell prix achat is null :( " );
            }
        }

        // afficher la marge dégagée
        Integer idxRowTTC = indexRowSumHT + 1;
        Row rw2 = sheet.getRow(idxRowTTC);
        Integer rowTTC = totalRowsSum + 1;
        Cell cellMarge = rw2.getCell(Tools.convertExcelColumName2Number("A"));
        if (cellMarge != null ) {
            cellMarge.setCellStyle(currencyCellStyle);
            String formulaMarge= "(G" + rowTTC + " - A" + rowTTC + ")/A" + rowTTC;
            System.out.println("formulaMarge : " + formulaMarge);
            cellMarge.setCellFormula(formulaMarge);
            cellMarge.setCellStyle(percentageCellStyle);
        } else {
            System.out.println("Cell Marge Dégagée is null :( " );
        }

        // TVA
        Row rwTVA = sheet.getRow(indexRowSumHT+1);
        Cell cellTVA = rwTVA.getCell(Tools.convertExcelColumName2Number("G"));
        Integer indexRowTTC = (indexRowSumHT+1);
        String formulaTVA = "G"+indexRowTTC+"*1.2";
        cellTVA.setCellFormula(formulaTVA);
        // cellTVA.setCellStyle(currencyCellStyle);

        // désignation projet
        /*
        String crDesign = Tools.readProperty("DevisComputacenter.designationProjet");
        CellReference cr = new CellReference("A1");
        Row rwDesign = sheet.getRow(cr.getRow());
        Cell cellDesign = rwDesign.getCell(cr.getCol());
        if (designation != null) {
            cellDesign.setCellValue(designation);
        }


         */
        // évaluer toutes les formules
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);

        // Closing the workbook
        try {
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(fileDest);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("ExportQuote class error", e);
        }
    }



}
