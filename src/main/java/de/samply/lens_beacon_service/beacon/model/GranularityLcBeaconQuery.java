package de.samply.lens_beacon_service.beacon.model;

/**
 * Class representing a Beacon query.
 *
 * This extension adds a lower-case request granularity.
 */

public class GranularityLcBeaconQuery extends BeaconQuery {
    // Request granularity: boolean, count or detail.
    public String requestedGranularity = "count";
}
