package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensAstNode;

import java.util.List;

/**
 * Convert the Lens AST representation of gender into a corresponding Beacon filter.
 */

public class SexConverter implements Converter {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        BeaconFilter beaconFilter = defaultBeaconFilter;
        if (((List) lensAstNode.value).get(0).equals("male"))
            beaconFilter = new BeaconFilter("id", "NCIT:C20197");
        if (((List) lensAstNode.value).get(0).equals("female"))
            beaconFilter = new BeaconFilter("id", "NCIT:C16576");
        return beaconFilter;
    }
}
