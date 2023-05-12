package de.samply.lens_beacon_service.measurereport.group;

import de.samply.lens_beacon_service.measurereport.MeasureReportUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups are the first-level subcomponents of a measure report, e.g. patients, diagnoses,
 * speciments, etc.
 *
 * This is the base class for generating a new group.
 *
 * The pricipal method of interest is generate(), which generates a new object of class
 * MeasureReportGroupComponent, that can be added to a measure report.
 *
 * I had to use a generator class rather than subclassing from MeasureReportGroupComponent
 * because JSON generation breaks if you use subclasses.
 */
public abstract class GroupGenerator {
    protected MeasureReport.MeasureReportGroupComponent group;

    public GroupGenerator() {
        group = new MeasureReport.MeasureReportGroupComponent();
        group.setPopulation(createPopulations(-1));
    }

    /**
     * Generate a new MeasureReportGroupComponent object.
     * @return
     */
    public abstract MeasureReport.MeasureReportGroupComponent generate();

    protected  MeasureReport.MeasureReportGroupComponent generate(String groupName) {
        return generate(groupName, null);
    }

    protected  MeasureReport.MeasureReportGroupComponent generate(String groupName, String stratifierName) {
        group.setCode(MeasureReportUtils.createTextCodeableConcept(groupName));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        if (stratifierName == null)
            stratifiers.add(createNullStratifier());
        else
            stratifiers.add(createStratifier(stratifierName));
        group.setStratifier(stratifiers);

        return group;
    }

    protected List<MeasureReport.MeasureReportGroupPopulationComponent> createPopulations(int count) {
        List<MeasureReport.MeasureReportGroupPopulationComponent> populations = new ArrayList<MeasureReport.MeasureReportGroupPopulationComponent>();
        populations.add(createMeasureReportPopulation(count));

        return populations;
    }

    protected MeasureReport.MeasureReportGroupStratifierComponent createNullStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("diagnosis");
        stratifierComponent.addStratum(MeasureReportUtils.createStratum("null", 0));

        return  stratifierComponent;
    }

    protected MeasureReport.MeasureReportGroupStratifierComponent createStratifier(String stratifierName) {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = new MeasureReport.MeasureReportGroupStratifierComponent();
        stratifierComponent.setCode(createTextCodeableConceptList(stratifierName));

        return  stratifierComponent;
    }

    private List<CodeableConcept> createTextCodeableConceptList(String text) {
        List<CodeableConcept> codeableConceptList = new ArrayList<CodeableConcept>();
        codeableConceptList.add(MeasureReportUtils.createTextCodeableConcept(text));

        return codeableConceptList;
    }

    private MeasureReport.MeasureReportGroupPopulationComponent createMeasureReportPopulation(int count) {
        MeasureReport.MeasureReportGroupPopulationComponent population = new MeasureReport.MeasureReportGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
    }
}
