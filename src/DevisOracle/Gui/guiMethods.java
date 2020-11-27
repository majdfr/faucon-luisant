package DevisOracle.Gui;

import DevisOracle.DevisArrow.ExcelQuoteRow;
import DevisOracle.shared.Tools;
import DevisOracle.DevisArrow.exportQuote;

import java.io.File;
import java.util.List;

public class guiMethods {

    // On exporte la feuille en cours. Si aucuune selectionnée, on prend la première. sinon, on prend celle selectionn&ée.
    // public void exportDestDevis(ActionEvent event) {
    public static void exportDevisClient(List<ExcelQuoteRow> quote, String filename, String designationDevis, Boolean isInterne, Double montantPrixAchat) {

        // call getFileName() and get FileName path object
        // File file = new File(txtFileToConvert.getText());
        File file = new File(filename);
        String myPath = file.getParent().toString();
        String myFile = file.getName();
        String extension = Tools.readProperty("devisComputacenter.extensionDevis");
        String destFile;
        if (!isInterne) {
            destFile = myPath.concat("//").concat(extension).concat(myFile); //.concat("x");
        } else {
            destFile = myPath.concat("//").concat("INTERNE_").concat(extension).concat(myFile); //.concat("x");
        }
        System.out.println("DestFile pour export manuel : " + destFile);
        System.out.println("Quote pour export manuel : " + quote.toString());

        // exportQuote.exportSingleQuote( tableCCQuote.getItems(), txtDesignationDevis.getText(),  destFile, isInterne );
        exportQuote.exportSingleQuote( quote, designationDevis,  destFile, isInterne, montantPrixAchat );

    }

    public static Double getMontantPrixAchat(List<ExcelQuoteRow> lstQuoteRow) {

        Double montantPrixAchat = Double.valueOf(0);
        for (ExcelQuoteRow row : lstQuoteRow) {
            montantPrixAchat = montantPrixAchat + ( row.getPrixUnitaire() * row.getQuantite());
        }
        System.out.println("montantPrixAchat =  " + montantPrixAchat);
        return montantPrixAchat;
    }

}
