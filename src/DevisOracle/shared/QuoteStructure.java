package DevisOracle.shared;

import DevisOracle.DevisArrow.ExcelQuoteRow;

import java.util.List;

public class QuoteStructure {
    private Integer headerIndex;
    private String designation;
    private Integer designationIndex;
    private Integer premiereLigneIndex;
    private Integer TotalHTIndex;
    private String sheetName;

    private String DateFacture;
    private String RefSX;
    private String Client;
    private String Projet;
    private String DateRealisation;
    private String ModeFacturation;

    private List<ExcelQuoteRow> quote;

    public String getDateFacture() {
        return DateFacture;
    }

    public void setDateFacture(String dateFacture) {
        DateFacture = dateFacture;
    }

    public String getRefSX() {
        return RefSX;
    }

    public void setRefSX(String refSX) {
        RefSX = refSX;
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public String getProjet() {
        return Projet;
    }

    public void setProjet(String projet) {
        Projet = projet;
    }

    public String getDateRealisation() {
        return DateRealisation;
    }

    public void setDateRealisation(String dateRealisation) {
        DateRealisation = dateRealisation;
    }

    public String getModeFacturation() {
        return ModeFacturation;
    }

    public void setModeFacturation(String modeFacturation) {
        ModeFacturation = modeFacturation;
    }

    public Integer getHeaderIndex() {
        return headerIndex;
    }

    public void setHeaderIndex(Integer headerIndex) {
        this.headerIndex = headerIndex;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getDesignationIndex() {
        return designationIndex;
    }

    public void setDesignationIndex(Integer designationIndex) {
        this.designationIndex = designationIndex;
    }

    public Integer getPremiereLigneIndex() {
        return premiereLigneIndex;
    }

    public void setPremiereLigneIndex(Integer premiereLigneIndex) {
        this.premiereLigneIndex = premiereLigneIndex;
    }

    public Integer getTotalHTIndex() {
        return TotalHTIndex;
    }

    public void setTotalHTIndex(Integer totalHTIndex) {
        TotalHTIndex = totalHTIndex;
    }

    public List<ExcelQuoteRow> getQuote() {
        return quote;
    }

    public void setQuote(List<ExcelQuoteRow> quote) {
        this.quote = quote;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    @Override
    public String toString() {
        return "QuoteStructure{" +
                "headerIndex=" + headerIndex +
                ", designation='" + designation + '\'' +
                ", designationIndex=" + designationIndex +
                ", premiereLigneIndex=" + premiereLigneIndex +
                ", TotalHTIndex=" + TotalHTIndex +
                ", sheetName='" + sheetName + '\'' +
                '}';
    }
}
