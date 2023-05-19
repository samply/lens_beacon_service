package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.BeaconQuery;
import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Bundles together all the information known about a site hosting a Beacon API.
 *
 * This information includes a name that we can give the site, the URL of the
 * API, plus information about some of the endpoints (BeaconEndpoint).
 */
public class Site {
    public Site(String name, String url, BeaconQuery query, EntryType individuals, EntryType biosamples, EntryType genomicVariations) {
        this.name = name;
        this.url = url;
        this.query = query;
        beaconQueryService = new BeaconQueryService(url, query);
        entryTypes.add(individuals);
        entryTypes.add(biosamples);
        entryTypes.add(genomicVariations);
    }

    public String name; // Site name, e.g. "HD Cineca".

    public String url; // URL of site, e.g. "http://beacon:5050/api".
    public BeaconQuery query; // Beacon query, minus the filters.
    public BeaconQueryService beaconQueryService; // Automatically derived from URL
    public List<EntryType> entryTypes = new ArrayList<EntryType>();
}
