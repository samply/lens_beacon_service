package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;

/**
 * Generate the diagnosis group for the measure report.
 */

public class DiagnosisGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        return generate("diagnosis");
    }
}
