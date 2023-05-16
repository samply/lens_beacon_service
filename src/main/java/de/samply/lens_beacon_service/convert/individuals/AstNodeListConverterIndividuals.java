package de.samply.lens_beacon_service.convert.individuals;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeListConverter;
import de.samply.lens_beacon_service.lens.AstNode;

/**
 * Convert a list of AstNode leaf elements into a list of Beacon filters for individuals.
 */

public class AstNodeListConverterIndividuals extends AstNodeListConverter {
    @Override
    public BeaconFilter convert(AstNode astNode) {
        BeaconFilter beaconFilter = null;
        if (astNode.key != null)
            // Choose the relevant converter for this AstNode.
            switch (astNode.key) {
                case "gender":
                    beaconFilter = new AstNodeConverterSex().convert(astNode);
                    break;
                case "ethnicity":
                    beaconFilter = new AstNodeConverterEthnicity().convert(astNode);
                    break;
            }

        return beaconFilter;
    }
}
