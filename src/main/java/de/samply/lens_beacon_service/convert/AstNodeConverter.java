package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.lens.AstNode;

import java.util.List;
import java.util.Map;

/**
 * Generic template for converting a single Lens AST node into a Beacon 2 filter.
 */
public abstract class AstNodeConverter {
    /**
     * Convert the supplied AST node to a Beacon filter. This should be a leaf node;
     * children will be ignored.
     *
     * @param astNode
     * @return
     */
    public abstract BeaconFilter convert(AstNode astNode);

    public BeaconFilter convert(AstNode astNode, Map<String, String> nameOntologyMap) {
        String ontologyTerm = nameOntologyMap.get(((List) astNode.value).get(0));
        BeaconFilter beaconFilter = new BeaconFilter("id", ontologyTerm);
        return beaconFilter;
    }
}
