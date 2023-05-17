package de.samply.lens_beacon_service.beacon.model;

import de.samply.lens_beacon_service.BiosamplesEntryType;
import de.samply.lens_beacon_service.EntryType;
import de.samply.lens_beacon_service.GenomicVariationsEntryType;
import de.samply.lens_beacon_service.IndividualsEntryType;

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
        EntryType individuals = new IndividualsEntryType();
        EntryType biosamples = new BiosamplesEntryType();
        EntryType genomicVariations = new GenomicVariationsEntryType();

        // Site definitions
        BeaconSite hdCineca = new BeaconSite("HD Cineca", "http://beacon:5050/api",
                new BeaconQueryGranularityLc(),
                new IndividualsEntryType("/individuals/", "POST"), // Error 380 without trailing slash
                new BiosamplesEntryType("/biosamples/", "POST"), // Error 380 without trailing slash
                new GenomicVariationsEntryType("/g_variants/", "POST")); // Error 380 without trailing slash
        BeaconSite egaCineca = new BeaconSite("EGA Cineca", "https://ega-archive.org/beacon-apis/cineca",
                new BeaconQueryGranularityUc(),
                individuals, biosamples, genomicVariations);
        BeaconSite molgenisMutations = new BeaconSite("Molgenis mutations", "https://mutatiedatabases.molgeniscloud.org/api/beacon",
                new BeaconQueryGranularityUc(),
                individuals,
                new BiosamplesEntryType("/biosamples", "GET"), // Uses GET, deviates from Beacon 2 standard
                new GenomicVariationsEntryType("/g_variants", "GET")); // Uses GET, deviates from Beacon 2 standard
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
