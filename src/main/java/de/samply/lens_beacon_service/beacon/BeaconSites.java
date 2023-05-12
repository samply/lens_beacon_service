package de.samply.lens_beacon_service.beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a static list of known Beacon sites.
 *
 * This is basically a hard-coded list of sites that will be contacted when a
 * query is received. It would be better if this information resided in a database.
 * Some kind of registration API would also be a good idea.
 */

public class BeaconSites {
    // Make this class a singleton
    private BeaconSites() {
    }

    private static List<BeaconSite> sites;
    static
    {
        sites = new ArrayList<BeaconSite>();

        // Standard entry types
        BeaconEntryType individuals = new BeaconEntryType("/individuals", "POST");
        BeaconEntryType biosamples = new BeaconEntryType("/biosamples", "POST");
        BeaconEntryType genomicVariations = new BeaconEntryType("/g_variants", "POST");

        // Site definitions
        BeaconSite hdCineca = new BeaconSite("HD Cineca", "http://beacon:5050/api",
                new BeaconEntryType("/individuals/", "POST"), // Error 380 without trailing slash
                new BeaconEntryType("/biosamples/", "POST"), // Error 380 without trailing slash
                new BeaconEntryType("/g_variants/", "POST")); // Error 380 without trailing slash
        BeaconSite egaCineca = new BeaconSite("EGA Cineca", "https://ega-archive.org/beacon-apis/cineca",
                individuals, biosamples, genomicVariations);
        BeaconSite molgenisMutations = new BeaconSite("Molgenis mutations", "https://mutatiedatabases.molgeniscloud.org/api/beacon",
                individuals,
                new BeaconEntryType("/biosamples", "GET"), // Uses GET, deviates from Beacon 2 standard
                new BeaconEntryType("/g_variants", "GET")); // Uses GET, deviates from Beacon 2 standard
        BeaconSite rdcPlayground = new BeaconSite("RDConnect playground", "https://playground.rd-connect.eu/beacon2/api",
                individuals, biosamples, null);
        BeaconSite progenetix = new BeaconSite("Progenetix", "https://progenetix.org/beacon",
                individuals, biosamples, genomicVariations);

        sites.add(hdCineca);
//        sites.add(egaCineca);
//        sites.add(molgenisMutations);
//        sites.add(rdcPlayground);
//        sites.add(progenetix);
    }

    public static List<BeaconSite> getSites() {
        return sites;
    }
}
