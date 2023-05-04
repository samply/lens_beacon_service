package de.samply.lens_beacon_service.measurereport;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static Map<String, String> ethnicityNameNcit;
    static {
        ethnicityNameNcit = new HashMap<String, String>();

        ethnicityNameNcit.put("African", "NCIT:C42331");
        ethnicityNameNcit.put("British", "NCIT:C41261");
        ethnicityNameNcit.put("Asian or Asian British", "NCIT:C41260");
        ethnicityNameNcit.put("White and Asian", "NCIT:C67109");
        ethnicityNameNcit.put("White and Black African", "NCIT:C67109");
        ethnicityNameNcit.put("Any other Asian background", "NCIT:C67109");
        ethnicityNameNcit.put("White and Black Caribbean", "NCIT:C67109");
        ethnicityNameNcit.put("Black or Black British", "NCIT:C16352");
        ethnicityNameNcit.put("Any other mixed background", "NCIT:C67109");
        ethnicityNameNcit.put("Bangladeshi", "NCIT:C41260");
        ethnicityNameNcit.put("Any other Black background", "NCIT:C67109");
        ethnicityNameNcit.put("Irish", "NCIT:C43856");
        ethnicityNameNcit.put("Mixed", "NCIT:C67109");
        ethnicityNameNcit.put("White", "NCIT:C41261");
        ethnicityNameNcit.put("Pakistani", "NCIT:C41260");
        ethnicityNameNcit.put("Caribbean", "NCIT:C77810");
        ethnicityNameNcit.put("Other ethnic group", "NCIT:C67109");
        ethnicityNameNcit.put("Indian", "NCIT:C67109");
        ethnicityNameNcit.put("Any other white background", "NCIT:C67109");
        ethnicityNameNcit.put("Chinese", "NCIT:C41260");
    }

    public static Map<String, String> getEthnicityNameNcit() {
        return ethnicityNameNcit;
    }

    public static MeasureReport.StratifierGroupComponent createStratum(String code, int count) {
        MeasureReport.StratifierGroupComponent stratum = new MeasureReport.StratifierGroupComponent();
        stratum.setValue(createTextCodeableConcept(code));
        stratum.addPopulation(createStratifierPopulation(count));

        return stratum;
    }

    public static CodeableConcept createTextCodeableConcept(String text) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(text);

        return codeableConcept;
    }

    private static MeasureReport.StratifierGroupPopulationComponent createStratifierPopulation(int count) {
        MeasureReport.StratifierGroupPopulationComponent population = new MeasureReport.StratifierGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
    }
}
