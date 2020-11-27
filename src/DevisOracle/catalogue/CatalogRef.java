package DevisOracle.catalogue;

public class CatalogRef {
	private String categorieProduit;
	private String refConstructeur;
	private String refDistributeur;
	private String refUGAP;
	private String designation;
	private Double prixPublicReponse;
	private Double prixAchatHT;
	private Double prixNetUGAP;
	private Double prixRetention;
	private Boolean promotion;

	public Boolean getPromotion() {return promotion;}
	public void setPromotion(Boolean promotion) {this.promotion = promotion;}

	public String getRefDistributeur() {
		return refDistributeur;
	}
	public void setRefDistributeur(String refDistributeur) {
		this.refDistributeur = refDistributeur;
	}

	public String getRefUGAP() {
		return refUGAP;
	}
	public void setRefUGAP(String refUGAP) {
		this.refUGAP = refUGAP;
	}

	public String getCategorieProduit() {
		return categorieProduit;
	}
	public void setCategorieProduit(String categorieProduit) {
		this.categorieProduit = categorieProduit;
	}
	public String getRefConstructeur() {
		return refConstructeur;
	}
	public void setRefConstructeur(String refConstructeur) {
		this.refConstructeur = refConstructeur;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public Double getPrixPublicReponse() {
		return prixPublicReponse;
	}
	public void setPrixPublicReponse(Double prixPublicReponse) {
		this.prixPublicReponse = prixPublicReponse;
	}
	public Double getPrixAchatHT() {
		return prixAchatHT;
	}
	public void setPrixAchatHT(Double prixAchatHT) {
		this.prixAchatHT = prixAchatHT;
	}
	public Double getPrixNetUGAP() {
		return prixNetUGAP;
	}
	public void setPrixNetUGAP(Double prixNetUGAP) {
		this.prixNetUGAP = prixNetUGAP;
	}
	public Double getPrixRetention() {
		return prixRetention;
	}
	public void setPrixRetention(Double prixRetention) {
		this.prixRetention = prixRetention;
	}

	@Override
	public String toString() {
		return "CatalogRef{" +
				"categorieProduit='" + categorieProduit + '\'' +
				", refConstructeur='" + refConstructeur + '\'' +
				", refDistributeur='" + refDistributeur + '\'' +
				", refUGAP='" + refUGAP + '\'' +
				", designation='" + designation + '\'' +
				", prixPublicReponse=" + prixPublicReponse +
				", prixAchatHT=" + prixAchatHT +
				", prixNetUGAP=" + prixNetUGAP +
				", prixRetention=" + prixRetention +
				", promotion=" + promotion +
				'}';
	}
}
