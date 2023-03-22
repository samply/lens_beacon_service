package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;

/**
 * Convert a list of LensQuery leaf elements into a list of Beacon filters for biosamples.
 */

public class ConvertBiosamples extends Convert {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        BeaconFilter beaconFilter = null;
        if (lensQuery.key != null)
            switch (lensQuery.key) {
                case "sample_kind":
                    beaconFilter = new SampleTypeConverter().convert(lensQuery);
                    break;
            }

        return beaconFilter;
    }
}
