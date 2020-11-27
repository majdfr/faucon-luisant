package DevisOracle.DevisDigora;

import DevisOracle.DevisArrow.ExcelQuoteRow;
import DevisOracle.catalogue.CatalogRef;
import DevisOracle.catalogue.readCatalog;
import DevisOracle.shared.QuoteStructure;
import DevisOracle.shared.Tools;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;


public class readDevisDigora {
    static final Logger log = LogManager.getLogger(readDevisDigora.class.getName());

    public static QuoteStructure readFile(String filename, Integer sheetIndex) {
        // List<ExcelQuoteRow> listRows = new ArrayList<>();
        QuoteStructure qs;
        Workbook wkb = Tools.readWorkbook(filename);
        Sheet sheet = wkb.getSheetAt(sheetIndex);
        qs = readDevisDigora(sheet);

        String folder = Tools.getFolder(filename);

        String refSX = qs.getRefSX();
        if (refSX == null) { refSX = String.valueOf(System.currentTimeMillis()); }
        String nomClient = qs.getClient();
        if (nomClient == null) { nomClient = "CLIENT"; }

        /*
        String destFilename = folder.concat("//INTERNE_devis_").concat(nomClient).concat("_").concat(refSX).concat("_").concat(sheet.getSheetName()).concat(".xlsx");
        exportQuote(qs, destFilename);

        try {
            wkb.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("SupportRenewMgmt - readSupportRenewFile - error ", e);
        }
         */

        return qs;
    }

    public static void exportQuote(String filename, Integer sheetIndex) {
        // List<ExcelQuoteRow> listRows = new ArrayList<>();
        QuoteStructure qs;
        Workbook wkb = Tools.readWorkbook(filename);
        Sheet sheet = wkb.getSheetAt(sheetIndex);
        qs = readDevisDigora(sheet);

        String folder = Tools.getFolder(filename);

        String refSX = qs.getRefSX();
        if (refSX == null) { refSX = String.valueOf(System.currentTimeMillis()); }
        String nomClient = qs.getClient();
        if (nomClient == null) { nomClient = "CLIENT"; }

        String destFilename = folder.concat("//INTERNE_devis_").concat(nomClient).concat("_").concat(refSX).concat("_").concat(sheet.getSheetName()).concat(".xlsx");
        exportQuote(qs, destFilename);

        try {
            wkb.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Digora - exportQuote - error ", e);
        }

    }



