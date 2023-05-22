package de.samply.lens_beacon_service.measurereport;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Groups are the first-level subcomponents of a measure report, e.g. individuals, biosamples,
 * genomic variants, etc.
 *
 * This is the base class for generating a new group.
 *
 * The pricipal method of interest is generate(), which generates a new object of class
 * MeasureReportGroupComponent, that can be added to a measure report.
 *
 * I had to use a generator class rather than subclassing from MeasureReportGroupComponent
 * because JSON generation breaks if you use subclasses.
 */
@Slf4j
public abstract class GroupAdmin {
    public MeasureReport.MeasureReportGroupComponent group;

    public GroupAdmin() {
        group = new MeasureReport.MeasureReportGroupComponent();
        group.setPopulation(createPopulations(-1));
        init();
    }

    public abstract void init();

    /**
     * Generate group with all counts set to default initial values.
     *
     * @return The group object.
     */
    protected void init(String groupName) {
        group.setCode(createTextCodeableConcept(groupName));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        group.setStratifier(stratifiers);
    }

    /**
     * Set the total population for this group.
     *
     * @param count Polulation count.
     */
    public void setCount(int count) {
        List<MeasureReport.MeasureReportGroupPopulationComponent> populations = group.getPopulation();
        if (populations == null || populations.size() == 0)
            return;
        // Set the population count in the first population in this group.
        populations.get(0).setCount(count);
    }

    protected List<MeasureReport.MeasureReportGroupPopulationComponent> createPopulations(int count) {
        List<MeasureReport.MeasureReportGroupPopulationComponent> populations = new ArrayList<MeasureReport.MeasureReportGroupPopulationComponent>();
        populations.add(createMeasureReportPopulation(count));

        return populations;
    }

    protected MeasureReport.MeasureReportGroupStratifierComponent createNullStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("null");
        stratifierComponent.addStratum(createStratum("null", 0));

        return  stratifierComponent;
    }

    protected MeasureReport.MeasureReportGroupStratifierComponent createStratifier(String stratifierName) {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = new MeasureReport.MeasureReportGroupStratifierComponent();
        stratifierComponent.setCode(createTextCodeableConceptList(stratifierName));

        return  stratifierComponent;
    }

    /**
     * Create a stratum with the given name and the given stratifier population count.
     *
     * @param code The name to give to the stratum.
     * @param count Population count.
     * @return New stratum.
     */
    protected MeasureReport.StratifierGroupComponent createStratum(String code, int count) {
        MeasureReport.StratifierGroupComponent stratum = new MeasureReport.StratifierGroupComponent();
        stratum.setValue(createTextCodeableConcept(code));
        stratum.addPopulation(createStratifierPopulation(count));

        return stratum;
    }

    /**
     * Create a text-codeable-concept with the given text.
     * @param text Text of concept.
     * @return New Codeable concept.
     */
    protected  CodeableConcept createTextCodeableConcept(String text) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(text);

        return codeableConcept;
    }
    /**
     * Add counts to the named stratifier.
     *
     * @param counts
     */
    protected void setStratifierCounts(Map<String, Integer> counts, String stratifierName) {
        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifierComponents = group.getStratifier();
        for (MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent: stratifierComponents) {
            CodeableConcept code = stratifierComponent.getCode().get(0);
            String text = code.getText();
            if (text.equals(stratifierName)) {
                // Order by key length. This looks better in the Lens histogram.
                for (String ethnicity: counts.keySet().stream()
                        .sorted((str1, str2) -> str1.length() - str2.length())
                        .collect(Collectors.toList()))
                    stratifierComponent.addStratum(createStratum(ethnicity, counts.get(ethnicity)));
                break;
            }
        }
    }


    /**
     * Create a population with the given count.
     *
     * @param count Population count.
     * @return New population.
     */
    private MeasureReport.StratifierGroupPopulationComponent createStratifierPopulation(int count) {
        MeasureReport.StratifierGroupPopulationComponent population = new MeasureReport.StratifierGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
    }

    private List<CodeableConcept> createTextCodeableConceptList(String text) {
        List<CodeableConcept> codeableConceptList = new ArrayList<CodeableConcept>();
        codeableConceptList.add(createTextCodeableConcept(text));

        return codeableConceptList;
    }

    private MeasureReport.MeasureReportGroupPopulationComponent createMeasureReportPopulation(int count) {
        MeasureReport.MeasureReportGroupPopulationComponent population = new MeasureReport.MeasureReportGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
    }
}
