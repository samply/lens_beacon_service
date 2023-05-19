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
        init("genetics");
    }
}