    private static void exportQuote (QuoteStructure qs, String destFilename) {

        String fichierModele = Tools.readProperty("config.modeleDevis");
        Workbook workbook = Tools.readWorkbook(fichierModele) ; // new HSSFWorkbook() for generating `.xls` file
        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();


        Sheet sheet = workbook.getSheetAt(0);
        log.debug("Quote size : "  + qs.getQuote().size());
        sheet.shiftRows(16,sheet.getLastRowNum(), qs.getQuote().size());

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
        CellStyle currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat((short)7);
        DataFormat format = workbook.createDataFormat();
        dateCellStyle.setDataFormat(format.getFormat("€#,##0;€#,##0")); //8 = "($#,##0.00_);[Red]($#,##0.00)"
        CellStyle percentageCellStyle = workbook.createCellStyle();
        percentageCellStyle.setDataFormat(format.getFormat("0.00%"));


        // Create Other rows and cells with data
        String premiereLigne = Tools.readProperty("DevisComputacenter.premiereLigne");
        int rowNum = Integer.parseInt(premiereLigne) - 1;
        Double montantPrixAchat = Double.valueOf(0);
        for(ExcelQuoteRow sr: qs.getQuote()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(1).setCellValue(sr.getRefUGAP() );
            row.createCell(2).setCellValue(sr.getRef() );
            row.createCell(3).setCellValue(sr.getDesignation() );
            row.createCell(4).setCellValue(sr.getQuantite());

            Cell cellPU = row.createCell(5);
            cellPU.setCellValue(sr.getPrixUnitaire() );
            cellPU.setCellStyle(currencyCellStyle);

            Cell cellPV = row.createCell(6);
            cellPV.setCellValue(sr.getPrixVenteHT() );
            cellPV.setCellStyle(currencyCellStyle);

            montantPrixAchat = montantPrixAchat + sr.getPrixAchatHT();

        }

        // projet
        Row rwPrj = sheet.getRow(11);
        Cell cellPrj = rwPrj.getCell(Tools.convertExcelColumName2Number("C"));
        cellPrj.setCellValue(qs.getProjet());

        // Date réalisation
        // on vide G12
        Cell cellDateReaOld = rwPrj.getCell(Tools.convertExcelColumName2Number("G"));
        cellDateReaOld.setCellValue("" );
        // on insère G11
        Cell cellDateRea = rwPrj.getCell(Tools.convertExcelColumName2Number("F"));
        String txtDateRea = "Réalisation : ".concat(qs.getDateRealisation());
        cellDateRea.setCellValue(txtDateRea);


        // sum des montants
        Integer totalRowsSum = qs.getQuote().size() + 16 ;
        String formula= "SUM(G15:G" + totalRowsSum + ")";
        Integer indexRowSumHT = qs.getQuote().size()+16;// ligne 16, et -1 comme le nombre de ligne commence par 0
        Row rw = sheet.getRow(indexRowSumHT);
        if (rw != null ) {
            Cell cell = rw.getCell(Tools.convertExcelColumName2Number("G"));
            // System.out.println("Column index : " + Tools.convertExcelColumName2Number("G"));
            if (cell != null ) {
                cell.setCellFormula(formula);
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
            log.error("DevisDigora - exportSingleQuote - error ", e);
        }

    }


    private static QuoteStructure readDevisDigora(Sheet sheet) {
        int rowStart = Math.max(0, sheet.getFirstRowNum());
        int rowEnd = Math.max(100, sheet.getLastRowNum());
        List<ExcelQuoteRow> listRows = new ArrayList<>();
        QuoteStructure qs = new QuoteStructure();

        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            Row row;
            try {
                row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;                 // if row empty, continue;
                }
            } catch (Exception e) {
                continue;
            }

            try {
                DataFormatter hdf = new DataFormatter();

                Cell cellRef = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientCle"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (Tools.isCellEmpty(cellRef)) {
                    continue;
                }
                Cell cellRefUgap = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (Tools.isCellEmpty(cellRef)) {
                    continue;
                }
                String refValue=Tools.getCellValueAsString(cellRef);

                Cell cellInfo;
                String value;
                // System.out.println("Ref substr : " + refValue.substring(0,6));
                switch(refValue.substring(0,6)) {
                    case "Date :":
                        cellInfo = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value= Tools.getCellValueAsString(cellInfo);
                        qs.setDateFacture(value);
                        break;
                    case "Client":
                        cellInfo = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = Tools.getCellValueAsString(cellInfo);
                        qs.setClient(value);
                        break;
                    case "Ref SX":
                        cellInfo = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = Tools.getCellValueAsString(cellInfo);
                        qs.setRefSX(value);
                        break;
                    case "Projet":
                        cellInfo = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = Tools.getCellValueAsString(cellInfo);
                        qs.setProjet(value);
                        break;
                    case "Date d":
                        cellInfo = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = Tools.getCellValueAsString(cellInfo);
                        qs.setDateRealisation(value);
                        break;
                    case "Mode f":
                        cellInfo = row.getCell(Tools.property2ExcelColNumber("DevisDigora.infosClientValeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        value = Tools.getCellValueAsString(cellInfo);
                        qs.setModeFacturation(value);
                        break;
                    case "Ref Co":
                        // rien
                        break;
                    default:
                        ExcelQuoteRow rw = getLigneDevisDigora(row);
                        if (rw != null) {
                            listRows.add(rw);
                            System.out.println("Added row : " + rw.toString());
                        }
                }

            } catch( NullPointerException NPE)	{
                System.out.println("	NULL cell");
                NPE.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("SupportRenewMgmt - readSupport - error ", e);
            }

        } // end for
        qs.setQuote(listRows);
        return qs;
    }



