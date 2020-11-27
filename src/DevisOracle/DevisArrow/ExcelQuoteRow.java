package DevisOracle.DevisArrow;

import DevisOracle.catalogue.CatalogRef;
import javafx.beans.property.*;

public class ExcelQuoteRow {

	private final IntegerProperty sectionDevis;
	private final StringProperty  ref;
	private final StringProperty  refUGAP;
	private final StringProperty  refOrigine;
	private final StringProperty designation;
	private final IntegerProperty  quantite;
	private final DoubleProperty prixPublic;
	private final DoubleProperty pourcentageRemise;
	private final DoubleProperty prixUnitaire;
	private final DoubleProperty prixAchatHT;
	private final DoubleProperty prixVenteHT;
	private final BooleanProperty modifiee;
	private final BooleanProperty alerte;
	private CatalogRef catalogRef;

	public ExcelQuoteRow(Integer sectionDevis, String ref, String refUGAP, String refOrigine, String designation, Integer quantite,
						 Double prixPublic, Double pourcentageRemise, Double prixUnitaire,
						 Double prixAchatHT, Double prixVenteHT, Boolean modifiee, Boolean alerte, CatalogRef catalogRef) {
		super();
		if (sectionDevis == null) {
			sectionDevis = 0;
		}
		if (pourcentageRemise == null) {
			pourcentageRemise = Double.valueOf(0);
		}
		this.sectionDevis = new SimpleIntegerProperty(sectionDevis);
		this.ref = new SimpleStringProperty(ref);
		this.refUGAP = new SimpleStringProperty(refUGAP);
		this.refOrigine = new SimpleStringProperty(refOrigine);
		this.designation = new SimpleStringProperty(designation);
		this.quantite = new SimpleIntegerProperty(quantite);
		this.prixPublic = new SimpleDoubleProperty(prixPublic);
		this.pourcentageRemise = new SimpleDoubleProperty(pourcentageRemise);
		this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
		this.prixAchatHT = new SimpleDoubleProperty(prixAchatHT);
		this.prixVenteHT = new SimpleDoubleProperty(prixVenteHT);
		this.modifiee = new SimpleBooleanProperty(modifiee);
		this.alerte = new SimpleBooleanProperty(alerte);
		this.catalogRef = catalogRef;
	}

	public ExcelQuoteRow() {
		this.sectionDevis = new SimpleIntegerProperty(0);
		this.ref = new SimpleStringProperty("");
		this.refUGAP = new SimpleStringProperty("");
		this.refOrigine = new SimpleStringProperty("");
		this.designation = new SimpleStringProperty("");
		this.quantite = new SimpleIntegerProperty(1);
		this.prixPublic = new SimpleDoubleProperty(0);
		this.pourcentageRemise = new SimpleDoubleProperty(0);
		this.prixUnitaire = new SimpleDoubleProperty(0);
		this.prixAchatHT = new SimpleDoubleProperty(0);
		this.prixVenteHT = new SimpleDoubleProperty(0);
		this.modifiee = new SimpleBooleanProperty(false);
		this.alerte = new SimpleBooleanProperty(false);
		this.catalogRef = null;
	}


    public String getRefOrigine() {
		return refOrigine.get();
	}

	public StringProperty refOrigineProperty() {
		return refOrigine;
	}

	public void setRefOrigine(String refOrigine) {
		this.refOrigine.set(refOrigine);
	}

	public String getRefUGAP() {
		return refUGAP.get();
	}

	public StringProperty refUGAPProperty() {
		return refUGAP;
	}

	public void setRefUGAP(String refUGAP) {
		this.refUGAP.set(refUGAP);
	}

	public int getSectionDevis() {
		return sectionDevis.get();
	}

	public IntegerProperty sectionDevisProperty() {
		return sectionDevis;
	}

	public void setSectionDevis(int sectionDevis) {
		this.sectionDevis.set(sectionDevis);
	}

	public CatalogRef getCatalogRef() {
		return catalogRef;
	}

	public void setCatalogRef(CatalogRef catalogRef) {
		this.catalogRef = catalogRef;
	}

	public boolean isAlerte() {
		return alerte.get();
	}

	public BooleanProperty alerteProperty() {
		return alerte;
	}

	public void setAlerte(boolean alerte) {
		this.alerte.set(alerte);
	}

	public boolean isModifiee() {
		return modifiee.get();
	}

	public BooleanProperty modifieeProperty() {
		return modifiee;
	}

	public void setModifiee(boolean modifiee) {
		this.modifiee.set(modifiee);
	}

	public String getRef() {
		return ref.get();
	}

	public StringProperty refProperty() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref.set(ref);
	}

	public String getDesignation() {
		return designation.get();
	}

	public StringProperty designationProperty() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation.set(designation);
	}

	public int getQuantite() {
		return quantite.get();
	}

	public IntegerProperty quantiteProperty() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite.set(quantite);
	}

	public double getPrixPublic() {
		return prixPublic.get();
	}

	public DoubleProperty prixPublicProperty() {
		return prixPublic;
	}

	public void setPrixPublic(double prixPublic) {
		this.prixPublic.set(prixPublic);
	}

	public double getPourcentageRemise() {
		return pourcentageRemise.get();
	}

	public DoubleProperty pourcentageRemiseProperty() {
		return pourcentageRemise;
	}

	public void setPourcentageRemise(double pourcentageRemise) {
		this.pourcentageRemise.set(pourcentageRemise);
	}

	public double getPrixUnitaire() {
		return prixUnitaire.get();
	}

	public DoubleProperty prixUnitaireProperty() {
		return prixUnitaire;
	}

	public void setPrixUnitaire(double prixUnitaire) {
		this.prixUnitaire.set(prixUnitaire);
	}

	public double getPrixAchatHT() {
		return prixAchatHT.get();
	}

	public DoubleProperty prixAchatHTProperty() {
		return prixAchatHT;
	}

	public void setPrixAchatHT(double prixAchatHT) {
		this.prixAchatHT.set(prixAchatHT);
	}

	public double getPrixVenteHT() {
		return prixVenteHT.get();
	}

	public DoubleProperty prixVenteHTProperty() {
		return prixVenteHT;
	}

	public void setPrixVenteHT(double prixVenteHT) {
		this.prixVenteHT.set(prixVenteHT);
	}


	@Override
	public String toString() {
		return "ExcelQuoteRow{" +
				"sectionDevis=" + sectionDevis +
				", ref=" + ref +
				", refUGAP=" + refUGAP +
				", designation=" + designation +
				", quantite=" + quantite +
				", prixPublic=" + prixPublic +
				", pourcentageRemise=" + pourcentageRemise +
				", prixUnitaire=" + prixUnitaire +
				", prixAchatHT=" + prixAchatHT +
				", prixVenteHT=" + prixVenteHT +
				", modifiee=" + modifiee +
				", alerte=" + alerte +
				", catalogRef=" + catalogRef +
				'}';
	}
}
