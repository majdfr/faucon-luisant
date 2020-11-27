package DevisOracle.supportRenew;

import DevisOracle.shared.KeyValue;
import DevisOracle.shared.Tools;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class supportRenewMgmt {
    static final Logger log = LogManager.getLogger(supportRenewMgmt.class.getName());

    public static void exportAll(List<SupportRenew> lstRenews, String filename) {
        HashMap<String, List<SupportRenew>> mapDevis = getMap(lstRenews);

        String folder = Tools.getFolder(filename);

        for (String key: mapDevis.keySet()) {
            System.out.println("Map Key : " + key);
            List<SupportRenew> src = mapDevis.get(key);
            String client = src.get(0).getClient();
            String destFilename = folder.concat("//devis_").concat(key).concat("_").concat(client).concat(".xlsx");
            exportSingleQuote(key, src, destFilename);
        }
    }

    public static HashMap<String, List<SupportRenew>> getMap(List<SupportRenew> lstRenews) {
        HashMap<String, List<SupportRenew>> mapDevis = new HashMap<String, List<SupportRenew>>();
        Integer iter = 0;
        for (SupportRenew sr : lstRenews) {
            if (sr.getContrat().equals("MIGRATION_SUPPORT")) {
                List<SupportRenew> lst = new ArrayList<>();
                lst.add(sr);
                mapDevis.put("MIGRATION_SUPPORT".concat(iter.toString()), lst);
                iter = iter + 1;
            } else {
                if (mapDevis.containsKey(sr.getContrat())) {
                    mapDevis.get(sr.getContrat()).add(sr);
                } else {
                    List<SupportRenew> lst = new ArrayList<>();
                    lst.add(sr);
                    mapDevis.put(sr.getContrat(), lst);
                }
            }

        }
        System.out.println("Map : " + mapDevis.toString());
        return mapDevis;
    }


    private static void exportSingleQuote(String contrat, List<SupportRenew> lstRenews, String destFilename) {

            String fichierModele = Tools.readProperty("config.modeleDevis");

            Workbook workbook = Tools.readWorkbook(fichierModele) ; // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
            CreationHelper createHelper = workbook.getCreationHelper();

            // Create a Sheet
            // Sheet sheet = workbook.createSheet(sheetName);
            Sheet sheet = workbook.getSheetAt(0);
            log.debug("Quote size : "  + lstRenews.size());
            sheet.shiftRows(16,sheet.getLastRowNum(), lstRenews.size());

            // Create Cell Style for formatting Date
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat((short)7);

            DataFormat format = workbook.createDataFormat();
            //  dateCellStyle.setDataFormat((short)7); //8 = "($#,##0.00_);[Red]($#,##0.00)"
            dateCellStyle.setDataFormat(format.getFormat("€#,##0;€#,##0")); //8 = "($#,##0.00_);[Red]($#,##0.00)"

            // Create Other rows and cells with employees data
            String premiereLigne = Tools.readProperty("DevisComputacenter.premiereLigne");
            int rowNum = Integer.parseInt(premiereLigne) - 1;
            for(SupportRenew sr: lstRenews) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(1).setCellValue(sr.getRefUGAP() );
                row.createCell(2).setCellValue(sr.getCsi() );
                row.createCell(3).setCellValue(sr.getLibelleComplet() );
                row.createCell(4).setCellValue(1);

                Cell cellPU = row.createCell(5);
                cellPU.setCellValue(sr.getPrixAchatUGAP() );
                cellPU.setCellStyle(currencyCellStyle);

                Cell cellPV = row.createCell(6);
                cellPV.setCellValue(sr.getPrixAchatUGAP() );
                cellPV.setCellStyle(currencyCellStyle);

            }

            // sum des montants
            Integer totalRowsSum = lstRenews.size() + 16 ;
            String formula= "SUM(G15:G" + totalRowsSum + ")";
            Integer indexRowSumHT = lstRenews.size()+16;// ligne 16, et -1 comme le nombre de ligne commence par 0
            Row rw = sheet.getRow(indexRowSumHT);
            if (rw != null ) {
                Cell cell = rw.getCell(Tools.convertExcelColumName2Number("G"));
                // System.out.println("Column index : " + Tools.convertExcelColumName2Number("G"));
                if (cell != null ) {
                    cell.setCellFormula(formula);
                } else {
                     System.out.println("cell is null !");
                }

            } else {
                 System.out.println("row is null !");
            }


            // TVA
            Row rwTVA = sheet.getRow(indexRowSumHT+1);
            Cell cellTVA = rwTVA.getCell(Tools.convertExcelColumName2Number("G"));
            Integer indexRowTTC = (indexRowSumHT+1);
            String formulaTVA = "G"+indexRowTTC+"*1.2";
            cellTVA.setCellFormula(formulaTVA);

            // évaluer toutes les formules
            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);


            // Closing the workbook
            try {
                // Write the output to a file
                FileOutputStream fileOut = new FileOutputStream(destFilename);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("SupportRenewMgmt - exportSingleQuote - error ", e);
            }

    }

    public static List<SupportRenew> readSupportRenewFile(String filename, Integer sheetIndex) {
        List<SupportRenew> listSupportRenews = new ArrayList<>();
        Workbook wkb = Tools.readWorkbook(filename);
        Sheet sheet = wkb.getSheetAt(sheetIndex);
        switch(sheet.getSheetName()) {
            case "Logiciels":
                listSupportRenews = readSupport(sheet, Tools.readProperty("SupportRenew.ongletLogiciel"));
                break;
            case "Matériel":
                listSupportRenews = readSupport(sheet, Tools.readProperty("SupportRenew.ongletMateriel"));
                break;
            default:
                System.out.print("Probleme. ne detecte pas le nom de l'onglet : " + sheet.getSheetName());
                // code block
        }
        System.out.print(listSupportRenews);
        try {
            wkb.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("SupportRenewMgmt - readSupportRenewFile - error ", e);
        }

        return listSupportRenews;
    }


    private static List<SupportRenew> readSupport(Sheet sheet, String type) {
        int rowStart = Math.max(0, sheet.getFirstRowNum());
        int rowEnd = Math.max(100, sheet.getLastRowNum());
        List<SupportRenew> listSupportRenews = new ArrayList<>();
        String nomOngletMateriel=Tools.readProperty("SupportRenew.ongletMateriel");
        String nomOngletLogiciel=Tools.readProperty("SupportRenew.ongletLogiciel");


        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            Row row;
            try {
                row = sheet.getRow(rowNum);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            // if row empty
            if (row == null) {
                continue;
            }

            try {
                DataFormatter hdf = new DataFormatter();
                String propStr =  "SupportRenew.".concat(type).concat(".status");
                Cell cellStatus = row.getCell(Tools.property2ExcelColNumber(propStr), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (Tools.isCellEmpty(cellStatus)) {
                    continue;
                }

                String status = Tools.getCellValueAsString(cellStatus);
                if (! status.equals(Tools.readProperty("SupportRenew.motCleAction"))) {
                    continue;
                }

                Cell cellRef = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".refUGAP")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellClient = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".client")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellContrat = null;
                String myContrat="MIGRATION_SUPPORT";
                try {
                    cellContrat = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".contrat")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                } catch (Exception e) {
                    // cellContrat = new Cell();
                    e.printStackTrace();
                }
                if (cellContrat != null) {
                    myContrat = Tools.getCellValueAsString(cellContrat);
                    if (myContrat.equals("")) {
                        myContrat = "MIGRATION_SUPPORT";
                    }
                }

                Cell cellDateDebutValidite = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".dateDebutValidite")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellDateFinValidite = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".dateFinValidite")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellDesignation = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".designation")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String CSI = "";
                String QTE = "";
                if (type.equals(nomOngletLogiciel)) {
                    Cell cellCSI = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".csi")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell cellQuantite = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".quantite")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    CSI = Tools.getCellValueAsString(cellCSI);
                    QTE = Tools.getCellValueAsString(cellQuantite);
                } else
                {
                    CSI = "";
                    QTE = "";
                }
                Cell cellTermeFacturation = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".termeFacturation")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellPrixPublicOracle = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".prixPublicOracle")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellTauxIntermediation = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".tauxIntermediation")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellPrixAchatUGAP = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".prixAchatUGAP")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellLibelleComplet = row.getCell(Tools.property2ExcelColNumber("SupportRenew.".concat(type).concat(".libelleComplet")), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                SupportRenew sr = new SupportRenew(
                        Tools.getCellValueAsString(cellRef),
                        Tools.getCellValueAsString(cellRef),
                        Tools.getCellValueAsString(cellClient),
                        myContrat,
                        Tools.getCellValueAsString(cellDateDebutValidite),
                        Tools.getCellValueAsString(cellDateFinValidite),
                        Tools.getCellValueAsString(cellDesignation),
                        CSI,
                        QTE,
                        Tools.getCellValueAsString(cellTermeFacturation),
                        Tools.getCellValueAsNumeric(cellPrixPublicOracle) ,
                        Tools.getCellValueAsString(cellTauxIntermediation),
                        Tools.getCellValueAsNumeric(cellPrixAchatUGAP),
                        Tools.getCellValueAsString(cellLibelleComplet)
                );

                System.out.println("SR : " + sr.toString());

                listSupportRenews.add(sr);

            } catch( NullPointerException NPE)	{
                System.out.println("	NULL cell");
                NPE.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("SupportRenewMgmt - readSupport - error ", e);
            }

        }
        return listSupportRenews;
    }




    public static List<KeyValue> listSheets(String filename) {
        Workbook wkb = Tools.readWorkbook(filename);
        List<KeyValue> lkv  =  Tools.getListWorkbookSheets(wkb);
        Tools.closeWorkbook(wkb);
        return lkv;
    }



}
