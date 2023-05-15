package de.samply.lens_beacon_service.convert.genetics;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeConverter;
import de.samply.lens_beacon_service.lens.LensAstNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert the Lens AST representation of a genetic variant into a corresponding Beacon filter.
 */

@Slf4j
public class AstNodeConverterGeneticVariation implements AstNodeConverter {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        String geneticVariation = (String) lensAstNode.value;
        // Remove non-ASCII stuff from filter term, because Beacon does not
        // like it. Also prepend an identifier type, because Beacon wants this.
        geneticVariation = geneticVariation.replace(":", "");
        geneticVariation = geneticVariation.replace(".", "");
        geneticVariation = geneticVariation.replace(">", "");
        geneticVariation = "HGVSid:" + geneticVariation;
        return new BeaconFilter("id", geneticVariation);
    }
}
