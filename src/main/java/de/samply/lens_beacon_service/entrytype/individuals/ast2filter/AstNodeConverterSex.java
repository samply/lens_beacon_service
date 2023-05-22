package de.samply.lens_beacon_service.entrytype.individuals.ast2filter;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeConverter;
import de.samply.lens_beacon_service.entrytype.individuals.NameOntologyMaps;
import de.samply.lens_beacon_service.lens.AstNode;

/**
 * Convert the Lens AST representation of gender into a corresponding Beacon filter.
 */

public class AstNodeConverterSex extends AstNodeConverter {
    @Override
    public BeaconFilter convert(AstNode astNode) {
//        if (((List) astNode.value).get(0).equals("male"))
//            return new BeaconFilter("id", "NCIT:C20197");
//        if (((List) astNode.value).get(0).equals("female"))
//            return new BeaconFilter("id", "NCIT:C16576");
//        return null;
        return(convert(astNode, NameOntologyMaps.genderNameNcit));
    }
}
