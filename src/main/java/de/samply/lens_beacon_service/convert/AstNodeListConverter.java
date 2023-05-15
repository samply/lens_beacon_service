package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.lens.LensAstNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert a list of LensAstNode leaf elements into a list of Beacon filters.
 */

public abstract class AstNodeListConverter {
    /**
     * Takes a flat list of LensAstNode objects and returns a corresponding list of filters.
     * The LensAstNode objects should have empty "children" variables (that's the definition of
     * "flat").
     *
     * @param lensAstNodeLeafNodeList List of LensAstNode objects.
     * @return List of BeaconFilter objects.
     */
    public List<BeaconFilter> convert(List<LensAstNode> lensAstNodeLeafNodeList) {
        List<BeaconFilter> beaconFilterList = new ArrayList<BeaconFilter>();
        for (LensAstNode lensAstNode : lensAstNodeLeafNodeList) {
            BeaconFilter beaconFilter = convert(lensAstNode);
            if (beaconFilter != null)
                beaconFilterList.add(beaconFilter);
        }
        return beaconFilterList;
    }

    /**
     * AstNodeListConverter a single LensAstNode object into a single Beacon filter.
     *
     * The implementation is going to depend on which Beacon endpoint we will
     * be addressing, so the implementation needs to be done in the superclass.
     *
     * @param lensAstNode Node from AST query.
     * @return single Beacon filter.
     */
    public abstract BeaconFilter convert(LensAstNode lensAstNode);
}
