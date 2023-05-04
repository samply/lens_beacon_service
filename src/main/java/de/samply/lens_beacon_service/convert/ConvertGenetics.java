package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert a list of LensQuery leaf elements into a list of Beacon filters for biosamples.
 */

@Slf4j
public class ConvertGenetics extends Convert {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        BeaconFilter beaconFilter = null;
        if (lensQuery.key != null)
            log.info("lensQuery.key: " + lensQuery.key);
            switch (lensQuery.key) {
                case "genomic_variation":
                    beaconFilter = new GeneticVariationConverter().convert(lensQuery);
                    break;
            }

        return beaconFilter;
    }
}
