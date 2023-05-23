package de.samply.lens_beacon_service.entrytype.biosamples.ast2filter;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.ast2filter.AstNodeListConverter;
import de.samply.lens_beacon_service.lens.AstNode;

/**
 * Convert a list of AstNode leaf elements into a list of Beacon filters for biosamples.
 */

public class BiosamplesAstNodeListConverter extends AstNodeListConverter {
    @Override
    public BeaconFilter convertSingleAstNode(AstNode astNode) {
        BeaconFilter beaconFilter = null;
        if (astNode.key != null)
            // Choose the relevant converter for this AstNode.
            switch (astNode.key) {
                case "sample_kind":
                    beaconFilter = new BiosamplesAstNodeConverter().convert(astNode);
                    break;
            }

        return beaconFilter;
    }
}
