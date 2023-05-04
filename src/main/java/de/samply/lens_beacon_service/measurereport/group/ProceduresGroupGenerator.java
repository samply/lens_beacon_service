package de.samply.lens_beacon_service.measurereport.group;

import de.samply.lens_beacon_service.measurereport.Utils;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

public class ProceduresGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        group.setCode(Utils.createTextCodeableConcept("procedures"));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("ProcedureType"));
        group.setStratifier(stratifiers);

        return group;
    }
}
