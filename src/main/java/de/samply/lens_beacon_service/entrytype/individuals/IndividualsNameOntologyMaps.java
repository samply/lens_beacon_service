package de.samply.lens_beacon_service.entrytype.individuals;

import java.util.HashMap;
import java.util.Map;

/**
 * Map Lens term names onto Ontology IDs understood by Beacon.
 */
public class IndividualsNameOntologyMaps {
    // Maps the standard names for genders onto NCIT codes
    public static Map<String, String> genderNameNcit;
    static {
        genderNameNcit = new HashMap<String, String>();

        genderNameNcit.put("female", "NCIT:C16576");
        genderNameNcit.put("male", "NCIT:C20197");
    }

    // Maps the standard names for ethnicities onto NCIT codes
    public static Map<String, String> ethnicityNameNcit;
    static {
        ethnicityNameNcit = new HashMap<String, String>();

        ethnicityNameNcit.put("Irish", "NCIT:C43856");
        ethnicityNameNcit.put("Mixed", "NCIT:C67109");
        ethnicityNameNcit.put("White", "NCIT:C41261");
        ethnicityNameNcit.put("Indian", "NCIT:C67109");
        ethnicityNameNcit.put("Chinese", "NCIT:C41260");
        ethnicityNameNcit.put("African", "NCIT:C42331");
        ethnicityNameNcit.put("British", "NCIT:C41261");
        ethnicityNameNcit.put("Pakistani", "NCIT:C41260");
        ethnicityNameNcit.put("Caribbean", "NCIT:C77810");
        ethnicityNameNcit.put("Bangladeshi", "NCIT:C41260");
        ethnicityNameNcit.put("White and Asian", "NCIT:C67109");
        ethnicityNameNcit.put("Other ethnic group", "NCIT:C67109");
        ethnicityNameNcit.put("Asian or Asian British", "NCIT:C41260");
        ethnicityNameNcit.put("Black or Black British", "NCIT:C16352");
        ethnicityNameNcit.put("White and Black African", "NCIT:C67109");
        ethnicityNameNcit.put("White and Black Caribbean", "NCIT:C67109");
        ethnicityNameNcit.put("Any other Asian background", "NCIT:C67109");
        ethnicityNameNcit.put("Any other mixed background", "NCIT:C67109");
        ethnicityNameNcit.put("Any other Black background", "NCIT:C67109");
        ethnicityNameNcit.put("Any other white background", "NCIT:C67109");
    }
}
