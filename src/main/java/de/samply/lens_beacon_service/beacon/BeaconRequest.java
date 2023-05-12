package de.samply.lens_beacon_service.beacon;

/**
 * Describes the request that will be sent to Beacon.
 *
 * Right now, the only thing we are modeling is the query.
 */
public class BeaconRequest {
    public BeaconRequest(BeaconQuery query) {
        this.query = query;
    }

    public BeaconQuery query;
}
