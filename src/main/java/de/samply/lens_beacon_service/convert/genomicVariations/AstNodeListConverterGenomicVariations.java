package de.samply.lens_beacon_service.convert.genomicVariations;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeListConverter;
import de.samply.lens_beacon_service.lens.AstNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert a list of AstNode leaf elements into a list of Beacon filters for biosamples.
 */

@Slf4j
public class AstNodeListConverterGenomicVariations extends AstNodeListConverter {
    @Override
    public BeaconFilter convertSingleAstNode(AstNode astNode) {
        BeaconFilter beaconFilter = null;
        if (astNode.key != null)
            // Choose the relevant converter for this AstNode.
            switch (astNode.key) {
                case "genomic_variation":
                    beaconFilter = new AstNodeConverterGenomicVariations().convert(astNode);
                    break;
            }

        return beaconFilter;
    }
}