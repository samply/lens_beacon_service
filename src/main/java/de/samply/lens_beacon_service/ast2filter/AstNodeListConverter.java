package de.samply.lens_beacon_service.ast2filter;

import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.lens.AstNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert a list of AstNode leaf elements into a list of Beacon filters.
 */

public abstract class AstNodeListConverter {
    /**
     * Takes an AstNode, representing the root of an AST tree and returns a corresponding
     * list of filters.
     *
     * @param astNode Root node of AST tree.
     * @return List of BeaconFilter objects.
     */
    public List<BeaconFilter> convert(AstNode astNode) {
        List<AstNode> astNodeLeafNodeList = new AstLeafPicker().crawl(astNode);
        return convertFlatAstNodeList(astNodeLeafNodeList);
    }

    /**
     * Takes a flat list of AstNode objects and returns a corresponding list of filters.
     * The AstNode objects should have empty "children" variables (that's the definition of
     * "flat").
     *
     * @param astNodeLeafNodeList List of AstNode objects.
     * @return List of BeaconFilter objects.
     */
    public List<BeaconFilter> convertFlatAstNodeList(List<AstNode> astNodeLeafNodeList) {
        List<BeaconFilter> beaconFilterList = new ArrayList<BeaconFilter>();
        for (AstNode astNode : astNodeLeafNodeList) {
            BeaconFilter beaconFilter = convertSingleAstNode(astNode);
            if (beaconFilter != null)
                beaconFilterList.add(beaconFilter);
        }
        return beaconFilterList;
    }

    /**
     * AstNodeListConverter a single AstNode object into a single Beacon filter.
     *
     * The implementation is going to depend on which Beacon endpoint we will
     * be addressing, so the implementation needs to be done in the superclass.
     *
     * @param astNode Node from AST query.
     * @return single Beacon filter.
     */
    public abstract BeaconFilter convertSingleAstNode(AstNode astNode);
}