    private static ExcelQuoteRow getLigneDevisDigora( Row poiROW) {

        Boolean isKeyAvailable;
        Double prixNetUGAP = Double.valueOf(0);
        try {

            Cell cellRef = poiROW.getCell(Tools.property2ExcelColNumber("DevisDigora.refConstructeur"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell cellRefUGAP = poiROW.getCell(Tools.property2ExcelColNumber("DevisDigora.refUGAP"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell cellLibelle = poiROW.getCell(Tools.property2ExcelColNumber("DevisDigora.libelleComplet"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell cellQte = poiROW.getCell(Tools.property2ExcelColNumber("DevisDigora.quantite"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell cellPU = poiROW.getCell(Tools.property2ExcelColNumber("DevisDigora.prixUnitaire"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell cellPrixTotal = poiROW.getCell(Tools.property2ExcelColNumber("DevisDigora.prixTotal"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            if (Tools.isCellEmpty(cellRef)
                    || Tools.isCellEmpty(cellRefUGAP)
                    || Tools.isCellEmpty(cellLibelle)
                    || Tools.isCellEmpty(cellQte)
                    || Tools.isCellEmpty(cellPU)
                    || Tools.isCellEmpty(cellPrixTotal) ) {
                // System.out.println("Row not valid : " + poiROW.toString());
                return null;
            }

            String refUGAP=Tools.getCellValueAsString(cellRefUGAP);
            isKeyAvailable = readCatalog.getInstance().getCatalogParRefUGAP().containsKey(refUGAP);
            ExcelQuoteRow singleCCRowQuoteAdd;
            if (isKeyAvailable) {
                CatalogRef catref = readCatalog.getInstance().getCatalogParRefUGAP().get(refUGAP);
                prixNetUGAP = catref.getPrixNetUGAP();

                // System.out.println("Row is valid : " + poiROW.toString());
                Integer qte = (int)Math.round(Tools.getCellValueAsNumeric(cellQte));
                singleCCRowQuoteAdd = new ExcelQuoteRow(
                        0,
                        Tools.getCellValueAsString(cellRef),
                        Tools.getCellValueAsString(cellRefUGAP),
                        "",
                        Tools.getCellValueAsString(cellLibelle),
                        qte,
                        Double.valueOf(0),
                        Double.valueOf(0),
                        prixNetUGAP,
                        Tools.getCellValueAsNumeric(cellPrixTotal),
                        prixNetUGAP * qte,
                        false,
                        false,
                        null
                );
            } else {

                Integer qte = (int)Math.round(Tools.getCellValueAsNumeric(cellQte));
                singleCCRowQuoteAdd = new ExcelQuoteRow(
                        0,
                        Tools.getCellValueAsString(cellRef),
                        Tools.getCellValueAsString(cellRefUGAP),
                        "",
                        Tools.getCellValueAsString(cellLibelle),
                        qte,
                        Double.valueOf(0),
                        Double.valueOf(0),
                        Tools.getCellValueAsNumeric(cellPU),
                        Double.valueOf(0),
                        Tools.getCellValueAsNumeric(cellPrixTotal),
                        false,
                        false,
                        null
                );
            }

            return singleCCRowQuoteAdd;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    } // end function

    public static List<DigoraAnalyse> AnalyserDevisDigora(QuoteStructure qs) {
        List<DigoraAnalyse> analyse = new ArrayList<>();
        if (qs == null) {
            return null;
        }
        List<ExcelQuoteRow> quote = qs.getQuote();

        // On calcule le prix d'achat
        Double totalHTPrixAchat = Double.valueOf(0);
        Double totalHTPrixVente = Double.valueOf(0);
        for (ExcelQuoteRow item : quote) {
            if (item != null ) {
                totalHTPrixAchat = totalHTPrixAchat + item.getPrixAchatHT();
                totalHTPrixVente = totalHTPrixVente + item.getPrixVenteHT();
            }
        }
        DigoraAnalyse daPA = new DigoraAnalyse("Prix Achat", Tools.getCurrencyAsString(totalHTPrixAchat), false);
        analyse.add(daPA);
        DigoraAnalyse daPV = new DigoraAnalyse("Prix de Vente", Tools.getCurrencyAsString(totalHTPrixVente), false);
        analyse.add(daPV);
        Double Marge = 100*(totalHTPrixVente - totalHTPrixAchat)/totalHTPrixAchat;
        Double MontantMarge = totalHTPrixVente - totalHTPrixAchat;

        DigoraAnalyse daMarge;
        if (Marge < 12) {
            daMarge = new DigoraAnalyse("Marge dégagée", Marge.toString().concat("%"), true);
        } else {
            daMarge = new DigoraAnalyse("Marge dégagée", Marge.toString().concat("%"), false);
        }
        analyse.add(daMarge);

        DigoraAnalyse daMontantMarge = new DigoraAnalyse("Montant Marge dégagée", Tools.getCurrencyAsString(MontantMarge), false);
        analyse.add(daMontantMarge);
        DigoraAnalyse daClient = new DigoraAnalyse("Client", qs.getClient(), false);
        analyse.add(daClient);
        DigoraAnalyse daDate = new DigoraAnalyse("Date de réalisation", qs.getDateRealisation(), false);
        analyse.add(daDate);
        DigoraAnalyse daPrj = new DigoraAnalyse("Description Projet", qs.getProjet(), false);
        analyse.add(daPrj);

        return analyse;
    }

}
