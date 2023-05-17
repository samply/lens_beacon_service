package de.samply.lens_beacon_service.beacon.model;

import de.samply.lens_beacon_service.EntryType;

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
        EntryType individuals = new EntryType("/individuals", "POST");
        EntryType biosamples = new EntryType("/biosamples", "POST");
        EntryType genomicVariations = new EntryType("/g_variants", "POST");

        // Site definitions
        BeaconSite hdCineca = new BeaconSite("HD Cineca", "http://beacon:5050/api",
                new BeaconQueryGranularityLc(),
                new EntryType("/individuals/", "POST"), // Error 380 without trailing slash
                new EntryType("/biosamples/", "POST"), // Error 380 without trailing slash
                new EntryType("/g_variants/", "POST")); // Error 380 without trailing slash
        BeaconSite egaCineca = new BeaconSite("EGA Cineca", "https://ega-archive.org/beacon-apis/cineca",
                new BeaconQueryGranularityUc(),
                individuals, biosamples, genomicVariations);
        BeaconSite molgenisMutations = new BeaconSite("Molgenis mutations", "https://mutatiedatabases.molgeniscloud.org/api/beacon",
                new BeaconQueryGranularityUc(),
                individuals,
                new EntryType("/biosamples", "GET"), // Uses GET, deviates from Beacon 2 standard
                new EntryType("/g_variants", "GET")); // Uses GET, deviates from Beacon 2 standard
        BeaconSite rdcPlayground = new BeaconSite("RDConnect playground", "https://playground.rd-connect.eu/beacon2/api",
                new BeaconQueryGranularityUc(),
                individuals, biosamples, null);
        BeaconSite progenetix = new BeaconSite("Progenetix", "https://progenetix.org/beacon",
                new BeaconQueryGranularityUc(),
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
