package de.samply.lens_beacon_service.convert.individuals;

import de.samply.lens_beacon_service.Utils;
import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeConverter;
import de.samply.lens_beacon_service.lens.AstNode;

import java.util.List;

/**
 * Convert the Lens AST representation of ethnicity into a corresponding Beacon filter.
 */

public class AstNodeConverterEthnicity implements AstNodeConverter {
    @Override
    public BeaconFilter convert(AstNode astNode) {
        String ethnicity = Utils.getEthnicityNameNcit().get(((List) astNode.value).get(0));
        BeaconFilter beaconFilter = new BeaconFilter("id", ethnicity);
        return beaconFilter;
    }
}
