package de.samply.lens_beacon_service.beacon;

/**
 * Class representing a Beacon query.
 *
 * This extension adds a lower-case request granularity.
 */

public class BeaconQueryGranularityLc extends BeaconQuery {
    // Request granularity: boolean, count or detail.
    public String requestedGranularity = "count";
}
