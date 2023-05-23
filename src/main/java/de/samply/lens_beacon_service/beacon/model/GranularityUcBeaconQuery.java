package de.samply.lens_beacon_service.beacon.model;

/**
 * Class representing a Beacon query.
 *
 * This extension adds an upper-case request granularity.
 */

public class GranularityUcBeaconQuery extends BeaconQuery {
    // Request granularity: boolean, count or detail.
    public String requestGranularity = "COUNT";
}
