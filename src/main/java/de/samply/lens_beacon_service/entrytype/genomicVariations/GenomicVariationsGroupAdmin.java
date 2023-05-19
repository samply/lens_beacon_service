package de.samply.lens_beacon_service.entrytype.genomicVariations;

import de.samply.lens_beacon_service.measurereport.GroupAdmin;
import org.hl7.fhir.r4.model.MeasureReport;

/**
 * Generate the genomicVariations group for the measure report.
 */

public class GenomicVariationsGroupAdmin extends GroupAdmin {
    /**
     * Generate group with all counts set to default initial values.
     *
     * @return The group object.
     */
    public MeasureReport.MeasureReportGroupComponent generate() {
        return generate("genetics");
    }
}
