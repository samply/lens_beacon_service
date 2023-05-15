package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;

/**
 * Generate the medication statements group for the measure report.
 */

public class MedicationStatementsGroupAdmin extends GroupAdmin {
    /**
     * Generate group with all counts set to default initial values.
     *
     * @return The group object.
     */
    public MeasureReport.MeasureReportGroupComponent generate() {
        return generate("medicationStatements", "MedicationType");
    }
}