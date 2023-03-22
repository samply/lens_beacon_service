package de.samply.lens_beacon_service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.MeasureReport.MeasureReportGroupComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

public class MeasureReportGenerator {
    private MeasureReport measureReport = new MeasureReport();

    public MeasureReportGenerator() {
        measureReport.setStatus(MeasureReport.MeasureReportStatus.COMPLETE);
        measureReport.setType(MeasureReport.MeasureReportType.SUMMARY);
        measureReport.setDate(new Date());
        measureReport.setMeasure("urn:uuid:533741bb-6176-41ea-b5e7-16f5a3742802");
        measureReport.addExtension(createExtension());
        measureReport.setPeriod(createPeriod());
        measureReport.addGroup(createPatientsGroup());
        measureReport.addGroup(createDiagnosisGroup());
        measureReport.addGroup(createSpecimenGroup());
        measureReport.addGroup(createProceduresGroup());
        measureReport.addGroup(createMedicationStatementsGroup());
    }

    public String toJsonString() {
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        String stringJsonMeasureReport = parser.encodeResourceToString(measureReport);

        return stringJsonMeasureReport;
    }

    public void setPatientCount(int count) {
        MeasureReportGroupComponent group = getGroup("patients");
        List<MeasureReport.MeasureReportGroupPopulationComponent> populations = group.getPopulation();
        if (populations == null || populations.size() == 0)
            return;
        populations.get(0).setCount(count);
    }

    /**
     * Find the group with the supplied name.
     *
     * @param name Group name.
     * @return The group found, if one exists, otherwise null.
     */
    private MeasureReportGroupComponent getGroup(String name) {
        MeasureReportGroupComponent group = null;

        for (MeasureReportGroupComponent testGroup: measureReport.getGroup())
            if (testGroup.getCode().getText().equals(name)) {
                group = testGroup;
                break;
            }

        return group;
    }

    private Extension createExtension() {
        Extension extension = new Extension("https://samply.github.io/blaze/fhir/StructureDefinition/eval-duration");
        Quantity quantity = new Quantity();
        quantity.setCode("s");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setUnit("s");
        quantity.setValue(new BigDecimal(0.3398866));
        extension.setValue(quantity);

        return extension;
    }

    private Period createPeriod() {
        Period period = new Period();
        Date startDate = new Date();
        startDate.setYear(2000);
        period.setStart(startDate);
        Date endDate = new Date();
        endDate.setYear(2030);
        period.setEnd(endDate);

        return period;
    }

    private MeasureReportGroupComponent createPatientsGroup() {
        MeasureReportGroupComponent group = new MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("patients"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createGenderStratifier());
        stratifiers.add(createVitalStatusStratifier());
        stratifiers.add(createAgeStratifier());
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReportGroupComponent createDiagnosisGroup() {
        MeasureReportGroupComponent group = new MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("diagnosis"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createNullStratifier());
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReportGroupComponent createSpecimenGroup() {
        MeasureReportGroupComponent group = new MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("specimen"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createNullStratifier());
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReportGroupComponent createProceduresGroup() {
        MeasureReportGroupComponent group = new MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("procedures"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("ProcedureType"));
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReportGroupComponent createMedicationStatementsGroup() {
        MeasureReportGroupComponent group = new MeasureReportGroupComponent();

        group.setCode(createTextCodeableConcept("medicationStatements"));
        group.setPopulation(createPopulations(0));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("MedicationType"));
        group.setStratifier(stratifiers);

        return group;
    }

    private List<MeasureReport.MeasureReportGroupPopulationComponent> createPopulations(int count) {
        List<MeasureReport.MeasureReportGroupPopulationComponent> populations = new ArrayList<MeasureReport.MeasureReportGroupPopulationComponent>();
        populations.add(createMeasureReportPopulation(count));

        return populations;
    }

    private MeasureReport.MeasureReportGroupPopulationComponent createMeasureReportPopulation(int count) {
        MeasureReport.MeasureReportGroupPopulationComponent population = new MeasureReport.MeasureReportGroupPopulationComponent();
        population.setCount(count);
        population.setCode(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/measure-population", "initial-population", null)));

        return population;
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

    private MeasureReport.MeasureReportGroupStratifierComponent createNullStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("diagnosis");
        stratifierComponent.addStratum(createStratum("null", 0));

        return  stratifierComponent;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createSampleKindStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("sample_kind");
        stratifierComponent.addStratum(createStratum("ascites", 0));
        stratifierComponent.addStratum(createStratum("blood-plasma", 0));
        stratifierComponent.addStratum(createStratum("blood-serum", 0));
        stratifierComponent.addStratum(createStratum("bone-marrow", 0));
        stratifierComponent.addStratum(createStratum("buffy-coat", 0));
        stratifierComponent.addStratum(createStratum("cf-dna", 0));
        stratifierComponent.addStratum(createStratum("csf-liquor", 0));
        stratifierComponent.addStratum(createStratum("derivative", 0));
        stratifierComponent.addStratum(createStratum("derivative-other", 0));
        stratifierComponent.addStratum(createStratum("dna", 0));
        stratifierComponent.addStratum(createStratum("liquid", 0));
        stratifierComponent.addStratum(createStratum("liquid-other", 0));
        stratifierComponent.addStratum(createStratum("peripheral-blood-cells-vital", 0));
        stratifierComponent.addStratum(createStratum("rna", 0));
        stratifierComponent.addStratum(createStratum("saliva", 0));
        stratifierComponent.addStratum(createStratum("stool-faeces", 0));
        stratifierComponent.addStratum(createStratum("tissue", 0));
        stratifierComponent.addStratum(createStratum("tissue-formalin", 0));
        stratifierComponent.addStratum(createStratum("tissue-frozen", 0));
        stratifierComponent.addStratum(createStratum("tissue-other", 0));
        stratifierComponent.addStratum(createStratum("tissue-paxgene-or-else", 0));
        stratifierComponent.addStratum(createStratum("urine", 0));
        stratifierComponent.addStratum(createStratum("whole-blood", 0));

        return  stratifierComponent;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createStratifier(String stratifierName) {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = new MeasureReport.MeasureReportGroupStratifierComponent();
        stratifierComponent.setCode(createTextCodeableConceptList(stratifierName));

        return  stratifierComponent;
    }

    private MeasureReport.StratifierGroupComponent createStratum(String code, int count) {
        MeasureReport.StratifierGroupComponent stratum = new MeasureReport.StratifierGroupComponent();
        stratum.setValue(createTextCodeableConcept(code));
        stratum.addPopulation(createStratifierPopulation(count));

        return stratum;
    }

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

    private CodeableConcept createTextCodeableConcept(String text) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(text);

        return codeableConcept;
    }
}
