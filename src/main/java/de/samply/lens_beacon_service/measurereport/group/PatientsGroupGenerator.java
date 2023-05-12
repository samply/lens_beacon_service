package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;
import de.samply.lens_beacon_service.measurereport.MeasureReportUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate the patients group for the measure report.
 */

public class PatientsGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        group.setCode(MeasureReportUtils.createTextCodeableConcept("patients"));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("Gender"));
        stratifiers.add(createVitalStatusStratifier());
        stratifiers.add(createStratifier("Age"));
        stratifiers.add(createStratifier("Ethnicity"));
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createVitalStatusStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("75186-7");
        stratifierComponent.addStratum(MeasureReportUtils.createStratum("unbekannt", 0));

        return  stratifierComponent;
    }
}
