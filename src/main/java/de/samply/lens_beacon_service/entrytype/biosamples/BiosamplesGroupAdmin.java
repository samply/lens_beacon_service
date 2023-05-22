package de.samply.lens_beacon_service.entrytype.biosamples;

import de.samply.lens_beacon_service.measurereport.GroupAdmin;

import java.util.Map;

/**
 * Generate the specimen group for the measure report.
 */

public class BiosamplesGroupAdmin extends GroupAdmin {
    private final String STRATIFIER_SAMPLE_KIND = "sample_kind";

    /**
     * Generate group with all counts set to default initial values.
     *
     * @return The group object.
     */
    public void init() {
        super.init("specimen");
        group.getStratifier().add(createStratifier(STRATIFIER_SAMPLE_KIND));
    }

    /**
     * Set the counts for the various biosample types known to Beam.
     *
     * @param counts
     */
    public void setBiosampleTypeCounts(Map<String, Integer> counts) {
        setStratifierCounts(counts, STRATIFIER_SAMPLE_KIND);
    }
}
