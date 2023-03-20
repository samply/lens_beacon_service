package de.samply.lens_beacon_service.beacon;

import java.util.List;

/**
 * Used to generate objects representing a Beacon query.
 *
 * Most of these things can be hard-coded, but the filters need to be specified on the fly.
 */

public class BeaconQuery {
    /**
     * Constructor that allows you to supply a list of filters.
     *
     * @param filters A list of Beacon filters.
     */
    public BeaconQuery(List<BeaconFilter> filters) {
        this.filters = filters;
    }

    public List<BeaconFilter> filters;
    public String includeResultsetResponses = "HIT";
    public boolean testMode = false;
    public String requestedGranularity = "count";
}
