package de.samply.lens_beacon_service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameters and functions used throughout the code.
 */
public class Utils {

    // Maps the standard names for ethnicities onto NCIT codes
    private static Map<String, String> ethnicityNameNcit;
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

    /**
     * Get a map from standard names for ethnicities onto NCIT codes.
     * @return Map of ethnicities.
     */
    public static Map<String, String> getEthnicityNameNcit() {
        return ethnicityNameNcit;
    }

    /**
     * Get a printable stack trace from an Exception object.
     * @param e
     * @return
     */
    public static String traceFromException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
