package DevisOracle.DevisArrow;

import java.util.*;

import DevisOracle.catalogue.CatalogRef;
import DevisOracle.catalogue.RegleMetier;
import DevisOracle.catalogue.readCatalog;
import DevisOracle.catalogue.readReglesMetier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class appliquerReglesMetier {
    static final Logger log = LogManager.getLogger(appliquerReglesMetier.class.getName());

    List<String> businessLogs = new ArrayList<>();
    List<ExcelQuoteRow> ccQuote = new ArrayList<>();        // devis en sortie
    HashMap<String, RegleMetier> reglesMetier = null;
    // HashMap<String, CatalogRef> myCatalogParRefConstructeur = new HashMap<>();
    // HashMap<String, CatalogRef> myCatalogParRefUGAP = new HashMap<>();
    // HashMap<String, CatalogRef> myCatalogParRefConstructeurConfig = new HashMap<>();


    public appliquerReglesMetier() {
    }

    public ReturnValue appliquerReglesMetier(List<ExcelQuoteRow> arrowQuote, Integer dureeSupport, String familleProduit, Boolean isPromo) {
        // on compare le devis Arrow (Arrowqote) avec les business rules (singleton)
        // intialisation des objets à retourner
        //List<String> businessLogs = new ArrayList<>();        // liste des logs (références modifiées)
        // List<ExcelQuoteRow> ccQuote = new ArrayList<>();        // devis en sortie
        // HashMap<String, RegleMetier> reglesMetier = null;
        try {
            reglesMetier = readReglesMetier.getInstance().getHmRegles();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erreur lors de l'application des règles métier", e);
        }


        // on traverse le devis arrow, ligne par ligne
        ListIterator<ExcelQuoteRow> iter = arrowQuote.listIterator();
        while (iter.hasNext()) {
            System.out.println(" ");
            Boolean refPromoExiste = false;
            // get ligne du devis
            ExcelQuoteRow arrowRow = iter.next();
            // System.out.println("TRAITEMENT de le REF : " + arrowRow.getRef());
            log.debug("TRAITEMENT de le REF : " + arrowRow.getRef());
            // obtenir les infos du catalogue
            CatalogRef catalogRef = new CatalogRef();
            try {
                if (isPromo) {
                    System.out.println("appliquerReglesMetier - refPromo à vérifier : " + arrowRow.getRef()+"PROMO") ;
                    refPromoExiste = readCatalog.getInstance().getCatalogParRefConstructeur().containsKey(arrowRow.getRef()+"PROMO");
                    if (refPromoExiste) {
                        catalogRef = readCatalog.getInstance().getCatalogParRefConstructeur().get(arrowRow.getRef()+"PROMO");
                        System.out.println("appliquerReglesMetier - refPromo existe : " + arrowRow.getRef()+"PROMO") ;
                    } else {
                        catalogRef = readCatalog.getInstance().getCatalogParRefConstructeur().get(arrowRow.getRef());
                        System.out.println("appliquerReglesMetier - aucune ref promo") ;
                    }
                } else {
                    catalogRef = readCatalog.getInstance().getCatalogParRefConstructeur().get(arrowRow.getRef());
                    System.out.println("appliquerReglesMetier - standard") ;
                }
            } catch (Exception e) {
                log.debug("AppliquerRèglesmétier - La ref suivante est introuvable : " + arrowRow.getRef());
            }
            arrowRow.setCatalogRef(catalogRef);
            // obtenir la regle métier éventuelle
            RegleMetier rm = reglesMetier.get(arrowRow.getRef());
            Boolean isRegleMetier = false;
            if (rm != null) {
                isRegleMetier = true;
                // on ignore la référence, si la règle métier le dit
                if (rm.isIgnore()) {
                    System.out.println("On ignore la ref : " + arrowRow.getRef());
                    continue;
                }
            }


            if (catalogRef != null) {
                // on vérifie les prix d'achat du devis, avec ceux de la réponse à AO
                Double prixDevisArrow = arrowRow.getPrixUnitaire();
                Double prixArrowReponseAO = catalogRef.getPrixAchatHT();
                //arrowRow.set
                Double percentageDifference = 100 * Math.abs(prixDevisArrow - prixArrowReponseAO) / ((prixDevisArrow + prixArrowReponseAO) / 2);
                System.out.println("    Pourcentage de différence est de : " + percentageDifference + "  entre prix réponse initial : " + prixArrowReponseAO + " et prix devis : " + prixDevisArrow);
                // prixArrowReponseAO > 0 permet de s'assurer que ous avons bien des prix d'achat 
                if ( prixArrowReponseAO > 0) {
                    if ((prixDevisArrow > prixArrowReponseAO) && percentageDifference > 0.5) {
                        arrowRow.setAlerte(true);
                        //System.out.println("        ATTENTION ! prix achat différent du prix de la réponse à l'AO UGAP. voir avec Arrow" );
                        businessLogs.add("      ATTENTION ! prix achat différent du prix de la réponse à l'AO UGAP. voir avec Arrow");
                        businessLogs.add("          le prix d'achat dans la réponse à l'AO est de : " + prixArrowReponseAO);
                        businessLogs.add("          alors que dans le devis, le prix proposé est de : " + prixDevisArrow);
                        businessLogs.add("          Pourcentage de différence est de : " + percentageDifference);
                    }
                }
            }

            // dans l'ordre :
            // vérifier si on remplace la ref par une autre
            // est-ce que la ref (ancienne ou nouvelle) existe dans le catalog
            // si n'existe pas : on rajoute l'ancienne + alerte.

            try {
                if (isRegleMetier ) { // && !isPromo
                    System.out.println("Regle Metier détectée sur cette ref : " + arrowRow.getRef());
                    if (rm.getRefAlternatives() != null) {
                        // using iterators
                        Iterator<Map.Entry<String, Integer>> itr = rm.getRefAlternatives().entrySet().iterator();
                        while(itr.hasNext())
                        {
                            Map.Entry<String, Integer> entry = itr.next();
                            referenceAlt(entry, dureeSupport, arrowRow, false, familleProduit);
                        } // end while over alternativ refs.
                    } else {
                        // pas de ref alternative. on utilise l'existante.
                        referenceCopy(arrowRow, dureeSupport, familleProduit, refPromoExiste);
                    }
                    if (rm.getRefAjouter()  != null) {
                        // using iterators
                        Iterator<Map.Entry<String, Integer>> itrAdd = rm.getRefAjouter().entrySet().iterator();
                        while(itrAdd.hasNext())
                        {
                            Map.Entry<String, Integer> entry = itrAdd.next();
                            referenceAlt(entry, dureeSupport, arrowRow, true, familleProduit);
                        } // end while over alternativ refs.
                    } // end if
                } else {
                    System.out.println("Arrowrow : " + arrowRow.toString() + " - Duree support : " + dureeSupport + " - famille produit " + familleProduit);
                    referenceCopy(arrowRow, dureeSupport, familleProduit, refPromoExiste);
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("AppliquerRèglesmétier - " , e);
            }
        }

        ReturnValue returnValue = new ReturnValue();
        returnValue.setComputaQuote(ccQuote);
        returnValue.setArrowQuote(arrowQuote);
        returnValue.setLogs(businessLogs);

        System.out.println("Appliquer Regles Metier - ccQuote : " + ccQuote.toString());

        log.debug("Fin des business rules. on retourne le résultat  ");
        return returnValue;
    }


    private void referenceAlt (Map.Entry<String, Integer> entry,  Integer dureeSupport, ExcelQuoteRow arrowRow, Boolean isAdd, String config) throws Exception {

        String refAdd = entry.getKey();
        // on modifie la ref par la reference alternative
        //CatalogRef newRefAdd = myCatalogParRefConstructeur.get(refAdd);

        Boolean altKeyAvailable = readCatalog.getInstance().getCatalogParRefConstructeurConfig().containsKey(refAdd + config);
        // System.out.println("    Does it contain the <" + refAdd + config+"> key ? : " + altKeyAvailable );

        CatalogRef newRefAdd;
        if (altKeyAvailable) {
            newRefAdd = readCatalog.getInstance().getCatalogParRefConstructeurConfig().get(refAdd + config);
            // System.out.println("    Reference <" + refAdd + config + "> par RefConstructeur/Config : " + newRefAdd);
            // System.out.println("    on utilise la ref constructeur alternative : " + refAdd);
        } else {
            Boolean oldAltKeyAvailable = readCatalog.getInstance().getCatalogParRefConstructeur().containsKey(refAdd);
            if (oldAltKeyAvailable ) {
                newRefAdd = readCatalog.getInstance().getCatalogParRefConstructeur().get(refAdd);
            } else {
                // System.out.print("  Erreur ! Objet Null. On sort de referenceAlt pour la ref : " + refAdd);
                log.debug("  Erreur ! Objet Null. On sort de referenceAlt pour la ref : " + refAdd);
                return;
            }
        }

        Integer refQte = 0;
        if (isAdd) {
            refQte = entry.getValue();
        } else{
            refQte = arrowRow.getQuantite() * entry.getValue();
        }

        ExcelQuoteRow singleCCRowQuoteAdd = new ExcelQuoteRow(arrowRow.getSectionDevis(),
                newRefAdd.getRefConstructeur(),
                newRefAdd.getRefUGAP(),
                arrowRow.getRef(), // permet de garder la reférence d'origine, qui a été remplacée
                newRefAdd.getDesignation(),
                refQte,
                newRefAdd.getPrixPublicReponse(),
                Double.valueOf(0),
                newRefAdd.getPrixNetUGAP(),
                Double.valueOf(0),
                newRefAdd.getPrixNetUGAP() * refQte, true, false, newRefAdd);
        // on rajoute à la liste à retourner
        this.ccQuote.add(singleCCRowQuoteAdd);

        // on rajoute maintenant le support
        ExcelQuoteRow support = AddSupport(singleCCRowQuoteAdd, dureeSupport, false);
        if (support != null) {
            this.ccQuote.add(support);
        }
        businessLogs.add("      On remplace la ref " + arrowRow.getRef() + " par la ref : " + newRefAdd.getRefConstructeur());
        log.debug("        On remplace la ref " + arrowRow.getRef() + " par la ref : " + newRefAdd.getRefConstructeur());


    }


    private void referenceCopy (ExcelQuoteRow arrowRow, Integer dureeSupport, String familleProduit, Boolean isPromo) throws Exception {

        Boolean notInCatalog=false;

        ExcelQuoteRow singleCCRowQuote1;
        if (arrowRow.getCatalogRef() == null ) {
            notInCatalog = true;
            singleCCRowQuote1 = new ExcelQuoteRow(
                    arrowRow.getSectionDevis(),
                    arrowRow.getRef(),
                    "REF MANQUANTE",
                    arrowRow.getRef(), // permet de garder la reférence d'origine, qui a été remplacée
                    arrowRow.getDesignation(),
                    arrowRow.getQuantite(),
                    arrowRow.getPrixPublic(),
                    arrowRow.getPourcentageRemise(),
                    arrowRow.getPrixUnitaire(),
                    arrowRow.getPrixAchatHT(),
                    arrowRow.getQuantite() * arrowRow.getPrixUnitaire(), false, true, arrowRow.getCatalogRef());
        } else {
            String cleConstructeurConfig = arrowRow.getCatalogRef().getRefConstructeur() + familleProduit;

            if (isPromo) {
                cleConstructeurConfig = cleConstructeurConfig + "PROMO";
            }


            // clé alternative;, liée à la catégorie du matériel
            Boolean addAltKeyAvailable = readCatalog.getInstance().getCatalogParRefConstructeurConfig().containsKey(cleConstructeurConfig);
            // clé par défaut.
            String catalogRefKey;
            catalogRefKey = arrowRow.getCatalogRef().getRefConstructeur();

            if (isPromo) {
                catalogRefKey = catalogRefKey + "PROMO";
            }


            Boolean addKeyAvailable = readCatalog.getInstance().getCatalogParRefConstructeur().containsKey(catalogRefKey);


            CatalogRef catalogRef;
            if (addAltKeyAvailable) {
                System.out.println("    Clé alternative disponible : " + cleConstructeurConfig);
                catalogRef = readCatalog.getInstance().getCatalogParRefConstructeurConfig().get(cleConstructeurConfig);
            } else if (addKeyAvailable) {
                System.out.println("    Clé par défaut : " + arrowRow.getRef());
                String arrowRowKey = arrowRow.getRef();

                if (isPromo) {
                    arrowRowKey = arrowRowKey + "PROMO";
                }


                catalogRef = readCatalog.getInstance().getCatalogParRefConstructeur().get(arrowRowKey);
            } else {
                catalogRef = null;
            }

            if (addKeyAvailable || addAltKeyAvailable) {
                System.out.println("CopyRef : " + catalogRef);
                singleCCRowQuote1 = new ExcelQuoteRow(arrowRow.getSectionDevis(),
                        catalogRef.getRefConstructeur(),
                        catalogRef.getRefUGAP(),
                        arrowRow.getRef(), // permet de garder la reférence d'origine, qui a été remplacée
                        catalogRef.getDesignation(),
                        arrowRow.getQuantite(),
                        catalogRef.getPrixPublicReponse(),
                        arrowRow.getPourcentageRemise(),
                        catalogRef.getPrixNetUGAP(),
                        arrowRow.getPrixAchatHT(),
                        arrowRow.getQuantite() * catalogRef.getPrixNetUGAP(), false, false, catalogRef);
            } else {
                singleCCRowQuote1 = new ExcelQuoteRow(
                        arrowRow.getSectionDevis(),
                        arrowRow.getRef(),
                        "REF MANQUANTE",
                        arrowRow.getRef(), // permet de garder la reférence d'origine, qui a été remplacée
                        arrowRow.getDesignation(),
                        arrowRow.getQuantite(),
                        arrowRow.getPrixPublic(),
                        arrowRow.getPourcentageRemise(),
                        arrowRow.getPrixUnitaire(),
                        arrowRow.getPrixAchatHT(),
                        arrowRow.getQuantite() * arrowRow.getPrixUnitaire(), false, true, arrowRow.getCatalogRef());
            }
        }
        // on rajoute à la liste à retourner
        ccQuote.add(singleCCRowQuote1);

        // on rajoute maintenant le support
        ExcelQuoteRow support = AddSupport(singleCCRowQuote1, dureeSupport, isPromo);
        if (support != null) {
            ccQuote.add(support);
        }


        businessLogs.add("      La ref " + arrowRow.getRef() + " utilisera le prix de la réponse UGAP AO 2020.");
        log.debug("        La ref " + arrowRow.getRef() + " utilisera le prix de la réponse UGAP AO 2020.");
    }


    public static ExcelQuoteRow AddSupport(ExcelQuoteRow arrowRow, Integer dureeSupport, Boolean isPromo) throws Exception {
        HashMap<String, CatalogRef> myCatalog = null;
        HashMap<String, RegleMetier> regles = readReglesMetier.getInstance().getHmRegles();
        RegleMetier rm;
        // Boolean ignoreSupport = false;
        try {
            myCatalog = readCatalog.getInstance().getCatalogParRefConstructeur();
            // rm = reglesMetier.get(arrowRow.getRef());
            rm = regles.get(arrowRow.getRef());
            if (rm != null) {
                if (rm.isIgnoreSupport()) { return null; }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String ref = arrowRow.getRef().trim().toUpperCase();
        String firstLetter = ref.substring(0, 1);
        String supportRef = arrowRow.getRef().trim();
        Boolean isHardware = false;
        Double tauxSupport = 0.12;
        String refSupport;
        if (firstLetter.equals("L") || firstLetter.equals("B") || firstLetter.equals("A")) {
            supportRef = supportRef + "_Y" + dureeSupport;
            isHardware = false;
            tauxSupport = 0.22;
            refSupport  = "A97163";
        } else {
            supportRef = supportRef + "/Y" + dureeSupport;
            isHardware = true;
            tauxSupport = 0.12;
            refSupport  = "B58121";
        }

        if (isPromo) {

            supportRef = supportRef + "PROMO";
            System.out.println("addSupport : PROMOTION ! Le refSupport : " + supportRef);
        }
        CatalogRef catalogRef = myCatalog.get(supportRef);
        if (catalogRef == null) {
            // on gère les ref SL8500 qui ont une ref support : refUgap/Y3
            CatalogRef catalogRefTape = myCatalog.get(arrowRow.getRefUGAP().trim()+ "/Y" + dureeSupport);
            if (catalogRefTape != null) {
                ExcelQuoteRow support = new ExcelQuoteRow(
                        arrowRow.getSectionDevis(),
                        catalogRefTape.getRefConstructeur(),
                        catalogRefTape.getRefUGAP(),
                        refSupport,
                        catalogRefTape.getDesignation(),
                        arrowRow.getQuantite(),
                        arrowRow.getPrixPublic() * tauxSupport * dureeSupport,
                        arrowRow.getPourcentageRemise(),
                        catalogRefTape.getPrixNetUGAP(),
                        arrowRow.getPrixAchatHT() * tauxSupport,
                        catalogRefTape.getPrixNetUGAP() * arrowRow.getQuantite(),
                        false, false, catalogRefTape);
                return support;
            } else {
                System.out.println("pas de regle métier  pour la ref : " + supportRef);
                Integer secDevis = 0;
                try {
                    secDevis = arrowRow.getSectionDevis();
                } catch (Exception e) {
                }


                ExcelQuoteRow support = new ExcelQuoteRow(
                        secDevis,
                        refSupport,
                        "REF MANQUANTE",
                        refSupport, // permet de garder la reférence d'origine, qui a été remplacée
                        arrowRow.getDesignation(),
                        arrowRow.getQuantite(),
                        arrowRow.getPrixPublic() * tauxSupport * dureeSupport,
                        arrowRow.getPourcentageRemise(),
                        arrowRow.getPrixUnitaire() * tauxSupport,
                        arrowRow.getPrixAchatHT() * tauxSupport,
                        arrowRow.getPrixUnitaire() * arrowRow.getQuantite() * tauxSupport,
                        false, true, catalogRef);
                return support;
            }
        } else {
            try {
                ExcelQuoteRow support = new ExcelQuoteRow(
                        arrowRow.getSectionDevis(),
                        catalogRef.getRefConstructeur(),
                        catalogRef.getRefUGAP(),
                        refSupport,
                        catalogRef.getDesignation(),
                        arrowRow.getQuantite(),
                        arrowRow.getPrixPublic() * tauxSupport * dureeSupport,
                        arrowRow.getPourcentageRemise(),
                        catalogRef.getPrixNetUGAP(),
                        arrowRow.getPrixAchatHT() * tauxSupport,
                        catalogRef.getPrixNetUGAP() * arrowRow.getQuantite(),
                        false, false, catalogRef);
                return support;
            } catch (Exception e) {
                log.error("Appliquer Règles métier - error ", e);
                return null;
            }
        }
    }
}


