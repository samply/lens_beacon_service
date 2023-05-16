package de.samply.lens_beacon_service.beacon;

import java.util.Map;

/**
 * Used to capture the response that comes back from Beacon when an API call is made.
 */

public class BeaconResponse {
    public Map meta;
    public Map responseSummary; // all queries should put something here, even if the number of results is 0.
    public Map response; // count or boolean queries should not fill this, only detail queries will put something in this variable.

    /**
     * Extract a hit count from the responseSummary, and return it.
     *
     * If only boolean results are available, return 1 for TRUE, 0 for FALSE.
     *
     * Return -1 if no responseSummary is present.
     *
     * @return Hit count for query.
     */
    public Integer getCount() {
        if (responseSummary == null)
            return -1;

        // If there is no results count but "exists" is true, return 1.
        // Rationale: if results exist, then there must be at least one.
        if (!this.responseSummary.containsKey("numTotalResults") &&
                this.responseSummary.containsKey("exists")) {
            if (this.responseSummary.get("exists").toString().equals("true")) {
                return 1;
            } else {
                return 0;
            }
        }
        // We have a results count, so cast it to Integer and return it.
        return (Integer) this.responseSummary.get("numTotalResults");
    }
}
