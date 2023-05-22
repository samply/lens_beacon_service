package de.samply.lens_beacon_service.entrytype.genomicVariations.ast2filter;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.convert.AstNodeConverter;
import de.samply.lens_beacon_service.lens.AstNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert the Lens AST representation of a genomic variations into a corresponding Beacon filter.
 */

@Slf4j
public class AstNodeConverterGenomicVariations extends AstNodeConverter {
    @Override
    public BeaconFilter convert(AstNode astNode) {
        String value = (String) astNode.value;
        // Remove non-ASCII stuff from filter term, because Beacon does not
        // like it. Also prepend an identifier type, because Beacon wants this.
        value = value.replace(":", "");
        value = value.replace(".", "");
        value = value.replace(">", "");
        value = "HGVSid:" + value;
        return new BeaconFilter("id", value);
    }
}
