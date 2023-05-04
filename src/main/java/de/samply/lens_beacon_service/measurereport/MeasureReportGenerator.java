package de.samply.lens_beacon_service.measurereport;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.samply.lens_beacon_service.measurereport.group.DiagnosisGroupGenerator;
import de.samply.lens_beacon_service.measurereport.group.GeneticsGroupGenerator;
import de.samply.lens_beacon_service.measurereport.group.PatientsGroupGenerator;
import de.samply.lens_beacon_service.measurereport.group.SpecimenGroupGenerator;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates a single FHIR measure report for Lens.
 *
 * Which groups get added to this report is hard-coded into the constructor.
 *
 * Several setter methods are provided to allow counts for various measures to
 * be set, e.g. patient count or specimen count.
 */
@Slf4j
public class MeasureReportGenerator extends MeasureReport {
    private MeasureReport measureReport = new MeasureReport();

    public MeasureReportGenerator() {
        measureReport.setStatus(MeasureReport.MeasureReportStatus.COMPLETE);
        measureReport.setType(MeasureReport.MeasureReportType.SUMMARY);
        measureReport.setDate(new Date());
        measureReport.setMeasure("urn:uuid:" + UUID.randomUUID());
        measureReport.addExtension(createExtension());
        measureReport.setPeriod(createPeriod());
        measureReport.addGroup(new PatientsGroupGenerator().generate());
        measureReport.addGroup(new DiagnosisGroupGenerator().generate());
        measureReport.addGroup(new SpecimenGroupGenerator().generate());
        measureReport.addGroup(new GeneticsGroupGenerator().generate());
    }

    /**
     * Set the total number of patients for this measure report.
     *
     * @param count Patient count.
     */
    public void setPatientCount(int count) {
        setGroupPopulationCount("patients", count);
    }

    public void setPatientGenderCounts(int femaleCount, int maleCount) {
        MeasureReportGroupComponent group = getGroup("patients");
        List<MeasureReportGroupStratifierComponent> stratifierComponents = group.getStratifier();
        for (MeasureReportGroupStratifierComponent stratifierComponent: stratifierComponents) {
            CodeableConcept code = stratifierComponent.getCode().get(0);
            String text = code.getText();
            if (text.equals("Gender")) {
                stratifierComponent.addStratum(Utils.createStratum("female", femaleCount));
                stratifierComponent.addStratum(Utils.createStratum("male", maleCount));
                break;
            }
        }
    }

    public void setPatientEthnicityCounts(Map<String, Integer> ethnicityCounts) {
        MeasureReportGroupComponent group = getGroup("patients");
        List<MeasureReportGroupStratifierComponent> stratifierComponents = group.getStratifier();
        for (MeasureReportGroupStratifierComponent stratifierComponent: stratifierComponents) {
            CodeableConcept code = stratifierComponent.getCode().get(0);
            String text = code.getText();
            if (text.equals("Ethnicity")) {
                for (String ethnicity: ethnicityCounts.keySet())
                    stratifierComponent.addStratum(Utils.createStratum(ethnicity, ethnicityCounts.get(ethnicity)));
                break;
            }
        }
    }

    /**
     * Set the total number of specimens for this measure report.
     *
     * @param count Specimen count.
     */
    public void setSpecimenCount(int count) {
        setGroupPopulationCount("specimen", count);
    }

    public void setSpecimenTypeCounts(int bloodCount, int bloodSerumCount, int bloodPlasmaCount, int lymphCount) {
        MeasureReportGroupComponent group = getGroup("specimen");
        List<MeasureReportGroupStratifierComponent> stratifierComponents = group.getStratifier();
        for (MeasureReportGroupStratifierComponent stratifierComponent: stratifierComponents) {
            CodeableConcept code = stratifierComponent.getCode().get(0);
            String text = code.getText();
            log.info("setSpecimenTypeCounts: text: " + text);
            if (text.equals("sample_kind")) {
                stratifierComponent.addStratum(Utils.createStratum("blood", bloodCount));
                stratifierComponent.addStratum(Utils.createStratum("blood-plasma", bloodPlasmaCount));
                stratifierComponent.addStratum(Utils.createStratum("blood-serum", bloodSerumCount));
                stratifierComponent.addStratum(Utils.createStratum("lymph", lymphCount));
                break;
            }
        }
    }

    /**
     * Set the total number of genomic variations for this measure report.
     *
     * @param count Genomic variation count.
     */
    public void setGenomicVariationCount(int count) {
        setGroupPopulationCount("genetics", count);
    }

    /**
     * Convert the measure report into a JSON-formatted string.
     *
     * @return JSON-formatted string.
     */
    @Override
    public String toString() {
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        String stringJsonMeasureReport = parser.encodeResourceToString(measureReport);

        return stringJsonMeasureReport;
    }

    /**
     * Set the population count in the first population in the named group.
     *
     * @param groupName Name of group.
     * @param count Population count.
     */
    public void setGroupPopulationCount(String groupName, int count) {
        MeasureReportGroupComponent group = getGroup(groupName);
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
}
