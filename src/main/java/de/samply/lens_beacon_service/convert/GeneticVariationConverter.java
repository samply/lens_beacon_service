package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensAstNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert the Lens AST representation of a genetic variant into a corresponding Beacon filter.
 */

@Slf4j
public class GeneticVariationConverter implements Converter {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        log.info("convert: lensAstNode=" + lensAstNode);
        String geneticVariation = (String) lensAstNode.value;
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
