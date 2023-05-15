package de.samply.lens_beacon_service.convert;

import de.samply.lens_beacon_service.lens.LensAstNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Crawl over a supplied LensAstNode object and extract a linear list
 * of leaf LensAstNode objects.
 *
 * This effectively flattens out the hierarchical structure of LensAstNode.
 */
@Slf4j
public class AstLeafPicker {
    /**
     * Crawls into the hierarchy of the LensAstNode object, depth first, and adds
     * some of the leaf nodes it finds to a list, which is then returned.
     *
     * Lists of children of a LensAstNode object with an OR operand are
     * truncated at element 0, i.e. only the 0th element is taken and added to
     * the leaf node list.
     *
     * Similarly, if values are of type List, then only the first list element
     * is retained, the others are discarded.
     *
     * @param lensAstNode
     * @return
     */
    public List<LensAstNode> crawl(LensAstNode lensAstNode) {
        List<LensAstNode> lensAstNodeLeafNodeList = new ArrayList<LensAstNode>();

        if (lensAstNode.children == null || lensAstNode.children.size() == 0)
            // Terminate recursion
            try {
                LensAstNode clonedLensAstNode = (LensAstNode) lensAstNode.clone();
                // Keep only the first value, if value is a list
                if (clonedLensAstNode.value != null && clonedLensAstNode.value instanceof List && ((List) clonedLensAstNode.value).size() > 1) {
                    Object valueFirstElement = ((List) clonedLensAstNode.value).get(0);
                    clonedLensAstNode.value = new ArrayList();
                    ((List) clonedLensAstNode.value).add(valueFirstElement);
                }
                lensAstNodeLeafNodeList.add(clonedLensAstNode);
            } catch (CloneNotSupportedException e) {
                log.error("Error while cloning a LensAstNode object.", e);
            }
        else {
            if (lensAstNode.operand != null && lensAstNode.operand.equals("AND"))
                for (LensAstNode childLensAstNode : lensAstNode.children) {
                    List<LensAstNode> childLensAstNodeLeafNodeList = crawl(childLensAstNode);
                    lensAstNodeLeafNodeList.addAll(childLensAstNodeLeafNodeList);
                }
            else
                lensAstNodeLeafNodeList = crawl(lensAstNode.children.get(0));
        }

        return lensAstNodeLeafNodeList;
    }
}
