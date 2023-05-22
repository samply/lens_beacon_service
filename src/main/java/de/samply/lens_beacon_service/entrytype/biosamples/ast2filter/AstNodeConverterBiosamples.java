package de.samply.lens_beacon_service.entrytype.biosamples.ast2filter;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeConverter;
import de.samply.lens_beacon_service.entrytype.biosamples.NameOntologyMaps;
import de.samply.lens_beacon_service.lens.AstNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert the Lens AST representation of sample type into a corresponding Beacon filter.
 */

@Slf4j
public class AstNodeConverterBiosamples extends AstNodeConverter {
    @Override
    public BeaconFilter convert(AstNode astNode) {
//        if (((List) astNode.value).get(0).equals("blood"))
//            return new BeaconFilter("id", "UBERON:0000178");
//        if (((List) astNode.value).get(0).equals("blood-serum"))
//            return new BeaconFilter("id", "UBERON:0001977");
//        if (((List) astNode.value).get(0).equals("blood-plasma"))
//            return new BeaconFilter("id", "UBERON:0001969");
//        if (((List) astNode.value).get(0).equals("lymph"))
//            return new BeaconFilter("id", "UBERON:0002391");
//        return null;
        return(convert(astNode, NameOntologyMaps.biosmapleTypeUberon));
    }
}
