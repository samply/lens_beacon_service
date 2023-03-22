package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;

/**
 * Generic template for converting a Lens query into a Beacon 2 filter.
 */
public interface Converter {
    BeaconFilter defaultBeaconFilter = new BeaconFilter("id", "FAKE:00001111");

    public BeaconFilter convert(LensQuery lensQuery);
}
