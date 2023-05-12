package de.samply.lens_beacon_service.beacon;

import java.util.Map;

/**
 * Used to capture the response that comes back from Beacon when an API call is made.
 */

public class BeaconResponse {
    public Map meta;
    public Map responseSummary; // all queries should put something here, even if the number of results is 0.
    public Map response; // count or boolean queries should not fill this, only detail queries will put something in this variable.
}
