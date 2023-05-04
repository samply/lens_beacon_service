package de.samply.lens_beacon_service.measurereport.group;

import de.samply.lens_beacon_service.measurereport.Utils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

public abstract class GroupGenerator {
    protected MeasureReport.MeasureReportGroupComponent group;

    public GroupGenerator() {
        group = new MeasureReport.MeasureReportGroupComponent();
        group.setPopulation(createPopulations(-1));
    }

    public abstract MeasureReport.MeasureReportGroupComponent generate();

    protected List<MeasureReport.MeasureReportGroupPopulationComponent> createPopulations(int count) {
        List<MeasureReport.MeasureReportGroupPopulationComponent> populations = new ArrayList<MeasureReport.MeasureReportGroupPopulationComponent>();
        populations.add(createMeasureReportPopulation(count));

        return populations;
    }

    protected MeasureReport.MeasureReportGroupStratifierComponent createNullStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("diagnosis");
        stratifierComponent.addStratum(Utils.createStratum("null", 0));

        return  stratifierComponent;
    }

    protected MeasureReport.MeasureReportGroupStratifierComponent createStratifier(String stratifierName) {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = new MeasureReport.MeasureReportGroupStratifierComponent();
        stratifierComponent.setCode(createTextCodeableConceptList(stratifierName));

        return  stratifierComponent;
    }

    private List<CodeableConcept> createTextCodeableConceptList(String text) {
        List<CodeableConcept> codeableConceptList = new ArrayList<CodeableConcept>();
        codeableConceptList.add(Utils.createTextCodeableConcept(text));

        return codeableConceptList;
    }

    private MeasureReport.MeasureReportGroupPopulationComponent createMeasureReportPopulation(int count) {
        MeasureReport.MeasureReportGroupPopulationComponent population = new MeasureReport.MeasureReportGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
    }
}
