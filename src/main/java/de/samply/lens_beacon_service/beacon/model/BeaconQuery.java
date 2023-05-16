package de.samply.lens_beacon_service.beacon.model;

import java.util.List;

/**
 * Class representing a Beacon query.
 *
 * Most of these parameters are hard-coded, but the filters need to be specified on the fly.
 */

public class BeaconQuery implements Cloneable {
    /**
     * Create a clone of the object, with the supplied filters.
     *
     * @param filters
     * @return
     */
    public BeaconQuery clone(List<BeaconFilter> filters) {
        try {
            BeaconQuery clonedBeaconQuery = (BeaconQuery) super.clone();
            clonedBeaconQuery.filters = filters;
            return clonedBeaconQuery;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public List<BeaconFilter> filters;
    public String includeResultsetResponses = "HIT";
    public boolean testMode = false;
}
