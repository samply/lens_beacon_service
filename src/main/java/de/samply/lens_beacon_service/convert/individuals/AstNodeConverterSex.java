package de.samply.lens_beacon_service.convert.individuals;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeConverter;
import de.samply.lens_beacon_service.lens.LensAstNode;

import java.util.List;

/**
 * Convert the Lens AST representation of gender into a corresponding Beacon filter.
 */

public class AstNodeConverterSex implements AstNodeConverter {
    @Override
    public BeaconFilter convert(LensAstNode lensAstNode) {
        if (((List) lensAstNode.value).get(0).equals("male"))
            return new BeaconFilter("id", "NCIT:C20197");
        if (((List) lensAstNode.value).get(0).equals("female"))
            return new BeaconFilter("id", "NCIT:C16576");
        return null;
    }
}
