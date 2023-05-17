package de.samply.lens_beacon_service.beacon.model;

import de.samply.lens_beacon_service.EntryType;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;

/**
 * Bundles together all the information known about a site hosting a Beacon API.
 *
 * This information includes a name that we can give the site, the URL of the
 * API, plus information about some of the endpoints (BeaconEndpoint).
 */
public class BeaconSite {
    public BeaconSite(String name, String url, BeaconQuery query, EntryType individuals, EntryType biosamples, EntryType genomicVariations) {
        this.name = name;
        this.url = url;
        this.query = query;
        this.individuals = individuals;
        this.biosamples = biosamples;
        this.genomicVariations = genomicVariations;
        beaconQueryService = new BeaconQueryService(url, query);
    }

    public String name; // Site name, e.g. "HD Cineca".

    public String url; // URL of site, e.g. "http://beacon:5050/api".
    public BeaconQuery query; // Beacon query, minus the filters.
    public BeaconQueryService beaconQueryService; // Automatically derived from URL
    public EntryType individuals; // Define how to get information about individuals. null means not available.
    public EntryType biosamples; // Define how to get information about biosamples. null means not available.
    public EntryType genomicVariations; // Define how to get information about genomicVariations. null means not available.
}
