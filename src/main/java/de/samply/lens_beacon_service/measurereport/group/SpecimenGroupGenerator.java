package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;

/**
 * Generate the specimen group for the measure report.
 */

public class SpecimenGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        return generate("specimen", "sample_kind");
    }
}
