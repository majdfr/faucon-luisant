package DevisOracle.DevisDigora;


public class DigoraAnalyse {

    private String info;
    private String value;
    private boolean alerte;

    public DigoraAnalyse(String info, String value, boolean alerte) {
        this.info = info;
        this.value = value;
        this.alerte = alerte;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isAlerte() {
        return alerte;
    }

    public void setAlerte(boolean alerte) {
        this.alerte = alerte;
    }
}
