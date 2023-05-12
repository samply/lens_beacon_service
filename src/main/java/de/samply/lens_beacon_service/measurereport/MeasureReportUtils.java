package de.samply.lens_beacon_service.measurereport;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MeasureReport;

/**
 * Parameters and functions used when generating MeasureReports.
 */
public class MeasureReportUtils {
    /**
     * Create a stratum with the given name and the given stratifier population count.
     *
     * @param code The name to give to the stratum.
     * @param count Population count.
     * @return New stratum.
     */
    public static MeasureReport.StratifierGroupComponent createStratum(String code, int count) {
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
    public static CodeableConcept createTextCodeableConcept(String text) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(text);

        return codeableConcept;
    }

    /**
     * Create a population with the given count.
     *
     * @param count Population count.
     * @return New population.
     */
    private static MeasureReport.StratifierGroupPopulationComponent createStratifierPopulation(int count) {
        MeasureReport.StratifierGroupPopulationComponent population = new MeasureReport.StratifierGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
    }
}
