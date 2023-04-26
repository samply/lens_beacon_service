package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Convert Lens gender into something that Beacon can understand.
 */

@Slf4j
public class SampleTypeConverter implements Converter {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        log.info("convert: lensQuery=" + lensQuery);
        BeaconFilter beaconFilter = defaultBeaconFilter;
        if (((List)lensQuery.value).get(0).equals("blood"))
            beaconFilter = new BeaconFilter("id", "UBERON:0000178");
        if (((List)lensQuery.value).get(0).equals("blood-serum"))
            beaconFilter = new BeaconFilter("id", "UBERON:0001977");
        if (((List)lensQuery.value).get(0).equals("blood-plasma"))
            beaconFilter = new BeaconFilter("id", "UBERON:0001969");
        if (((List)lensQuery.value).get(0).equals("lymph"))
            beaconFilter = new BeaconFilter("id", "UBERON:0002391");
        return beaconFilter;
    }
}
