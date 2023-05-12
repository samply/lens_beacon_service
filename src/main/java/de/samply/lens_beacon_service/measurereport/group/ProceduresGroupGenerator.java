package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;

/**
 * Generate the procedures group for the measure report.
 */

public class ProceduresGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        return generate("procedures", "ProcedureType");
    }
}
