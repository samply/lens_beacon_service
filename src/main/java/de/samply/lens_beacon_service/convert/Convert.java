package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert a list of LensQuery leaf elements into a list of Beacon filters.
 */

public abstract class Convert {
    public List<BeaconFilter> convert(List<LensQuery> lensQueryLeafNodeList) {
        List<BeaconFilter> beaconFilterList = new ArrayList<BeaconFilter>();
        for (LensQuery lensQuery: lensQueryLeafNodeList) {
            BeaconFilter beaconFilter = convert(lensQuery);
            if (beaconFilter != null)
                beaconFilterList.add(beaconFilter);
        }
        return beaconFilterList;
    }

    public abstract BeaconFilter convert(LensQuery lensQuery);
}
