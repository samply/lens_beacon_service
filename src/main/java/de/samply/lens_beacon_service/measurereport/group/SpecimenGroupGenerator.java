package de.samply.lens_beacon_service.measurereport.group;

import de.samply.lens_beacon_service.measurereport.Utils;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

public class SpecimenGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        group.setCode(Utils.createTextCodeableConcept("specimen"));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("sample_kind"));
        group.setStratifier(stratifiers);

        return group;
    }
}
