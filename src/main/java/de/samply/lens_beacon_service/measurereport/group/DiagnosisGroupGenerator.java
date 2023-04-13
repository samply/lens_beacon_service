package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        MeasureReport.MeasureReportGroupComponent group = new MeasureReport.MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("diagnosis"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createNullStratifier());
        group.setStratifier(stratifiers);

        return group;
    }
}
