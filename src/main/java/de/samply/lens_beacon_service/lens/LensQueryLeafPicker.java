package de.samply.lens_beacon_service.lens;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Crawl over a supplied LensQuery object and extract a linear list
 * of leaf LensQuery objects.
 *
 * This effectively flattens out the hierarchical structure of LensQuery.
 */
@Slf4j
public class LensQueryLeafPicker {
    /**
     * Crawls into the hierarchy of the LensQuery object, depth first, and adds
     * some of the leaf nodes it finds to a list, which is then returned.
     *
     * Lists of children of a LensQuery object with an OR operand are
     * truncated at element 0, i.e. only the 0th element is taken and added to
     * the leaf node list.
     *
     * Similarly, if values are of type List, then only the first list element
     * is retained, the others are discarded.
     *
     * @param lensQuery
     * @return
     */
    public List<LensQuery> crawl(LensQuery lensQuery) {
        List<LensQuery> lensQueryLeafNodeList = new ArrayList<LensQuery>();

        if (lensQuery.children == null || lensQuery.children.size() == 0)
            // Terminate recursion
            try {
                LensQuery clonedLensQuery = (LensQuery) lensQuery.clone();
                // Keep only the first value, if value is a list
                if (clonedLensQuery.value != null && clonedLensQuery.value instanceof List && ((List)clonedLensQuery.value).size() > 1) {
                    Object valueFirstElement = ((List)clonedLensQuery.value).get(0);
                    clonedLensQuery.value = new ArrayList();
                    ((List)clonedLensQuery.value).add(valueFirstElement);
                }
                lensQueryLeafNodeList.add(clonedLensQuery);
            } catch (CloneNotSupportedException e) {
                log.error("Error while cloning a LensQuery object.", e);
            }
        else {
            if (lensQuery.operand != null && lensQuery.operand.equals("AND"))
                for (LensQuery childLensQuery: lensQuery.children) {
                    List<LensQuery> childLensQueryLeafNodeList = crawl(childLensQuery);
                    lensQueryLeafNodeList.addAll(childLensQueryLeafNodeList);
                }
            else
                lensQueryLeafNodeList = crawl(lensQuery.children.get(0));
        }

        return lensQueryLeafNodeList;
    }
}
