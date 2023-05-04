package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;
import de.samply.lens_beacon_service.measurereport.Utils;

import java.util.List;

/**
 * Convert Lens gender into something that Beacon can understand.
 */

public class EthnicityConverter implements Converter {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        String ethnicity = Utils.getEthnicityNameNcit().get(((List)lensQuery.value).get(0));
        BeaconFilter beaconFilter = new BeaconFilter("id", ethnicity);
        return beaconFilter;
    }
}
