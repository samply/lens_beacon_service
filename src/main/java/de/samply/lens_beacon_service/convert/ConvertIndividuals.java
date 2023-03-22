package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;

/**
 * Convert a list of LensQuery leaf elements into a list of Beacon filters for individuals.
 */

public class ConvertIndividuals extends Convert {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        BeaconFilter beaconFilter = null;
        if (lensQuery.key != null)
            switch (lensQuery.key) {
                case "gender":
                    beaconFilter = new SexConverter().convert(lensQuery);
                    break;
            }

        return beaconFilter;
    }
}
