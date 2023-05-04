package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert Lens gender into something that Beacon can understand.
 */

@Slf4j
public class GeneticVariationConverter implements Converter {
    @Override
    public BeaconFilter convert(LensQuery lensQuery) {
        log.info("convert: lensQuery=" + lensQuery);
        String geneticVariation = (String) lensQuery.value;
        BeaconFilter beaconFilter = defaultBeaconFilter;
        if (beaconFilter != null && !beaconFilter.equals("")) {
            // Remove non-ASCII stuff from filter term, because Beacon does not
            // like it. Also prepend an identifier type, because Beacon wants this.
            geneticVariation = geneticVariation.replace(":", "");
            geneticVariation = geneticVariation.replace(".", "");
            geneticVariation = geneticVariation.replace(">", "");
            geneticVariation = "HGVSid:" + geneticVariation;
            log.info("convert: geneticVariation=" + geneticVariation);
            beaconFilter = new BeaconFilter("id", geneticVariation);
        }
        return beaconFilter;
    }
}
