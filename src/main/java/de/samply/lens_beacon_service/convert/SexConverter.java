package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;
import java.util.List;

/**
 * Convert Lens gender into something that Beacon can understand.
 */

public class SexConverter implements Converter {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        BeaconFilter beaconFilter = null;
        if (((List)lensQuery.value).get(0).equals("male"))
            beaconFilter = new BeaconFilter("id", "NCIT:C20197");
        if (((List)lensQuery.value).get(0).equals("female"))
            beaconFilter = new BeaconFilter("id", "NCIT:C16576");
        return beaconFilter;
    }
}
