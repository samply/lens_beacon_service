package de.samply.lens_beacon_service.beacon;

public class BeaconRequest {
    public BeaconRequest(BeaconQuery query) {
        this.query = query;
    }

    public BeaconQuery query;
}
