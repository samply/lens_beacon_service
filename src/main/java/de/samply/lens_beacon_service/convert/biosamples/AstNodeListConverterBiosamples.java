package de.samply.lens_beacon_service.convert.biosamples;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeListConverter;
import de.samply.lens_beacon_service.lens.LensAstNode;

/**
 * Convert a list of LensAstNode leaf elements into a list of Beacon filters for biosamples.
 */

public class AstNodeListConverterBiosamples extends AstNodeListConverter {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        BeaconFilter beaconFilter = null;
        if (lensAstNode.key != null)
            // Choose the relevant converter for this LensAstNode.
            switch (lensAstNode.key) {
                case "sample_kind":
                    beaconFilter = new AstNodeConverterSampleType().convert(lensAstNode);
                    break;
            }

        return beaconFilter;
    }
}
