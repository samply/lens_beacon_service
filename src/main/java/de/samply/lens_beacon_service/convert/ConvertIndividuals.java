package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensAstNode;

/**
 * Convert a list of LensAstNode leaf elements into a list of Beacon filters for individuals.
 */

public class ConvertIndividuals extends Convert {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        BeaconFilter beaconFilter = null;
        if (lensAstNode.key != null)
            // Choose the relevant converter for this LensAstNode.
            switch (lensAstNode.key) {
                case "gender":
                    beaconFilter = new SexConverter().convert(lensAstNode);
                    break;
                case "ethnicity":
                    beaconFilter = new EthnicityConverter().convert(lensAstNode);
                    break;
            }

        return beaconFilter;
    }
}
