package de.samply.lens_beacon_service.entrytype.individuals.ast2filter;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.ast2filter.AstNodeListConverter;
import de.samply.lens_beacon_service.lens.AstNode;

/**
 * Convert a list of AstNode leaf elements into a list of Beacon filters for individuals.
 */

public class IndividualsAstNodeListConverter extends AstNodeListConverter {
    @Override
    public BeaconFilter convertSingleAstNode(AstNode astNode) {
        BeaconFilter beaconFilter = null;
        if (astNode.key != null)
            // Choose the relevant converter for this AstNode.
            switch (astNode.key) {
                case "gender":
                    beaconFilter = new SexAstNodeConverter().convert(astNode);
                    break;
                case "ethnicity":
                    beaconFilter = new EthnicityAstNodeConverter().convert(astNode);
                    break;
            }

        return beaconFilter;
    }
}
