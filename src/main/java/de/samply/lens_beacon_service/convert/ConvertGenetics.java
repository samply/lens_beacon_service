package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensAstNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert a list of LensAstNode leaf elements into a list of Beacon filters for biosamples.
 */

@Slf4j
public class ConvertGenetics extends Convert {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        BeaconFilter beaconFilter = null;
        if (lensAstNode.key != null)
            // Choose the relevant converter for this LensAstNode.
            switch (lensAstNode.key) {
                case "genomic_variation":
                    beaconFilter = new GeneticVariationConverter().convert(lensAstNode);
                    break;
            }

        return beaconFilter;
    }
}
