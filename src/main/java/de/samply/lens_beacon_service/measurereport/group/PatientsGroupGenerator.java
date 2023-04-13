package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

public class PatientsGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        MeasureReport.MeasureReportGroupComponent group = new MeasureReport.MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("patients"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createGenderStratifier());
        stratifiers.add(createVitalStatusStratifier());
        stratifiers.add(createAgeStratifier());
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createGenderStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("Gender");
        stratifierComponent.addStratum(createStratum("female", 0));
        stratifierComponent.addStratum(createStratum("male", 0));

        return  stratifierComponent;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createVitalStatusStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("75186-7");
        stratifierComponent.addStratum(createStratum("unbekannt", 0));

        return  stratifierComponent;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createAgeStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("Age");
        stratifierComponent.addStratum(createStratum("20", 0));
        stratifierComponent.addStratum(createStratum("30", 0));
        stratifierComponent.addStratum(createStratum("40", 0));
        stratifierComponent.addStratum(createStratum("50", 0));
        stratifierComponent.addStratum(createStratum("60", 0));
        stratifierComponent.addStratum(createStratum("70", 0));

        return  stratifierComponent;
    }
}
