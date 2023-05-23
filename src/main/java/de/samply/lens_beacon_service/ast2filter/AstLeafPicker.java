package de.samply.lens_beacon_service.ast2filter;

import de.samply.lens_beacon_service.lens.AstNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Crawl over a supplied AstNode object and extract a linear list
 * of leaf AstNode objects.
 *
 * This effectively flattens out the hierarchical structure of AstNode.
 */
@Slf4j
public class AstLeafPicker {
    /**
     * Crawls into the hierarchy of the AstNode object, depth first, and adds
     * some of the leaf nodes it finds to a list, which is then returned.
     *
     * Lists of children of a AstNode object with an OR operand are
     * truncated at element 0, i.e. only the 0th element is taken and added to
     * the leaf node list.
     *
     * Similarly, if values are of type List, then only the first list element
     * is retained, the others are discarded.
     *
     * @param astNode
     * @return
     */
    public List<AstNode> crawl(AstNode astNode) {
        List<AstNode> astNodeLeafNodeList = new ArrayList<AstNode>();

        if (astNode.children == null || astNode.children.size() == 0)
            // Terminate recursion
            try {
                AstNode clonedAstNode = (AstNode) astNode.clone();
                // Keep only the first value, if value is a list
                if (clonedAstNode.value != null && clonedAstNode.value instanceof List && ((List) clonedAstNode.value).size() > 1) {
                    Object valueFirstElement = ((List) clonedAstNode.value).get(0);
                    clonedAstNode.value = new ArrayList();
                    ((List) clonedAstNode.value).add(valueFirstElement);
                }
                astNodeLeafNodeList.add(clonedAstNode);
            } catch (CloneNotSupportedException e) {
                log.error("Error while cloning a AstNode object.", e);
            }
        else {
            if (astNode.operand != null && astNode.operand.equals("AND"))
                for (AstNode childAstNode : astNode.children) {
                    List<AstNode> childAstNodeLeafNodeList = crawl(childAstNode);
                    astNodeLeafNodeList.addAll(childAstNodeLeafNodeList);
                }
            else
                astNodeLeafNodeList = crawl(astNode.children.get(0));
        }

        return astNodeLeafNodeList;
    }
}
