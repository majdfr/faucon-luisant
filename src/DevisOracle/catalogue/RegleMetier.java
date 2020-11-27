package DevisOracle.catalogue;

import java.util.List;
import java.util.Map;

public class RegleMetier {
    private String ref;
    private String configuration;
    private boolean ignore;
    private boolean ignoreSupport;
    private Map<String, Integer> refAlternatives;
    private Map<String, Integer> refAjouter;

    public RegleMetier() {
    }

    public boolean isIgnoreSupport() {
        return ignoreSupport;
    }

    public void setIgnoreSupport(boolean ignoreSupport) {
        this.ignoreSupport = ignoreSupport;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Map<String, Integer> getRefAlternatives() {
        return refAlternatives;
    }

    public void setRefAlternatives(Map<String, Integer> refAlternatives) {
        this.refAlternatives = refAlternatives;
    }

    public Map<String, Integer> getRefAjouter() {
        return refAjouter;
    }

    public void setRefAjouter(Map<String, Integer> refAjouter) {
        this.refAjouter = refAjouter;
    }

    @Override
    public String toString() {
        return "RegleMetier{" +
                "ref='" + ref + '\'' +
                ", configuration='" + configuration + '\'' +
                ", ignore=" + ignore +
                ", refAlternatives=" + refAlternatives +
                ", refAjouter=" + refAjouter +
                '}';
    }
}
