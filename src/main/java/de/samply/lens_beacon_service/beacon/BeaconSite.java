package de.samply.lens_beacon_service.beacon;

/**
 * Bundles together all the information known about a site hosting a Beacon API.
 *
 * This information includes a name that we can give the site, the URL of the
 * API, plus information about some of the endpoints (BeaconEntryType).
 */
public class BeaconSite {
    public BeaconSite(String name, String url, BeaconEntryType individuals, BeaconEntryType biosamples, BeaconEntryType genomicVariations) {
        this.name = name;
        this.url = url;
        this.individuals = individuals;
        this.biosamples = biosamples;
        this.genomicVariations = genomicVariations;
        beaconQueryService = new BeaconQueryService(url);
    }

    public String name; // Site name, e.g. "HD Cineca".

    public String url; // URL of site, e.g. "http://beacon:5050/api".
    public BeaconQueryService beaconQueryService; // Automatically derived from URL
    public BeaconEntryType individuals; // Define how to get information about individuals. null means not available.
    public BeaconEntryType biosamples; // Define how to get information about biosamples. null means not available.
    public BeaconEntryType genomicVariations; // Define how to get information about genomicVariations. null means not available.
}
