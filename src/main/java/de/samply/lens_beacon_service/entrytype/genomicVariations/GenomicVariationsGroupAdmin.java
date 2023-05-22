package de.samply.lens_beacon_service.entrytype.genomicVariations;

import de.samply.lens_beacon_service.measurereport.GroupAdmin;

/**
 * Generate the genomicVariations group for the measure report.
 */

public class GenomicVariationsGroupAdmin extends GroupAdmin {
    /**
     * Generate group with all counts set to default initial values.
     *
     * @return The group object.
     */
    public void init() {
        super.init("genetics");
        // Lens seems to like to have at least one stratifier, even if it is unused.
        group.getStratifier().add(createNullStratifier());
    }
}
