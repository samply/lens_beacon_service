package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.Utils;
import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensAstNode;

import java.util.List;

/**
 * Convert the Lens AST representation of ethnicity into a corresponding Beacon filter.
 */

public class EthnicityConverter implements Converter {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        String ethnicity = Utils.getEthnicityNameNcit().get(((List) lensAstNode.value).get(0));
        BeaconFilter beaconFilter = new BeaconFilter("id", ethnicity);
        return beaconFilter;
    }
}
