package de.samply.lens_beacon_service.beacon.model;

/**
 * Encapsulates the information needed to access Beacon endpoint.
 *
 * * uri:    E.g. "/individuals/"
 * * method: POST or GET are allowed.
 */
public class BeaconEndpoint {
    public BeaconEndpoint(String uri, String method) {
        this.uri = uri;
        this.method = method;
    }

    public String uri; // Name of entry type. Presence of absence of trailing slash can be important.
    public String method; // Normally POST, sometimes GET

    /**
     * Get the entry type, based on the URI.
     * @return
     */
    public String getEntryType() {
        return uri.replace("/", "");
    }
}
