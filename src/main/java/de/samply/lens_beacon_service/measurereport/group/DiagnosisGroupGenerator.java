package de.samply.lens_beacon_service.measurereport.group;

import de.samply.lens_beacon_service.measurereport.Utils;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        group.setCode(Utils.createTextCodeableConcept("diagnosis"));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createNullStratifier());
        group.setStratifier(stratifiers);

        return group;
    }
}
