package DevisOracle.supportRenew;

public class SupportRenew {
    private String status;
    private String refUGAP;
    private String client;
    private String contrat;
    private String dateDebutValidite;
    private String dateFinValidite;
    private String designation;
    private String csi;
    private String quantite;
    private String termeFacturation;
    private Double prixPublicOracle;
    private String tauxIntermediation;
    private Double prixAchatUGAP;
    private String libelleComplet;

    public SupportRenew(String status, String refUGAP, String client, String contrat, String dateDebutValidite, String dateFinValidite, String designation, String csi, String quantite, String termeFacturation, Double prixPublicOracle, String tauxIntermediation, Double prixAchatUGAP, String libelleComplet) {

        this.status = status;
        this.refUGAP = refUGAP;
        this.client = client;
        this.contrat = contrat;
        this.dateDebutValidite = dateDebutValidite;
        this.dateFinValidite = dateFinValidite;
        this.designation = designation;
        this.csi = csi;
        this.quantite = quantite;
        this.termeFacturation = termeFacturation;
        this.prixPublicOracle = prixPublicOracle;
        this.tauxIntermediation = tauxIntermediation;
        this.prixAchatUGAP = prixAchatUGAP;
        this.libelleComplet = libelleComplet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRefUGAP() {
        return refUGAP;
    }

    public void setRefUGAP(String refUGAP) {
        this.refUGAP = refUGAP;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getContrat() {
        return contrat;
    }

    public void setContrat(String contrat) {
        this.contrat = contrat;
    }

    public String getDateDebutValidite() {
        return dateDebutValidite;
    }

    public void setDateDebutValidite(String dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }

    public String getDateFinValidite() {
        return dateFinValidite;
    }

    public void setDateFinValidite(String dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCsi() {
        return csi;
    }

    public void setCsi(String csi) {
        this.csi = csi;
    }

    public String getQuantite() {
        return quantite;
    }

    public void setQuantite(String quantite) {
        this.quantite = quantite;
    }

    public String getTermeFacturation() {
        return termeFacturation;
    }

    public void setTermeFacturation(String termeFacturation) {
        this.termeFacturation = termeFacturation;
    }

    public Double getPrixPublicOracle() {
        return prixPublicOracle;
    }

    public void setPrixPublicOracle(Double prixPublicOracle) {
        this.prixPublicOracle = prixPublicOracle;
    }

    public String getTauxIntermediation() {
        return tauxIntermediation;
    }

    public void setTauxIntermediation(String tauxIntermediation) {
        this.tauxIntermediation = tauxIntermediation;
    }

    public Double getPrixAchatUGAP() {
        return prixAchatUGAP;
    }

    public void setPrixAchatUGAP(Double prixAchatUGAP) {
        this.prixAchatUGAP = prixAchatUGAP;
    }

    public String getLibelleComplet() {
        return libelleComplet;
    }

    public void setLibelleComplet(String libelleComplet) {
        this.libelleComplet = libelleComplet;
    }

    @Override
    public String toString() {
        return "SupportRenew{" +
                "status='" + status + '\'' +
                ", refUGAP='" + refUGAP + '\'' +
                ", client='" + client + '\'' +
                ", contrat='" + contrat + '\'' +
                ", dateDebutValidite='" + dateDebutValidite + '\'' +
                ", dateFinValidite='" + dateFinValidite + '\'' +
                ", designation='" + designation + '\'' +
                ", csi='" + csi + '\'' +
                ", quantite='" + quantite + '\'' +
                ", termeFacturation='" + termeFacturation + '\'' +
                ", prixPublicOracle=" + prixPublicOracle +
                ", tauxIntermediation='" + tauxIntermediation + '\'' +
                ", prixAchatUGAP=" + prixAchatUGAP +
                ", libelleComplet='" + libelleComplet + '\'' +
                '}';
    }
}
